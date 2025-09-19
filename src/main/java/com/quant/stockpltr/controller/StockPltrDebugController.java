package com.quant.stockpltr.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quant.stockpltr.crawler.StockPltrCrawler;

/**
 * StockPltr调试控制器
 * 用于诊断和调试爬虫问题
 */
@RestController
@RequestMapping("/api/stockpltr/debug")
public class StockPltrDebugController {
    
    @Autowired
    private StockPltrCrawler stockPltrCrawler;
    
    /**
     * 测试WebDriver初始化
     */
    @GetMapping("/webdriver")
    public Map<String, Object> testWebDriver() {
        try {
            stockPltrCrawler.init();
            return Map.of(
                "status", "success",
                "message", "WebDriver初始化成功",
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "message", "WebDriver初始化失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    /**
     * 测试网站访问
     */
    @GetMapping("/website")
    public Map<String, Object> testWebsite() {
        try {
            stockPltrCrawler.init();
            // 尝试访问stockpltr.com
            String testUrl = "https://www.stockpltr.com";
            // 这里需要实际访问网站来测试
            return Map.of(
                "status", "success",
                "message", "网站访问测试完成",
                "url", testUrl,
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "message", "网站访问失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        }
    }
    
    /**
     * 测试股票搜索
     */
    @GetMapping("/search/{symbol}")
    public Map<String, Object> testSearch(@PathVariable String symbol) {
        try {
            stockPltrCrawler.init();
            var result = stockPltrCrawler.searchStock(symbol);
            return Map.of(
                "status", "success",
                "symbol", symbol,
                "result", result != null ? result.toString() : "null",
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "symbol", symbol,
                "message", "搜索失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        } finally {
            try {
                stockPltrCrawler.close();
            } catch (Exception e) {
                // 忽略关闭错误
            }
        }
    }
    
    /**
     * 测试股票详情获取
     */
    @GetMapping("/detail/{symbol}")
    public Map<String, Object> testDetail(@PathVariable String symbol) {
        try {
            stockPltrCrawler.init();
            var result = stockPltrCrawler.getStockDetail(symbol);
            return Map.of(
                "status", "success",
                "symbol", symbol,
                "result", result != null ? result.toString() : "null",
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "symbol", symbol,
                "message", "获取详情失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        } finally {
            try {
                stockPltrCrawler.close();
            } catch (Exception e) {
                // 忽略关闭错误
            }
        }
    }
    
    /**
     * 测试评论获取
     */
    @GetMapping("/comments/{symbol}")
    public Map<String, Object> testComments(@PathVariable String symbol) {
        try {
            stockPltrCrawler.init();
            var result = stockPltrCrawler.getStockComments(symbol);
            return Map.of(
                "status", "success",
                "symbol", symbol,
                "commentCount", result != null ? result.size() : 0,
                "result", result != null ? result.toString() : "null",
                "timestamp", System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Map.of(
                "status", "error",
                "symbol", symbol,
                "message", "获取评论失败: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
        } finally {
            try {
                stockPltrCrawler.close();
            } catch (Exception e) {
                // 忽略关闭错误
            }
        }
    }
}
