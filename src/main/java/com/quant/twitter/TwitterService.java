package com.quant.twitter;

import com.quant.config.TwitterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Twitter服务类
 * 提供Twitter监控的启动和停止功能
 */
@Service
public class TwitterService {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);
    
    @Autowired
    private TwitterMonitor twitterMonitor;
    
    @Autowired
    private TwitterConfig twitterConfig;
    
    @PostConstruct
    public void init() {
        // 添加控制台监听器
        twitterMonitor.addListener((username, tweetId) -> {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🚨 Twitter 新推文提醒 🚨");
            System.out.println("用户: @" + username);
            System.out.println("推文ID: " + tweetId);
            System.out.println("时间: " + java.time.LocalDateTime.now());
            System.out.println("=".repeat(50) + "\n");
        });
        
        // 启动监控
        if (twitterConfig.isEnabled()) {
            twitterMonitor.startMonitoring();
            logger.info("Twitter服务已启动，开始监控 @{}", twitterConfig.getTargetUser());
            logger.info("监控配置: {}", twitterConfig);
        } else {
            logger.info("Twitter监控已禁用");
        }
    }
    
    @PreDestroy
    public void destroy() {
        twitterMonitor.stopMonitoring();
        logger.info("Twitter服务已停止");
    }
    
    /**
     * 手动检查推文
     */
    public void checkTweetsNow() {
        logger.info("手动检查推文...");
        // 这里可以添加手动检查的逻辑
    }
}
