package com.quant.twitter.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.quant.twitter.model.Tweet;

/**
 * 推文解析器
 * 负责从网页中解析推文信息
 */
@Component
public class TweetParser {
    
    private static final Logger logger = LoggerFactory.getLogger(TweetParser.class);
    
    // 推文容器选择器
    private static final String TWEET_SELECTOR = "[data-testid='tweet']";
    private static final String TWEET_TEXT_SELECTOR = "[data-testid='tweetText']";
    private static final String TWEET_TIME_SELECTOR = "time";
    private static final String TWEET_LINK_SELECTOR = "a[href*='/status/']";
    
    // 推文ID提取正则
    private static final Pattern TWEET_ID_PATTERN = Pattern.compile("/status/([0-9]+)");
    
    /**
     * 解析页面中的所有推文
     */
    public List<Tweet> parseTweets(WebDriver driver, String username) {
        List<Tweet> tweets = new ArrayList<>();
        
        try {
            // 等待推文加载
            Thread.sleep(2000);
            
            // 查找所有推文元素
            List<WebElement> tweetElements = driver.findElements(By.cssSelector(TWEET_SELECTOR));
            logger.info("找到 {} 个推文元素", tweetElements.size());
            
            for (WebElement tweetElement : tweetElements) {
                try {
                    Tweet tweet = parseSingleTweet(tweetElement, username);
                    if (tweet != null && tweet.isValid()) {
                        tweets.add(tweet);
                    }
                } catch (Exception e) {
                    logger.debug("解析单个推文失败: {}", e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("解析推文时发生错误: {}", e.getMessage());
        }
        
        return tweets;
    }
    
    /**
     * 解析单个推文
     */
    private Tweet parseSingleTweet(WebElement tweetElement, String username) {
        try {
            // 提取推文ID
            String tweetId = extractTweetId(tweetElement);
            if (tweetId == null) {
                return null;
            }
            
            // 提取推文内容
            String content = extractTweetContent(tweetElement);
            
            // 创建推文对象
            Tweet tweet = new Tweet(tweetId, username, content);
            
            // 提取发布时间
            LocalDateTime publishTime = extractPublishTime(tweetElement);
            tweet.setPublishTime(publishTime);
            
            // 提取互动数据
            extractEngagementData(tweetElement, tweet);
            
            logger.debug("成功解析推文: {}", tweet.getId());
            return tweet;
            
        } catch (Exception e) {
            logger.debug("解析推文元素失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 提取推文ID
     */
    private String extractTweetId(WebElement tweetElement) {
        try {
            WebElement linkElement = tweetElement.findElement(By.cssSelector(TWEET_LINK_SELECTOR));
            String href = linkElement.getAttribute("href");
            
            Matcher matcher = TWEET_ID_PATTERN.matcher(href);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (NoSuchElementException e) {
            logger.debug("未找到推文链接元素");
        }
        return null;
    }
    
    /**
     * 提取推文内容
     */
    private String extractTweetContent(WebElement tweetElement) {
        try {
            WebElement textElement = tweetElement.findElement(By.cssSelector(TWEET_TEXT_SELECTOR));
            return textElement.getText().trim();
        } catch (NoSuchElementException e) {
            logger.debug("未找到推文内容元素");
            return "";
        }
    }
    
    /**
     * 提取发布时间
     */
    private LocalDateTime extractPublishTime(WebElement tweetElement) {
        try {
            WebElement timeElement = tweetElement.findElement(By.cssSelector(TWEET_TIME_SELECTOR));
            String datetime = timeElement.getAttribute("datetime");
            
            if (datetime != null) {
                // Twitter使用ISO 8601格式
                return LocalDateTime.parse(datetime.replace("Z", ""), 
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (Exception e) {
            logger.debug("解析发布时间失败: {}", e.getMessage());
        }
        
        return LocalDateTime.now(); // 默认使用当前时间
    }
    
    /**
     * 提取互动数据（点赞、转发、回复）
     */
    private void extractEngagementData(WebElement tweetElement, Tweet tweet) {
        try {
            // 这里可以添加提取点赞数、转发数、回复数的逻辑
            // Twitter的DOM结构比较复杂，可能需要根据实际页面调整选择器
            
            // 暂时设置为0
            tweet.setLikes(0);
            tweet.setRetweets(0);
            tweet.setReplies(0);
            
        } catch (Exception e) {
            logger.debug("提取互动数据失败: {}", e.getMessage());
        }
    }
    
    /**
     * 获取最新推文
     */
    public Tweet getLatestTweet(WebDriver driver, String username) {
        List<Tweet> tweets = parseTweets(driver, username);
        return tweets.isEmpty() ? null : tweets.get(0);
    }
}
