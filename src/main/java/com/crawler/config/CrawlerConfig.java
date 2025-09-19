package com.crawler.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 爬虫配置类
 * 管理爬虫的各种配置参数
 */
public class CrawlerConfig {
    
    private int timeout = 10000; // 默认10秒超时
    private int retryCount = 3; // 默认重试3次
    private int delayBetweenRequests = 1000; // 默认请求间隔1秒
    private int maxConcurrentRequests = 5; // 默认最大并发请求数
    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private Map<String, String> defaultHeaders = new HashMap<>();
    
    public CrawlerConfig() {
        initDefaultHeaders();
    }
    
    private void initDefaultHeaders() {
        defaultHeaders.put("User-Agent", userAgent);
        defaultHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        defaultHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        defaultHeaders.put("Accept-Encoding", "gzip, deflate");
        defaultHeaders.put("Connection", "keep-alive");
    }
    
    // Getters and Setters
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    public int getDelayBetweenRequests() {
        return delayBetweenRequests;
    }
    
    public void setDelayBetweenRequests(int delayBetweenRequests) {
        this.delayBetweenRequests = delayBetweenRequests;
    }
    
    public int getMaxConcurrentRequests() {
        return maxConcurrentRequests;
    }
    
    public void setMaxConcurrentRequests(int maxConcurrentRequests) {
        this.maxConcurrentRequests = maxConcurrentRequests;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        this.defaultHeaders.put("User-Agent", userAgent);
    }
    
    public Map<String, String> getDefaultHeaders() {
        return new HashMap<>(defaultHeaders);
    }
    
    public void addHeader(String key, String value) {
        defaultHeaders.put(key, value);
    }
    
    public void removeHeader(String key) {
        defaultHeaders.remove(key);
    }
}
