package com.quant.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Twitter监控器
 * 监控指定用户的推文更新
 */
@Component
public class TwitterMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterMonitor.class);
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final List<TweetListener> listeners = new ArrayList<>();
    
    @Value("${twitter.monitor.target-user:xiaozhaolucky}")
    private String targetUser;
    
    @Value("${twitter.monitor.target-url:https://x.com/xiaozhaolucky}")
    private String targetUrl;
    
    @Value("${twitter.monitor.user-agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36}")
    private String userAgent;
    
    @Value("${twitter.monitor.timeout:10000}")
    private int timeout;
    
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
        
        isMonitoring = true;
        logger.info("开始监控Twitter用户: @{}", targetUser);
        logger.info("监控地址: {}", targetUrl);
        
        // 每30秒检查一次新推文
        scheduler.scheduleAtFixedRate(this::checkForNewTweets, 0, 30, TimeUnit.SECONDS);
    }
    
    /**
     * 停止监控
     */
    public void stopMonitoring() {
        isMonitoring = false;
        scheduler.shutdown();
        logger.info("Twitter监控已停止");
    }
    
    /**
     * 检查新推文
     */
    private void checkForNewTweets() {
        try {
            // 使用curl获取用户最新推文
            String latestTweet = getLatestTweet();
            if (latestTweet != null && !latestTweet.equals(lastTweetId)) {
                if (lastTweetId != null) {
                    // 有新推文
                    notifyNewTweet(latestTweet);
                }
                lastTweetId = latestTweet;
            }
        } catch (Exception e) {
            logger.error("检查推文时发生错误: {}", e.getMessage());
        }
    }
    
    /**
     * 获取最新推文
     */
    private String getLatestTweet() {
        try {
            // 使用curl命令获取Twitter页面内容
            ProcessBuilder pb = new ProcessBuilder(
                "curl", "-s", 
                targetUrl,
                "-H", "User-Agent: " + userAgent,
                "--connect-timeout", String.valueOf(timeout / 1000)
            );
            
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            
            process.waitFor();
            
            // 简单解析推文内容（这里需要根据实际页面结构调整）
            String htmlContent = content.toString();
            return extractLatestTweetId(htmlContent);
            
        } catch (IOException | InterruptedException e) {
            logger.error("获取推文失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从HTML内容中提取最新推文ID
     */
    private String extractLatestTweetId(String htmlContent) {
        // 这里需要根据Twitter页面的实际HTML结构来解析
        // 由于Twitter使用JavaScript渲染，可能需要使用Selenium等工具
        // 暂时返回一个模拟的推文ID
        return "tweet_" + System.currentTimeMillis();
    }
    
    /**
     * 通知新推文
     */
    private void notifyNewTweet(String tweetId) {
        String message = String.format(
            "🚨 新推文提醒 🚨\n用户: @%s\n时间: %s\n推文ID: %s",
            targetUser,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            tweetId
        );
        
        // 发送系统通知
        sendSystemNotification(message);
        
        // 通知所有监听器
        for (TweetListener listener : listeners) {
            try {
                listener.onNewTweet(targetUser, tweetId);
            } catch (Exception e) {
                logger.error("通知监听器时发生错误: {}", e.getMessage());
            }
        }
        
        logger.info("检测到新推文: @{} - {}", targetUser, tweetId);
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
        void onNewTweet(String username, String tweetId);
    }
}
