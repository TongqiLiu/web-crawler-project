package com.quant.stockpltr.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quant.stockpltr.model.StockComment;
import com.quant.stockpltr.model.StockPltrData;
import com.quant.stockpltr.service.StockPltrDataService;

/**
 * StockPltr数据控制器
 * 提供REST API接口访问StockPltr数据
 */
@RestController
@RequestMapping("/api/stockpltr")
public class StockPltrController {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPltrController.class);
    
    @Autowired
    private StockPltrDataService stockPltrDataService;
    
    /**
     * 获取股票数据
     * GET /api/stockpltr/stock/{symbol}
     */
    @GetMapping("/stock/{symbol}")
    public ResponseEntity<StockPltrData> getStockData(@PathVariable String symbol) {
        try {
            logger.info("API请求获取股票数据: {}", symbol);
            
            StockPltrData stockData = stockPltrDataService.getStockData(symbol);
            
            if (stockData != null) {
                return ResponseEntity.ok(stockData);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("API获取股票数据失败: {} - {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取股票评论
     * GET /api/stockpltr/stock/{symbol}/comments
     */
    @GetMapping("/stock/{symbol}/comments")
    public ResponseEntity<List<StockComment>> getStockComments(@PathVariable String symbol) {
        try {
            logger.info("API请求获取股票评论: {}", symbol);
            
            List<StockComment> comments = stockPltrDataService.getStockComments(symbol);
            
            return ResponseEntity.ok(comments);
            
        } catch (Exception e) {
            logger.error("API获取股票评论失败: {} - {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取股票完整信息（数据+评论）
     * GET /api/stockpltr/stock/{symbol}/full
     */
    @GetMapping("/stock/{symbol}/full")
    public ResponseEntity<StockPltrData> getStockFullInfo(@PathVariable String symbol) {
        try {
            logger.info("API请求获取股票完整信息: {}", symbol);
            
            StockPltrData stockData = stockPltrDataService.getStockFullInfo(symbol);
            
            if (stockData != null) {
                return ResponseEntity.ok(stockData);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("API获取股票完整信息失败: {} - {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取股票历史数据
     * GET /api/stockpltr/stock/{symbol}/history?days=30
     */
    @GetMapping("/stock/{symbol}/history")
    public ResponseEntity<List<StockPltrData>> getStockHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int days) {
        try {
            logger.info("API请求获取股票历史数据: {} - {}天", symbol, days);
            
            List<StockPltrData> history = stockPltrDataService.getStockHistory(symbol, days);
            
            return ResponseEntity.ok(history);
            
        } catch (Exception e) {
            logger.error("API获取股票历史数据失败: {} - {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取股票评论统计
     * GET /api/stockpltr/stock/{symbol}/statistics
     */
    @GetMapping("/stock/{symbol}/statistics")
    public ResponseEntity<Object> getCommentStatistics(@PathVariable String symbol) {
        try {
            logger.info("API请求获取股票评论统计: {}", symbol);
            
            Object statistics = stockPltrDataService.getCommentStatistics(symbol);
            
            if (statistics != null) {
                return ResponseEntity.ok(statistics);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("API获取股票评论统计失败: {} - {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 搜索股票
     * GET /api/stockpltr/search?q=AAPL
     */
    @GetMapping("/search")
    public ResponseEntity<StockPltrData> searchStock(@RequestParam String q) {
        try {
            logger.info("API请求搜索股票: {}", q);
            
            StockPltrData searchResult = stockPltrDataService.searchStock(q);
            
            if (searchResult != null) {
                return ResponseEntity.ok(searchResult);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            logger.error("API搜索股票失败: {} - {}", q, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 批量更新股票数据
     * POST /api/stockpltr/batch-update
     */
    @PostMapping("/batch-update")
    public ResponseEntity<Map<String, Object>> batchUpdateStockData(@RequestParam List<String> symbols) {
        try {
            logger.info("API请求批量更新股票数据: {}个股票", symbols.size());
            
            // 异步执行批量更新
            stockPltrDataService.batchUpdateStockData(symbols);
            
            Map<String, Object> response = Map.of(
                "message", "批量更新任务已启动",
                "symbols", symbols,
                "count", symbols.size(),
                "status", "processing"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API批量更新股票数据失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 启动定时爬取任务
     * POST /api/stockpltr/scheduler/start
     */
    @PostMapping("/scheduler/start")
    public ResponseEntity<Map<String, Object>> startScheduledCrawl() {
        try {
            logger.info("API请求启动定时爬取任务");
            
            stockPltrDataService.startScheduledCrawl();
            
            Map<String, Object> response = Map.of(
                "message", "定时爬取任务已启动",
                "status", "started"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API启动定时爬取任务失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 停止定时爬取任务
     * POST /api/stockpltr/scheduler/stop
     */
    @PostMapping("/scheduler/stop")
    public ResponseEntity<Map<String, Object>> stopScheduledCrawl() {
        try {
            logger.info("API请求停止定时爬取任务");
            
            stockPltrDataService.stopScheduledCrawl();
            
            Map<String, Object> response = Map.of(
                "message", "定时爬取任务已停止",
                "status", "stopped"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API停止定时爬取任务失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 清理旧数据
     * POST /api/stockpltr/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldData() {
        try {
            logger.info("API请求清理旧数据");
            
            stockPltrDataService.cleanupOldData();
            
            Map<String, Object> response = Map.of(
                "message", "旧数据清理完成",
                "status", "completed"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("API清理旧数据失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取API状态信息
     * GET /api/stockpltr/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = Map.of(
                "service", "StockPltr Data Service",
                "version", "1.0.0",
                "status", "running",
                "timestamp", System.currentTimeMillis(),
                "endpoints", List.of(
                    "GET /api/stockpltr/stock/{symbol}",
                    "GET /api/stockpltr/stock/{symbol}/comments",
                    "GET /api/stockpltr/stock/{symbol}/full",
                    "GET /api/stockpltr/stock/{symbol}/history",
                    "GET /api/stockpltr/stock/{symbol}/statistics",
                    "GET /api/stockpltr/search",
                    "POST /api/stockpltr/batch-update",
                    "POST /api/stockpltr/scheduler/start",
                    "POST /api/stockpltr/scheduler/stop",
                    "POST /api/stockpltr/cleanup"
                )
            );
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            logger.error("API获取状态信息失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
