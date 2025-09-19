package com.quant.stockpltr.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StockPltr测试控制器
 * 提供简单的测试接口，不依赖WebDriver
 */
@RestController
@RequestMapping("/api/stockpltr/test")
public class StockPltrTestController {
    
    /**
     * 测试基本功能
     */
    @GetMapping("/basic")
    public Map<String, Object> testBasic() {
        return Map.of(
            "message", "StockPltr基本功能正常",
            "timestamp", System.currentTimeMillis(),
            "status", "success"
        );
    }
    
    /**
     * 测试数据模型
     */
    @GetMapping("/model")
    public Map<String, Object> testModel() {
        return Map.of(
            "stockDataModel", "StockPltrData",
            "commentModel", "StockComment",
            "models", Map.of(
                "StockPltrData", Map.of(
                    "symbol", "String",
                    "companyName", "String", 
                    "currentPrice", "Double",
                    "priceChange", "Double",
                    "volume", "Long",
                    "marketCap", "Long"
                ),
                "StockComment", Map.of(
                    "stockSymbol", "String",
                    "commentId", "String",
                    "userName", "String",
                    "content", "String",
                    "likesCount", "Integer",
                    "sentiment", "String"
                )
            )
        );
    }
    
    /**
     * 测试配置
     */
    @GetMapping("/config")
    public Map<String, Object> testConfig() {
        return Map.of(
            "crawlEnabled", true,
            "crawlInterval", 300,
            "batchSize", 10,
            "retentionDays", 30,
            "baseUrl", "https://www.stockpltr.com"
        );
    }
}
