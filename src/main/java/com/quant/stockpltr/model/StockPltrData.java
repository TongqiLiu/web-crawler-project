package com.quant.stockpltr.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * StockPltr网站数据模型
 * 存储从stockpltr.com爬取的股票相关数据
 */
@Entity
@Table(name = "stockpltr_data")
public class StockPltrData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol; // 股票代码
    
    @Column(name = "company_name", length = 200)
    private String companyName; // 公司名称
    
    @Column(name = "current_price")
    private Double currentPrice; // 当前价格
    
    @Column(name = "price_change")
    private Double priceChange; // 价格变化
    
    @Column(name = "price_change_percent")
    private Double priceChangePercent; // 价格变化百分比
    
    @Column(name = "volume")
    private Long volume; // 成交量
    
    @Column(name = "market_cap")
    private Long marketCap; // 市值
    
    @Column(name = "pe_ratio")
    private Double peRatio; // 市盈率
    
    @Column(name = "pb_ratio")
    private Double pbRatio; // 市净率
    
    @Column(name = "dividend_yield")
    private Double dividendYield; // 股息率
    
    @Column(name = "recommendation", length = 50)
    private String recommendation; // 推荐评级
    
    @Column(name = "target_price")
    private Double targetPrice; // 目标价格
    
    @Column(name = "data_source", length = 50)
    private String dataSource = "stockpltr"; // 数据源
    
    @Column(name = "crawl_time", nullable = false)
    private LocalDateTime crawlTime; // 爬取时间
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 创建时间
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 更新时间
    
    @Transient
    private List<StockComment> comments; // 评论列表（不持久化）
    
    // 构造函数
    public StockPltrData() {
        this.createdAt = LocalDateTime.now();
        this.crawlTime = LocalDateTime.now();
    }
    
    public StockPltrData(String symbol) {
        this();
        this.symbol = symbol;
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
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public Double getCurrentPrice() {
        return currentPrice;
    }
    
    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }
    
    public Double getPriceChange() {
        return priceChange;
    }
    
    public void setPriceChange(Double priceChange) {
        this.priceChange = priceChange;
    }
    
    public Double getPriceChangePercent() {
        return priceChangePercent;
    }
    
    public void setPriceChangePercent(Double priceChangePercent) {
        this.priceChangePercent = priceChangePercent;
    }
    
    public Long getVolume() {
        return volume;
    }
    
    public void setVolume(Long volume) {
        this.volume = volume;
    }
    
    public Long getMarketCap() {
        return marketCap;
    }
    
    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }
    
    public Double getPeRatio() {
        return peRatio;
    }
    
    public void setPeRatio(Double peRatio) {
        this.peRatio = peRatio;
    }
    
    public Double getPbRatio() {
        return pbRatio;
    }
    
    public void setPbRatio(Double pbRatio) {
        this.pbRatio = pbRatio;
    }
    
    public Double getDividendYield() {
        return dividendYield;
    }
    
    public void setDividendYield(Double dividendYield) {
        this.dividendYield = dividendYield;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    public Double getTargetPrice() {
        return targetPrice;
    }
    
    public void setTargetPrice(Double targetPrice) {
        this.targetPrice = targetPrice;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    
    public LocalDateTime getCrawlTime() {
        return crawlTime;
    }
    
    public void setCrawlTime(LocalDateTime crawlTime) {
        this.crawlTime = crawlTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<StockComment> getComments() {
        return comments;
    }
    
    public void setComments(List<StockComment> comments) {
        this.comments = comments;
    }
    
    @Override
    public String toString() {
        return "StockPltrData{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice +
                ", priceChange=" + priceChange +
                ", priceChangePercent=" + priceChangePercent +
                ", volume=" + volume +
                ", marketCap=" + marketCap +
                ", peRatio=" + peRatio +
                ", pbRatio=" + pbRatio +
                ", dividendYield=" + dividendYield +
                ", recommendation='" + recommendation + '\'' +
                ", targetPrice=" + targetPrice +
                ", crawlTime=" + crawlTime +
                '}';
    }
}
