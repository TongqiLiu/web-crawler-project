package com.quant.stockpltr.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quant.stockpltr.crawler.StockPltrCrawler;
import com.quant.stockpltr.model.StockComment;
import com.quant.stockpltr.model.StockPltrData;
import com.quant.stockpltr.repository.StockCommentRepository;
import com.quant.stockpltr.repository.StockPltrDataRepository;

/**
 * StockPltr数据服务
 * 提供股票数据和评论的业务逻辑处理
 */
@Service
public class StockPltrDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPltrDataService.class);
    
    @Autowired
    private StockPltrCrawler stockPltrCrawler;
    
    @Autowired
    private StockPltrDataRepository stockPltrDataRepository;
    
    @Autowired
    private StockCommentRepository stockCommentRepository;
    
    @Value("${stockpltr.crawl.enabled:true}")
    private boolean crawlEnabled;
    
    @Value("${stockpltr.crawl.interval:300}")
    private int crawlInterval; // 爬取间隔（秒）
    
    @Value("${stockpltr.crawl.batch-size:10}")
    private int batchSize; // 批量处理大小
    
    @Value("${stockpltr.data.retention-days:30}")
    private int retentionDays; // 数据保留天数
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * 获取股票数据
     * @param symbol 股票代码
     * @return 股票数据
     */
    public StockPltrData getStockData(String symbol) {
        try {
            logger.info("获取股票数据: {}", symbol);
            
            // 先尝试从数据库获取最新数据
            Optional<StockPltrData> existingData = stockPltrDataRepository.findLatestBySymbol(symbol);
            
            // 如果数据存在且是最近5分钟内的，直接返回
            if (existingData.isPresent()) {
                StockPltrData data = existingData.get();
                if (data.getCrawlTime().isAfter(LocalDateTime.now().minusMinutes(5))) {
                    logger.debug("返回缓存的股票数据: {}", symbol);
                    return data;
                }
            }
            
            // 从网站爬取新数据
            StockPltrData newData = stockPltrCrawler.getStockDetail(symbol);
            if (newData != null) {
                // 保存到数据库
                saveStockData(newData);
                logger.info("成功获取并保存股票数据: {}", symbol);
                return newData;
            }
            
            // 如果爬取失败，返回缓存数据
            return existingData.orElse(null);
            
        } catch (Exception e) {
            logger.error("获取股票数据失败: {} - {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取股票评论
     * @param symbol 股票代码
     * @return 评论列表
     */
    public List<StockComment> getStockComments(String symbol) {
        try {
            logger.info("获取股票评论: {}", symbol);
            
            // 先尝试从数据库获取最近评论
            List<StockComment> existingComments = stockCommentRepository.findByStockSymbolAndDataSourceOrderByCommentTimeDesc(symbol, "stockpltr");
            
            // 如果评论存在且是最近10分钟内的，直接返回
            if (!existingComments.isEmpty()) {
                StockComment latestComment = existingComments.get(0);
                if (latestComment.getCrawlTime().isAfter(LocalDateTime.now().minusMinutes(10))) {
                    logger.debug("返回缓存的股票评论: {} - {}条", symbol, existingComments.size());
                    return existingComments;
                }
            }
            
            // 从网站爬取新评论
            List<StockComment> newComments = stockPltrCrawler.getStockComments(symbol);
            if (!newComments.isEmpty()) {
                // 保存到数据库
                saveStockComments(newComments);
                logger.info("成功获取并保存股票评论: {} - {}条", symbol, newComments.size());
                return newComments;
            }
            
            // 如果爬取失败，返回缓存评论
            return existingComments;
            
        } catch (Exception e) {
            logger.error("获取股票评论失败: {} - {}", symbol, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * 获取股票完整信息（数据+评论）
     * @param symbol 股票代码
     * @return 股票数据（包含评论）
     */
    public StockPltrData getStockFullInfo(String symbol) {
        try {
            logger.info("获取股票完整信息: {}", symbol);
            
            // 异步获取数据和评论
            CompletableFuture<StockPltrData> dataFuture = CompletableFuture.supplyAsync(() -> getStockData(symbol));
            CompletableFuture<List<StockComment>> commentsFuture = CompletableFuture.supplyAsync(() -> getStockComments(symbol));
            
            // 等待两个任务完成
            CompletableFuture.allOf(dataFuture, commentsFuture).join();
            
            StockPltrData stockData = dataFuture.get();
            List<StockComment> comments = commentsFuture.get();
            
            if (stockData != null) {
                stockData.setComments(comments);
                logger.info("成功获取股票完整信息: {} - 数据: {}, 评论: {}条", 
                    symbol, stockData.getCompanyName(), comments.size());
            }
            
            return stockData;
            
        } catch (Exception e) {
            logger.error("获取股票完整信息失败: {} - {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * 批量更新股票数据
     * @param symbols 股票代码列表
     */
    @Async
    public void batchUpdateStockData(List<String> symbols) {
        if (!crawlEnabled) {
            logger.info("StockPltr爬虫已禁用");
            return;
        }
        
        logger.info("开始批量更新股票数据: {}个股票", symbols.size());
        
        for (String symbol : symbols) {
            try {
                // 获取股票数据
                StockPltrData stockData = stockPltrCrawler.getStockDetail(symbol);
                if (stockData != null) {
                    saveStockData(stockData);
                }
                
                // 获取股票评论
                List<StockComment> comments = stockPltrCrawler.getStockComments(symbol);
                if (!comments.isEmpty()) {
                    saveStockComments(comments);
                }
                
                // 避免请求过于频繁
                Thread.sleep(2000);
                
            } catch (Exception e) {
                logger.error("批量更新股票数据失败: {} - {}", symbol, e.getMessage());
            }
        }
        
        logger.info("批量更新股票数据完成");
    }
    
    /**
     * 启动定时爬取任务
     */
    public void startScheduledCrawl() {
        if (!crawlEnabled) {
            logger.info("StockPltr定时爬取已禁用");
            return;
        }
        
        logger.info("启动StockPltr定时爬取任务，间隔: {}秒", crawlInterval);
        
        // 定时清理旧数据
        scheduler.scheduleAtFixedRate(this::cleanupOldData, 0, 24, TimeUnit.HOURS);
        
        // 定时爬取数据（这里可以配置要监控的股票列表）
        scheduler.scheduleAtFixedRate(() -> {
            List<String> symbols = List.of("AAPL", "TSLA", "PLTR", "NVDA", "MSFT");
            batchUpdateStockData(symbols);
        }, 0, crawlInterval, TimeUnit.SECONDS);
    }
    
    /**
     * 停止定时爬取任务
     */
    public void stopScheduledCrawl() {
        logger.info("停止StockPltr定时爬取任务");
        scheduler.shutdown();
    }
    
    /**
     * 保存股票数据
     */
    @Transactional
    public StockPltrData saveStockData(StockPltrData stockData) {
        try {
            stockData.setUpdatedAt(LocalDateTime.now());
            StockPltrData savedData = stockPltrDataRepository.save(stockData);
            logger.debug("保存股票数据成功: {}", savedData.getSymbol());
            return savedData;
        } catch (Exception e) {
            logger.error("保存股票数据失败: {} - {}", stockData.getSymbol(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * 保存股票评论
     */
    @Transactional
    public List<StockComment> saveStockComments(List<StockComment> comments) {
        try {
            List<StockComment> savedComments = stockCommentRepository.saveAll(comments);
            logger.debug("保存股票评论成功: {}条", savedComments.size());
            return savedComments;
        } catch (Exception e) {
            logger.error("保存股票评论失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 获取股票历史数据
     * @param symbol 股票代码
     * @param days 天数
     * @return 历史数据列表
     */
    public List<StockPltrData> getStockHistory(String symbol, int days) {
        try {
            LocalDateTime startTime = LocalDateTime.now().minusDays(days);
            LocalDateTime endTime = LocalDateTime.now();
            
            List<StockPltrData> history = stockPltrDataRepository.findBySymbolAndCrawlTimeBetween(symbol, startTime, endTime);
            logger.info("获取股票历史数据: {} - {}条记录", symbol, history.size());
            return history;
            
        } catch (Exception e) {
            logger.error("获取股票历史数据失败: {} - {}", symbol, e.getMessage());
            return List.of();
        }
    }
    
    /**
     * 获取股票评论统计
     * @param symbol 股票代码
     * @return 评论统计信息
     */
    public Object getCommentStatistics(String symbol) {
        try {
            long totalComments = stockCommentRepository.countByStockSymbol(symbol);
            List<Object[]> sentimentStats = stockCommentRepository.countByStockSymbolAndSentiment(symbol);
            List<StockComment> popularComments = stockCommentRepository.findPopularComments(symbol);
            
            return Map.of(
                "totalCount", totalComments,
                "sentimentStatistics", sentimentStats,
                "topComments", popularComments.stream().limit(5).toList()
            );
            
        } catch (Exception e) {
            logger.error("获取股票评论统计失败: {} - {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * 清理旧数据
     */
    @Transactional
    public void cleanupOldData() {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
            
            // 删除旧的股票数据
            stockPltrDataRepository.deleteByCrawlTimeBefore(cutoffTime);
            
            // 删除旧的评论数据
            stockCommentRepository.deleteByCrawlTimeBefore(cutoffTime);
            
            logger.info("清理{}天前的旧数据完成", retentionDays);
            
        } catch (Exception e) {
            logger.error("清理旧数据失败: {}", e.getMessage());
        }
    }
    
    /**
     * 搜索股票
     * @param query 搜索关键词
     * @return 搜索结果
     */
    public StockPltrData searchStock(String query) {
        try {
            logger.info("搜索股票: {}", query);
            
            StockPltrData searchResult = stockPltrCrawler.searchStock(query);
            if (searchResult != null) {
                saveStockData(searchResult);
                logger.info("搜索股票成功: {} - {}", query, searchResult.getCompanyName());
            }
            
            return searchResult;
            
        } catch (Exception e) {
            logger.error("搜索股票失败: {} - {}", query, e.getMessage());
            return null;
        }
    }
}
