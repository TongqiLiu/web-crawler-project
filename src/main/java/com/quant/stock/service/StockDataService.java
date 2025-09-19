package com.quant.stock.service;

import com.quant.stock.model.StockData;
import com.quant.stock.repository.StockDataRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 股票数据服务类
 * 提供股票数据的获取、存储和查询功能
 */
@Service
public class StockDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(StockDataService.class);
    
    @Autowired
    private StockDataRepository stockDataRepository;
    
    @Value("${stock.data-source:yahoo-finance}")
    private String dataSource;
    
    @Value("${stock.update-interval:60}")
    private int updateInterval;
    
    // 默认监控的股票列表
    private final List<String> DEFAULT_SYMBOLS = Arrays.asList(
        "AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", 
        "META", "NVDA", "NFLX", "AMD", "INTC"
    );
    
    /**
     * 获取股票数据
     * @param symbol 股票代码
     * @return 股票数据
     */
    public Optional<StockData> getStockData(String symbol) {
        logger.info("获取股票数据: {}", symbol);
        return stockDataRepository.findTopBySymbolOrderByLastUpdatedDesc(symbol.toUpperCase());
    }
    
    /**
     * 获取股票历史数据
     * @param symbol 股票代码
     * @return 历史数据列表
     */
    public List<StockData> getHistoricalData(String symbol) {
        return stockDataRepository.findBySymbolOrderByLastUpdatedDesc(symbol.toUpperCase());
    }
    
    /**
     * 获取所有活跃股票代码
     * @return 股票代码列表
     */
    public List<String> getAllActiveSymbols() {
        return stockDataRepository.findAllActiveSymbols();
    }
    
    /**
     * 异步更新股票数据
     * @param symbol 股票代码
     * @return CompletableFuture<StockData>
     */
    @Async
    public CompletableFuture<StockData> updateStockDataAsync(String symbol) {
        try {
            StockData stockData = fetchStockDataFromYahoo(symbol);
            if (stockData != null) {
                stockData = stockDataRepository.save(stockData);
                logger.info("成功更新股票数据: {} - ${}", symbol, stockData.getCurrentPrice());
                return CompletableFuture.completedFuture(stockData);
            }
        } catch (Exception e) {
            logger.error("更新股票数据失败: {}, 错误: {}", symbol, e.getMessage(), e);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * 从Yahoo Finance获取股票数据
     * @param symbol 股票代码
     * @return 股票数据
     * @throws IOException 网络异常
     */
    private StockData fetchStockDataFromYahoo(String symbol) throws IOException {
        String url = "https://finance.yahoo.com/quote/" + symbol.toUpperCase();
        
        logger.debug("获取股票数据URL: {}", url);
        
        try {
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
                .timeout(10000)
                .get();
            
            StockData stockData = new StockData(symbol.toUpperCase(), "");
            
            // 提取股票名称
            Element nameElement = doc.selectFirst("h1[data-test-id='quote-header']");
            if (nameElement != null) {
                String fullName = nameElement.text();
                if (fullName.contains("(")) {
                    stockData.setName(fullName.substring(0, fullName.indexOf("(")).trim());
                } else {
                    stockData.setName(fullName);
                }
            }
            
            // 提取当前价格
            Element priceElement = doc.selectFirst("fin-streamer[data-test-id='qsp-price']");
            if (priceElement != null) {
                try {
                    BigDecimal currentPrice = new BigDecimal(priceElement.attr("data-value"));
                    stockData.setCurrentPrice(currentPrice);
                } catch (NumberFormatException e) {
                    logger.warn("解析当前价格失败: {}", priceElement.text());
                }
            }
            
            // 提取涨跌幅
            Element changeElement = doc.selectFirst("fin-streamer[data-test-id='qsp-price-change-percent']");
            if (changeElement != null) {
                try {
                    String changeText = changeElement.attr("data-value");
                    if (!changeText.isEmpty()) {
                        BigDecimal changePercent = new BigDecimal(changeText);
                        stockData.setChangePercent(changePercent);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("解析涨跌幅失败: {}", changeElement.text());
                }
            }
            
            // 提取其他数据
            extractAdditionalData(doc, stockData);
            
            return stockData;
            
        } catch (IOException e) {
            logger.error("获取股票数据失败: {}, URL: {}, 错误: {}", symbol, url, e.getMessage());
            throw e;
        }
    }
    
    /**
     * 提取额外的股票数据
     * @param doc HTML文档
     * @param stockData 股票数据对象
     */
    private void extractAdditionalData(Document doc, StockData stockData) {
        // 提取开盘价、最高价、最低价等数据
        Element summaryTable = doc.selectFirst("table[data-test-id='quote-statistics']");
        if (summaryTable != null) {
            extractFromSummaryTable(summaryTable, stockData);
        }
        
        // 提取前一日收盘价
        Element prevCloseElement = doc.selectFirst("td[data-test-id='PREV_CLOSE-value']");
        if (prevCloseElement != null) {
            try {
                BigDecimal prevClose = new BigDecimal(prevCloseElement.text().replace(",", ""));
                stockData.setPreviousClose(prevClose);
            } catch (NumberFormatException e) {
                logger.warn("解析前收盘价失败: {}", prevCloseElement.text());
            }
        }
    }
    
    /**
     * 从汇总表格中提取数据
     * @param table 表格元素
     * @param stockData 股票数据对象
     */
    private void extractFromSummaryTable(Element table, StockData stockData) {
        table.select("tr").forEach(row -> {
            Element label = row.selectFirst("td");
            Element value = row.selectFirst("td:nth-child(2)");
            
            if (label != null && value != null) {
                String labelText = label.text().toLowerCase();
                String valueText = value.text().replace(",", "");
                
                try {
                    switch (labelText) {
                        case "open":
                            stockData.setOpenPrice(new BigDecimal(valueText));
                            break;
                        case "day's range":
                            String[] range = valueText.split(" - ");
                            if (range.length == 2) {
                                stockData.setLowPrice(new BigDecimal(range[0]));
                                stockData.setHighPrice(new BigDecimal(range[1]));
                            }
                            break;
                        case "volume":
                            stockData.setVolume(new BigDecimal(valueText));
                            break;
                        case "market cap":
                            stockData.setMarketCap(parseMarketCap(valueText));
                            break;
                        case "pe ratio (ttm)":
                            if (!valueText.equals("N/A")) {
                                stockData.setPeRatio(new BigDecimal(valueText));
                            }
                            break;
                    }
                } catch (NumberFormatException e) {
                    logger.debug("解析数据失败: {} = {}", labelText, valueText);
                }
            }
        });
    }
    
    /**
     * 解析市值（支持K、M、B、T单位）
     * @param marketCapText 市值文本
     * @return 市值数值
     */
    private BigDecimal parseMarketCap(String marketCapText) {
        if (marketCapText == null || marketCapText.trim().isEmpty() || marketCapText.equals("N/A")) {
            return null;
        }
        
        String text = marketCapText.trim().toUpperCase();
        BigDecimal multiplier = BigDecimal.ONE;
        
        if (text.endsWith("K")) {
            multiplier = new BigDecimal("1000");
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("M")) {
            multiplier = new BigDecimal("1000000");
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("B")) {
            multiplier = new BigDecimal("1000000000");
            text = text.substring(0, text.length() - 1);
        } else if (text.endsWith("T")) {
            multiplier = new BigDecimal("1000000000000");
            text = text.substring(0, text.length() - 1);
        }
        
        try {
            return new BigDecimal(text).multiply(multiplier);
        } catch (NumberFormatException e) {
            logger.warn("解析市值失败: {}", marketCapText);
            return null;
        }
    }
    
    /**
     * 定时更新股票数据
     */
    @Scheduled(fixedRateString = "${stock.update-interval:60}000")
    public void scheduledUpdateStockData() {
        logger.info("开始定时更新股票数据...");
        
        List<String> symbols = getAllActiveSymbols();
        if (symbols.isEmpty()) {
            symbols = DEFAULT_SYMBOLS;
            logger.info("使用默认股票列表: {}", symbols);
        }
        
        for (String symbol : symbols) {
            updateStockDataAsync(symbol);
        }
        
        logger.info("股票数据更新任务已启动，共{}只股票", symbols.size());
    }
    
    /**
     * 手动更新指定股票数据
     * @param symbol 股票代码
     * @return 更新后的股票数据
     */
    public StockData manualUpdateStockData(String symbol) {
        logger.info("手动更新股票数据: {}", symbol);
        try {
            StockData stockData = fetchStockDataFromYahoo(symbol);
            if (stockData != null) {
                return stockDataRepository.save(stockData);
            }
        } catch (Exception e) {
            logger.error("手动更新股票数据失败: {}, 错误: {}", symbol, e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * 清理过期数据
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void cleanupOldData() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        int deletedCount = stockDataRepository.deleteOldData(cutoffTime);
        logger.info("清理了{}条过期股票数据", deletedCount);
    }
}
