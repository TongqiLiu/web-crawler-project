package com.quant.stock.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 股票数据实体类
 * 存储股票的基本信息和价格数据
 */
@Entity
@Table(name = "stock_data")
public class StockData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String symbol;
    
    @Column(length = 100)
    private String name;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal openPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal highPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal lowPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal previousClose;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal changePercent;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal volume;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal marketCap;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal peRatio;
    
    @Column(precision = 8, scale = 4)
    private BigDecimal dividendYield;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fiftyTwoWeekHigh;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal fiftyTwoWeekLow;
    
    @Column(nullable = false)
    private LocalDateTime lastUpdated;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    // 默认构造函数
    public StockData() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }
    
    // 带参数构造函数
    public StockData(String symbol, String name) {
        this();
        this.symbol = symbol;
        this.name = name;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public BigDecimal getOpenPrice() {
        return openPrice;
    }
    
    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }
    
    public BigDecimal getHighPrice() {
        return highPrice;
    }
    
    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }
    
    public BigDecimal getLowPrice() {
        return lowPrice;
    }
    
    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }
    
    public BigDecimal getPreviousClose() {
        return previousClose;
    }
    
    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }
    
    public BigDecimal getChangePercent() {
        return changePercent;
    }
    
    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }
    
    public BigDecimal getVolume() {
        return volume;
    }
    
    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }
    
    public BigDecimal getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
    
    public BigDecimal getPeRatio() {
        return peRatio;
    }
    
    public void setPeRatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }
    
    public BigDecimal getDividendYield() {
        return dividendYield;
    }
    
    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }
    
    public BigDecimal getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }
    
    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }
    
    public BigDecimal getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }
    
    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * 计算价格变化
     * @return 价格变化金额
     */
    public BigDecimal getPriceChange() {
        if (currentPrice != null && previousClose != null) {
            return currentPrice.subtract(previousClose);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 更新最后更新时间
     */
    @PreUpdate
    public void updateLastModified() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "StockData{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice=" + currentPrice +
                ", changePercent=" + changePercent +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
