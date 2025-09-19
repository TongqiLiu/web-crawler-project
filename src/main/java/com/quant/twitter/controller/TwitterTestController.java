package com.quant.twitter.controller;

import com.quant.twitter.TwitterMonitor;
import com.quant.twitter.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Twitter测试控制器
 * 用于手动测试推特监控功能
 */
@RestController
@RequestMapping("/twitter-test")
public class TwitterTestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterTestController.class);
    
    @Autowired
    private TwitterService twitterService;
    
    /**
     * 测试推特监控状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            status.put("service", "Twitter监控系统");
            status.put("status", "运行中");
            status.put("message", "系统正在监控 @xiaozhaolucky 的推文更新");
            status.put("check_interval", "30秒");
            status.put("timestamp", System.currentTimeMillis());
            
            logger.info("Twitter监控状态查询: 系统运行正常");
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            status.put("service", "Twitter监控系统");
            status.put("status", "错误");
            status.put("error", e.getMessage());
            status.put("timestamp", System.currentTimeMillis());
            
            logger.error("Twitter监控状态查询失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(status);
        }
    }
    
    /**
     * 手动触发检测（用于测试）
     */
    @GetMapping("/manual-check")
    public ResponseEntity<Map<String, Object>> manualCheck() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            logger.info("手动触发推文检测测试...");
            
            // 触发检查
            twitterService.checkTweetsNow();
            
            result.put("service", "Twitter监控系统");
            result.put("action", "手动检测");
            result.put("status", "已触发");
            result.put("message", "手动推文检测已触发，请查看日志了解检测结果");
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("service", "Twitter监控系统");
            result.put("action", "手动检测");
            result.put("status", "失败");
            result.put("error", e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
            
            logger.error("手动推文检测失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Twitter Monitor");
        health.put("version", "2.0");
        return ResponseEntity.ok(health);
    }
}
