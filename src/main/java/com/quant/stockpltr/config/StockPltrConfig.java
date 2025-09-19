package com.quant.stockpltr.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * StockPltr配置类
 * 管理StockPltr爬虫相关配置
 */
@Configuration
@ConfigurationProperties(prefix = "stockpltr")
public class StockPltrConfig {
    
    private Crawl crawl = new Crawl();
    private Data data = new Data();
    private Website website = new Website();
    private Monitoring monitoring = new Monitoring();
    
    public static class Crawl {
        private boolean enabled = true;
        private int interval = 300;
        private int batchSize = 10;
        private int timeout = 30;
        private int maxRetries = 3;
        private int delayBetweenRequests = 2000;
        
        // Getter和Setter方法
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public int getInterval() {
            return interval;
        }
        
        public void setInterval(int interval) {
            this.interval = interval;
        }
        
        public int getBatchSize() {
            return batchSize;
        }
        
        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }
        
        public int getTimeout() {
            return timeout;
        }
        
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
        
        public int getMaxRetries() {
            return maxRetries;
        }
        
        public void setMaxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
        }
        
        public int getDelayBetweenRequests() {
            return delayBetweenRequests;
        }
        
        public void setDelayBetweenRequests(int delayBetweenRequests) {
            this.delayBetweenRequests = delayBetweenRequests;
        }
    }
    
    public static class Data {
        private int retentionDays = 30;
        private boolean enableCache = true;
        private int cacheDuration = 300;
        
        // Getter和Setter方法
        public int getRetentionDays() {
            return retentionDays;
        }
        
        public void setRetentionDays(int retentionDays) {
            this.retentionDays = retentionDays;
        }
        
        public boolean isEnableCache() {
            return enableCache;
        }
        
        public void setEnableCache(boolean enableCache) {
            this.enableCache = enableCache;
        }
        
        public int getCacheDuration() {
            return cacheDuration;
        }
        
        public void setCacheDuration(int cacheDuration) {
            this.cacheDuration = cacheDuration;
        }
    }
    
    public static class Website {
        private String baseUrl = "https://www.stockpltr.com";
        private String searchUrl = "https://www.stockpltr.com/search";
        private String stockDetailUrl = "https://www.stockpltr.com/stock";
        
        // Getter和Setter方法
        public String getBaseUrl() {
            return baseUrl;
        }
        
        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }
        
        public String getSearchUrl() {
            return searchUrl;
        }
        
        public void setSearchUrl(String searchUrl) {
            this.searchUrl = searchUrl;
        }
        
        public String getStockDetailUrl() {
            return stockDetailUrl;
        }
        
        public void setStockDetailUrl(String stockDetailUrl) {
            this.stockDetailUrl = stockDetailUrl;
        }
    }
    
    public static class Monitoring {
        private List<String> symbols = List.of("AAPL", "TSLA", "PLTR", "NVDA", "MSFT", "GOOGL", "AMZN", "META");
        
        // Getter和Setter方法
        public List<String> getSymbols() {
            return symbols;
        }
        
        public void setSymbols(List<String> symbols) {
            this.symbols = symbols;
        }
    }
    
    // Getter和Setter方法
    public Crawl getCrawl() {
        return crawl;
    }
    
    public void setCrawl(Crawl crawl) {
        this.crawl = crawl;
    }
    
    public Data getData() {
        return data;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
    
    public Website getWebsite() {
        return website;
    }
    
    public void setWebsite(Website website) {
        this.website = website;
    }
    
    public Monitoring getMonitoring() {
        return monitoring;
    }
    
    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }
}
