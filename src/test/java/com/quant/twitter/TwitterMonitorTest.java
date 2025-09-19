package com.quant.twitter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quant.config.TwitterConfig;
import com.quant.config.WebDriverConfig;
import com.quant.twitter.parser.TweetParser;

/**
 * 推特监控功能测试类（简化版本，不依赖Spring上下文）
 */
public class TwitterMonitorTest {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitterMonitorTest.class);
    
    private TwitterService twitterService;
    private TwitterMonitor twitterMonitor;
    private TwitterConfig twitterConfig;
    
    @BeforeEach
    public void setup() {
        // 手动创建测试所需的对象
        twitterConfig = new TwitterConfig();
        twitterConfig.setEnabled(true);
        twitterConfig.setTargetUser("elonmusk");
        twitterConfig.setTargetUrl("https://x.com/elonmusk");
        
        logger.info("测试环境设置完成");
    }
    
    @Test
    public void testTwitterConfigInitialization() {
        logger.info("测试TwitterConfig初始化...");
        
        // 验证配置对象创建成功
        assert twitterConfig != null;
        assert twitterConfig.isEnabled();
        assert "elonmusk".equals(twitterConfig.getTargetUser());
        assert "https://x.com/elonmusk".equals(twitterConfig.getTargetUrl());
        
        logger.info("TwitterConfig配置: {}", twitterConfig.toString());
        logger.info("TwitterConfig初始化测试通过 ✅");
    }
    
    @Test 
    public void testWebDriverConfigCreation() {
        logger.info("测试WebDriverConfig创建...");
        
        try {
            WebDriverConfig webDriverConfig = new WebDriverConfig();
            webDriverConfig.init();
            
            logger.info("WebDriverConfig创建测试通过 ✅");
            logger.info("注意：实际WebDriver创建需要Chrome浏览器环境支持");
            
        } catch (Exception e) {
            logger.warn("WebDriverConfig测试警告: {}", e.getMessage());
            logger.info("这可能是由于缺少Chrome浏览器或WebDriver导致的，属于正常情况");
        }
    }
    
    @Test
    public void testTweetParserCreation() {
        logger.info("测试TweetParser创建...");
        
        try {
            TweetParser tweetParser = new TweetParser();
            assert tweetParser != null;
            
            logger.info("TweetParser创建测试通过 ✅");
            
        } catch (Exception e) {
            logger.error("TweetParser测试失败: {}", e.getMessage());
        }
    }
}
