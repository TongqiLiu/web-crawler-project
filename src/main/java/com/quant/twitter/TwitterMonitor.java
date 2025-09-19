package com.quant.twitter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.quant.config.WebDriverConfig;
import com.quant.twitter.model.Tweet;
import com.quant.twitter.parser.TweetParser;

/**
 * Twitter监控器
 * 监控指定用户的推文更新
 */
@Component
public class TwitterMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterMonitor.class);
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<TweetListener> listeners = new ArrayList<>();
    
    @Autowired
    private WebDriverConfig webDriverConfig;
    
    @Autowired
    private TweetParser tweetParser;
    
    @Value("${twitter.monitor.target-user:xiaozhaolucky}")
    private String targetUser;
    
    @Value("${twitter.monitor.target-url:https://x.com/xiaozhaolucky}")
    private String targetUrl;
    
    @Value("${twitter.monitor.timeout:30}")
    private int timeout;
    
    @Value("${twitter.monitor.max-retries:3}")
    private int maxRetries;
    
    private WebDriver webDriver;
    private WebDriverWait wait;
    private String lastTweetId = null;
    private boolean isMonitoring = false;
    
    /**
     * 添加推文监听器
     */
    public void addListener(TweetListener listener) {
        listeners.add(listener);
    }
    
    /**
     * 开始监控
     */
    public void startMonitoring() {
        if (isMonitoring) {
            logger.warn("Twitter监控已在运行中");
            return;
        }
        
        try {
            // 初始化WebDriver
            initWebDriver();
            
            isMonitoring = true;
            logger.info("开始监控Twitter用户: @{}", targetUser);
            logger.info("监控地址: {}", targetUrl);
            
            // 每30秒检查一次新推文
            scheduler.scheduleAtFixedRate(this::checkForNewTweets, 0, 30, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.error("启动Twitter监控失败: {}", e.getMessage());
            throw new RuntimeException("Twitter监控启动失败", e);
        }
    }
    
    /**
     * 初始化WebDriver
     */
    private void initWebDriver() {
        try {
            webDriver = webDriverConfig.createWebDriver();
            wait = new WebDriverWait(webDriver, Duration.ofSeconds(timeout));
            logger.info("WebDriver初始化成功");
        } catch (Exception e) {
            logger.error("WebDriver初始化失败: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 停止监控
     */
    public void stopMonitoring() {
        isMonitoring = false;
        scheduler.shutdown();
        
        // 关闭WebDriver
        if (webDriver != null) {
            try {
                webDriver.quit();
                webDriver = null;
                wait = null;
                logger.info("WebDriver已关闭");
            } catch (Exception e) {
                logger.error("关闭WebDriver时发生错误: {}", e.getMessage());
            }
        }
        
        logger.info("Twitter监控已停止");
    }
    
    /**
     * 检查新推文
     */
    private void checkForNewTweets() {
        if (!isMonitoring || webDriver == null) {
            return;
        }
        
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                // 获取最新推文
                Tweet latestTweet = getLatestTweet();
                
                if (latestTweet != null && latestTweet.isValid()) {
                    String currentTweetId = latestTweet.getId();
                    
                    if (currentTweetId != null && !currentTweetId.equals(lastTweetId)) {
                        if (lastTweetId != null) {
                            // 有新推文
                            notifyNewTweet(latestTweet);
                        }
                        lastTweetId = currentTweetId;
                    }
                }
                
                // 检查成功，退出重试循环
                break;
                
            } catch (Exception e) {
                retryCount++;
                logger.error("检查推文时发生错误 (重试 {}/{}): {}", 
                    retryCount, maxRetries, e.getMessage());
                
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(5000); // 等待5秒后重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // 重试失败，重新初始化WebDriver
                    logger.warn("重试失败，尝试重新初始化WebDriver");
                    try {
                        reinitWebDriver();
                    } catch (Exception re) {
                        logger.error("重新初始化WebDriver失败: {}", re.getMessage());
                    }
                }
            }
        }
    }
    
    /**
     * 重新初始化WebDriver
     */
    private void reinitWebDriver() {
        if (webDriver != null) {
            try {
                webDriver.quit();
            } catch (Exception e) {
                logger.debug("关闭旧WebDriver时发生错误: {}", e.getMessage());
            }
        }
        
        initWebDriver();
    }
    
    /**
     * 获取最新推文
     */
    private Tweet getLatestTweet() {
        try {
            // 访问Twitter页面
            logger.debug("访问Twitter页面: {}", targetUrl);
            webDriver.get(targetUrl);
            
            // 等待页面加载
            Thread.sleep(3000);
            
            // 解析推文
            Tweet latestTweet = tweetParser.getLatestTweet(webDriver, targetUser);
            
            if (latestTweet != null) {
                logger.debug("获取到最新推文: {}", latestTweet.getId());
            } else {
                logger.debug("未获取到推文内容");
            }
            
            return latestTweet;
            
        } catch (Exception e) {
            logger.error("获取最新推文失败: {}", e.getMessage());
            throw new RuntimeException("获取推文失败", e);
        }
    }
    
    /**
     * 通知新推文
     */
    private void notifyNewTweet(Tweet tweet) {
        String message = String.format(
            "🚨 新推文提醒 🚨\n用户: @%s\n时间: %s\n推文ID: %s\n推文链接: %s\n内容预览: %s",
            tweet.getUsername(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            tweet.getId(),
            tweet.getUrl(),
            tweet.getPreview()
        );
        
        // 发送系统通知
        sendSystemNotification(message);
        
        // 通知所有监听器
        for (TweetListener listener : listeners) {
            try {
                listener.onNewTweet(tweet);
            } catch (Exception e) {
                logger.error("通知监听器时发生错误: {}", e.getMessage());
            }
        }
        
        logger.info("检测到新推文: @{} - {} [{}]", 
            tweet.getUsername(), tweet.getId(), tweet.getUrl());
        logger.info("推文内容: {}", tweet.getPreview());
    }
    
    /**
     * 发送系统通知
     */
    private void sendSystemNotification(String message) {
        try {
            // macOS系统通知
            ProcessBuilder pb = new ProcessBuilder(
                "osascript", "-e",
                String.format("display notification \"%s\" with title \"Twitter提醒\"", 
                    message.replace("\"", "\\\""))
            );
            pb.start();
        } catch (IOException e) {
            logger.error("发送系统通知失败: {}", e.getMessage());
        }
    }
    
    /**
     * 推文监听器接口
     */
    public interface TweetListener {
        void onNewTweet(Tweet tweet);
    }
}
