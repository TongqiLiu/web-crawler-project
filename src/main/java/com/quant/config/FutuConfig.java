package com.quant.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 富途OpenAPI配置类
 * 管理富途API连接和认证配置
 */
@Component
@ConfigurationProperties(prefix = "futu.api")
public class FutuConfig {
    
    private boolean enabled = true;
    private String host = "127.0.0.1";
    private int port = 11111;
    private boolean enableEncrypt = false;
    private String keyFile = "";
    private int connectionTimeout = 3000;
    private int keepAliveInterval = 10;
    private String userAgent = "StockQuantSystem/1.0";
    
    // 市场配置
    private boolean enableUsStock = true;
    private boolean enableHkStock = false;
    private boolean enableCnStock = false;
    
    // 数据订阅配置
    private int maxSubscriptions = 100;
    private boolean enableRealtimeQuote = true;
    private boolean enableOrderBook = false;
    private boolean enableTicker = false;
    
    // 构造函数
    public FutuConfig() {
    }
    
    // Getter和Setter方法
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public boolean isEnableEncrypt() {
        return enableEncrypt;
    }
    
    public void setEnableEncrypt(boolean enableEncrypt) {
        this.enableEncrypt = enableEncrypt;
    }
    
    public String getKeyFile() {
        return keyFile;
    }
    
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }
    
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public boolean isEnableUsStock() {
        return enableUsStock;
    }
    
    public void setEnableUsStock(boolean enableUsStock) {
        this.enableUsStock = enableUsStock;
    }
    
    public boolean isEnableHkStock() {
        return enableHkStock;
    }
    
    public void setEnableHkStock(boolean enableHkStock) {
        this.enableHkStock = enableHkStock;
    }
    
    public boolean isEnableCnStock() {
        return enableCnStock;
    }
    
    public void setEnableCnStock(boolean enableCnStock) {
        this.enableCnStock = enableCnStock;
    }
    
    public int getMaxSubscriptions() {
        return maxSubscriptions;
    }
    
    public void setMaxSubscriptions(int maxSubscriptions) {
        this.maxSubscriptions = maxSubscriptions;
    }
    
    public boolean isEnableRealtimeQuote() {
        return enableRealtimeQuote;
    }
    
    public void setEnableRealtimeQuote(boolean enableRealtimeQuote) {
        this.enableRealtimeQuote = enableRealtimeQuote;
    }
    
    public boolean isEnableOrderBook() {
        return enableOrderBook;
    }
    
    public void setEnableOrderBook(boolean enableOrderBook) {
        this.enableOrderBook = enableOrderBook;
    }
    
    public boolean isEnableTicker() {
        return enableTicker;
    }
    
    public void setEnableTicker(boolean enableTicker) {
        this.enableTicker = enableTicker;
    }
    
    /**
     * 获取完整的连接地址
     * @return 连接地址
     */
    public String getConnectionUrl() {
        return String.format("%s:%d", host, port);
    }
    
    /**
     * 验证配置是否有效
     * @return 配置是否有效
     */
    public boolean isValid() {
        return enabled && 
               host != null && !host.trim().isEmpty() && 
               port > 0 && port <= 65535;
    }
    
    @Override
    public String toString() {
        return "FutuConfig{" +
                "enabled=" + enabled +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", enableEncrypt=" + enableEncrypt +
                ", connectionTimeout=" + connectionTimeout +
                ", keepAliveInterval=" + keepAliveInterval +
                ", enableUsStock=" + enableUsStock +
                ", enableHkStock=" + enableHkStock +
                ", enableCnStock=" + enableCnStock +
                ", maxSubscriptions=" + maxSubscriptions +
                ", enableRealtimeQuote=" + enableRealtimeQuote +
                '}';
    }
}
