package com.quant.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quant.config.TwitterConfig;

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
        twitterMonitor.addListener((tweet) -> {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🚨 Twitter 新推文提醒 🚨");
            System.out.println("用户: @" + tweet.getUsername());
            System.out.println("推文ID: " + tweet.getId());
            System.out.println("推文链接: " + tweet.getUrl());
            System.out.println("发布时间: " + (tweet.getPublishTime() != null ? tweet.getPublishTime() : "未知"));
            System.out.println("检测时间: " + tweet.getDetectTime());
            System.out.println("内容预览: " + tweet.getPreview());
            if (tweet.getContent() != null && !tweet.getContent().isEmpty()) {
                System.out.println("完整内容: " + tweet.getContent());
            }
            System.out.println("=".repeat(80) + "\n");
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
        logger.info("手动检查推文开始...");
        try {
            if (!twitterConfig.isEnabled()) {
                logger.warn("Twitter监控未启用，无法手动检查");
                return;
            }
            
            // 触发监控器的检查方法
            twitterMonitor.manualCheck();
            logger.info("手动检查推文完成");
            
        } catch (Exception e) {
            logger.error("手动检查推文失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取监控状态
     */
    public String getMonitoringStatus() {
        return String.format(
            "Twitter监控状态: %s, 目标用户: @%s, 配置: %s",
            twitterConfig.isEnabled() ? "运行中" : "已停用",
            twitterConfig.getTargetUser(),
            twitterConfig
        );
    }
}
