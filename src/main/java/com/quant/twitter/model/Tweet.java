package com.quant.twitter.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 推文数据模型
 * 封装推文的完整信息
 */
public class Tweet {
    
    private String id;              // 推文ID
    private String username;        // 用户名
    private String content;         // 推文内容
    private String url;             // 推文链接
    private LocalDateTime publishTime;  // 发布时间
    private LocalDateTime detectTime;   // 检测时间
    private int retweets;           // 转发数
    private int likes;              // 点赞数
    private int replies;            // 回复数
    
    public Tweet() {}
    
    public Tweet(String id, String username, String content) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.detectTime = LocalDateTime.now();
        this.url = generateUrl(username, id);
    }
    
    /**
     * 生成推文URL
     */
    private String generateUrl(String username, String tweetId) {
        return String.format("https://twitter.com/%s/status/%s", username, tweetId);
    }
    
    /**
     * 获取推文预览（截取前100个字符）
     */
    public String getPreview() {
        if (content == null || content.isEmpty()) {
            return "[无内容]";
        }
        return content.length() > 100 ? 
            content.substring(0, 100) + "..." : content;
    }
    
    /**
     * 检查是否为有效推文
     */
    public boolean isValid() {
        return id != null && !id.isEmpty() && 
               username != null && !username.isEmpty();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
    
    public LocalDateTime getDetectTime() {
        return detectTime;
    }
    
    public void setDetectTime(LocalDateTime detectTime) {
        this.detectTime = detectTime;
    }
    
    public int getRetweets() {
        return retweets;
    }
    
    public void setRetweets(int retweets) {
        this.retweets = retweets;
    }
    
    public int getLikes() {
        return likes;
    }
    
    public void setLikes(int likes) {
        this.likes = likes;
    }
    
    public int getReplies() {
        return replies;
    }
    
    public void setReplies(int replies) {
        this.replies = replies;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(id, tweet.id) && Objects.equals(username, tweet.username);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
    
    @Override
    public String toString() {
        return "Tweet{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", content='" + getPreview() + '\'' +
                ", url='" + url + '\'' +
                ", publishTime=" + publishTime +
                ", detectTime=" + detectTime +
                ", retweets=" + retweets +
                ", likes=" + likes +
                ", replies=" + replies +
                '}';
    }
}
