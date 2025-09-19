package com.quant.stockpltr.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quant.stockpltr.model.StockComment;

/**
 * StockPltr评论数据仓库接口
 */
@Repository
public interface StockCommentRepository extends JpaRepository<StockComment, Long> {
    
    /**
     * 根据股票代码查找评论
     */
    List<StockComment> findByStockSymbolOrderByCommentTimeDesc(String stockSymbol);
    
    /**
     * 根据股票代码和评论ID查找评论
     */
    Optional<StockComment> findByStockSymbolAndCommentId(String stockSymbol, String commentId);
    
    /**
     * 根据股票代码和数据源查找评论
     */
    List<StockComment> findByStockSymbolAndDataSourceOrderByCommentTimeDesc(String stockSymbol, String dataSource);
    
    /**
     * 查找指定时间范围内的评论
     */
    @Query("SELECT c FROM StockComment c WHERE c.stockSymbol = :stockSymbol AND c.commentTime BETWEEN :startTime AND :endTime ORDER BY c.commentTime DESC")
    List<StockComment> findByStockSymbolAndCommentTimeBetween(
        @Param("stockSymbol") String stockSymbol,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 根据情感分析结果查找评论
     */
    List<StockComment> findByStockSymbolAndSentimentOrderByCommentTimeDesc(String stockSymbol, String sentiment);
    
    /**
     * 查找热门评论（按点赞数排序）
     */
    @Query("SELECT c FROM StockComment c WHERE c.stockSymbol = :stockSymbol ORDER BY c.likesCount DESC, c.commentTime DESC")
    List<StockComment> findPopularComments(@Param("stockSymbol") String stockSymbol);
    
    /**
     * 统计股票评论数量
     */
    long countByStockSymbol(String stockSymbol);
    
    /**
     * 统计情感分析结果
     */
    @Query("SELECT c.sentiment, COUNT(c) FROM StockComment c WHERE c.stockSymbol = :stockSymbol GROUP BY c.sentiment")
    List<Object[]> countByStockSymbolAndSentiment(@Param("stockSymbol") String stockSymbol);
    
    /**
     * 查找最近评论
     */
    @Query("SELECT c FROM StockComment c WHERE c.commentTime >= :since ORDER BY c.commentTime DESC")
    List<StockComment> findRecentComments(@Param("since") LocalDateTime since);
    
    /**
     * 删除指定时间之前的评论
     */
    void deleteByCrawlTimeBefore(LocalDateTime beforeTime);
}
