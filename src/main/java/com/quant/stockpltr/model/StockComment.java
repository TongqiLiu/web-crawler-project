package com.quant.stockpltr.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * StockPltr网站评论数据模型
 * 存储从stockpltr.com爬取的股票相关评论
 */
@Entity
@Table(name = "stockpltr_comments")
public class StockComment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "stock_symbol", nullable = false, length = 20)
    private String stockSymbol; // 关联的股票代码
    
    @Column(name = "comment_id", length = 100)
    private String commentId; // 评论ID
    
    @Column(name = "user_name", length = 100)
    private String userName; // 用户名
    
    @Column(name = "user_avatar", length = 500)
    private String userAvatar; // 用户头像URL
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // 评论内容
    
    @Column(name = "likes_count")
    private Integer likesCount = 0; // 点赞数
    
    @Column(name = "replies_count")
    private Integer repliesCount = 0; // 回复数
    
    @Column(name = "sentiment", length = 20)
    private String sentiment; // 情感分析：positive, negative, neutral
    
    @Column(name = "sentiment_score")
    private Double sentimentScore; // 情感分数 (-1.0 到 1.0)
    
    @Column(name = "comment_time")
    private LocalDateTime commentTime; // 评论时间
    
    @Column(name = "data_source", length = 50)
    private String dataSource = "stockpltr"; // 数据源
    
    @Column(name = "crawl_time", nullable = false)
    private LocalDateTime crawlTime; // 爬取时间
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 创建时间
    
    // 构造函数
    public StockComment() {
        this.createdAt = LocalDateTime.now();
        this.crawlTime = LocalDateTime.now();
    }
    
    public StockComment(String stockSymbol, String commentId) {
        this();
        this.stockSymbol = stockSymbol;
        this.commentId = commentId;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStockSymbol() {
        return stockSymbol;
    }
    
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    
    public String getCommentId() {
        return commentId;
    }
    
    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }
    
    public Integer getRepliesCount() {
        return repliesCount;
    }
    
    public void setRepliesCount(Integer repliesCount) {
        this.repliesCount = repliesCount;
    }
    
    public String getSentiment() {
        return sentiment;
    }
    
    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }
    
    public Double getSentimentScore() {
        return sentimentScore;
    }
    
    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
    
    public LocalDateTime getCommentTime() {
        return commentTime;
    }
    
    public void setCommentTime(LocalDateTime commentTime) {
        this.commentTime = commentTime;
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
    
    @Override
    public String toString() {
        return "StockComment{" +
                "id=" + id +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", commentId='" + commentId + '\'' +
                ", userName='" + userName + '\'' +
                ", content='" + (content != null ? content.substring(0, Math.min(50, content.length())) + "..." : null) + '\'' +
                ", likesCount=" + likesCount +
                ", sentiment='" + sentiment + '\'' +
                ", commentTime=" + commentTime +
                '}';
    }
}
