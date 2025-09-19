package com.quant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Twitter配置类
 * 管理Twitter监控相关的配置参数
 */
@Component
@ConfigurationProperties(prefix = "twitter.monitor")
public class TwitterConfig {
    
    private boolean enabled = true;
    private String targetUser = "xiaozhaolucky";
    private String targetUrl = "https://x.com/xiaozhaolucky";
    private int checkInterval = 30; // 秒
    private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36";
    private int timeout = 10000; // 毫秒
    
    // Getters and Setters
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getTargetUser() {
        return targetUser;
    }
    
    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }
    
    public String getTargetUrl() {
        return targetUrl;
    }
    
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
    
    public int getCheckInterval() {
        return checkInterval;
    }
    
    public void setCheckInterval(int checkInterval) {
        this.checkInterval = checkInterval;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    @Override
    public String toString() {
        return "TwitterConfig{" +
                "enabled=" + enabled +
                ", targetUser='" + targetUser + '\'' +
                ", targetUrl='" + targetUrl + '\'' +
                ", checkInterval=" + checkInterval +
                ", userAgent='" + userAgent + '\'' +
                ", timeout=" + timeout +
                '}';
    }
}
