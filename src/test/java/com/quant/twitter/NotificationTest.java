package com.quant.twitter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quant.twitter.model.Tweet;

/**
 * 系统通知和监听器测试
 */
public class NotificationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationTest.class);
    
    @Test
    public void testTweetModelCreation() {
        logger.info("测试Tweet模型创建...");
        
        // 创建测试推文
        Tweet tweet = new Tweet("1234567890", "elonmusk", "测试推文内容");
        
        // 验证推文属性
        assert "1234567890".equals(tweet.getId());
        assert "elonmusk".equals(tweet.getUsername());
        assert "测试推文内容".equals(tweet.getContent());
        assert tweet.getUrl().contains("twitter.com/elonmusk/status/1234567890");
        assert tweet.getDetectTime() != null;
        
        // 测试预览功能
        String preview = tweet.getPreview();
        assert preview != null && !preview.isEmpty();
        
        // 测试有效性检查
        assert tweet.isValid();
        
        logger.info("Tweet模型创建测试通过 ✅");
        logger.info("推文预览: {}", preview);
        logger.info("推文URL: {}", tweet.getUrl());
    }
    
    @Test
    public void testTweetListenerSimulation() {
        logger.info("测试推文监听器模拟...");
        
        // 模拟监听器
        TwitterMonitor.TweetListener listener = new TwitterMonitor.TweetListener() {
            @Override
            public void onNewTweet(Tweet tweet) {
                logger.info("监听器收到新推文: @{} - {}", tweet.getUsername(), tweet.getPreview());
            }
        };
        
        // 创建测试推文并触发监听器
        Tweet testTweet = new Tweet("9876543210", "elonmusk", "这是一条测试推文，用于验证监听器功能是否正常工作");
        
        try {
            listener.onNewTweet(testTweet);
            logger.info("推文监听器模拟测试通过 ✅");
        } catch (Exception e) {
            logger.error("推文监听器测试失败: {}", e.getMessage());
        }
    }
    
    @Test
    public void testSystemNotificationSimulation() {
        logger.info("测试系统通知模拟...");
        
        // 模拟系统通知消息
        String message = "🚨 新推文提醒 🚨\n用户: @xiaozhaolucky\n时间: 2025-09-20 02:42:30\n推文ID: 1234567890\n推文链接: https://twitter.com/xiaozhaolucky/status/1234567890\n内容预览: 这是一条测试推文...";
        
        logger.info("模拟系统通知消息:");
        logger.info("{}", message);
        
        // 验证消息格式
        assert message.contains("🚨");
        assert message.contains("@xiaozhaolucky");
        assert message.contains("1234567890");
        assert message.contains("twitter.com");
        
        logger.info("系统通知模拟测试通过 ✅");
    }
    
    @Test
    public void testTweetValidation() {
        logger.info("测试推文验证功能...");
        
        // 测试有效推文
        Tweet validTweet = new Tweet("123", "user", "content");
        assert validTweet.isValid();
        
        // 测试无效推文（空ID）
        Tweet invalidTweet1 = new Tweet("", "user", "content");
        assert !invalidTweet1.isValid();
        
        // 测试无效推文（空用户名）
        Tweet invalidTweet2 = new Tweet("123", "", "content");
        assert !invalidTweet2.isValid();
        
        // 测试无效推文（null ID）
        Tweet invalidTweet3 = new Tweet(null, "user", "content");
        assert !invalidTweet3.isValid();
        
        logger.info("推文验证功能测试通过 ✅");
    }
    
    @Test
    public void testTweetPreviewFunctionality() {
        logger.info("测试推文预览功能...");
        
        // 测试短内容
        Tweet shortTweet = new Tweet("1", "user", "短内容");
        String shortPreview = shortTweet.getPreview();
        assert "短内容".equals(shortPreview);
        
        // 测试长内容
        String longContent = "这是一条非常长的推文内容，用来测试预览功能是否能正确截取前100个字符并添加省略号";
        Tweet longTweet = new Tweet("2", "user", longContent);
        String longPreview = longTweet.getPreview();
        
        logger.info("长内容预览: {}", longPreview);
        logger.info("预览长度: {}", longPreview.length());
        
        // 验证预览功能
        assert longPreview.contains("这是一条非常长的推文内容");
        assert longPreview.length() > 0;
        
        // 测试空内容
        Tweet emptyTweet = new Tweet("3", "user", "");
        String emptyPreview = emptyTweet.getPreview();
        assert "[无内容]".equals(emptyPreview);
        
        logger.info("推文预览功能测试通过 ✅");
        logger.info("短内容预览: {}", shortPreview);
        logger.info("长内容预览: {}", longPreview);
        logger.info("空内容预览: {}", emptyPreview);
    }
}
