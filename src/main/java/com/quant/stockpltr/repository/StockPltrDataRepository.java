package com.quant.stockpltr.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quant.stockpltr.model.StockPltrData;

/**
 * StockPltr数据仓库接口
 */
@Repository
public interface StockPltrDataRepository extends JpaRepository<StockPltrData, Long> {
    
    /**
     * 根据股票代码查找最新数据
     */
    @Query("SELECT s FROM StockPltrData s WHERE s.symbol = :symbol ORDER BY s.crawlTime DESC")
    Optional<StockPltrData> findLatestBySymbol(@Param("symbol") String symbol);
    
    /**
     * 根据股票代码查找所有数据
     */
    List<StockPltrData> findBySymbolOrderByCrawlTimeDesc(String symbol);
    
    /**
     * 根据股票代码和数据源查找数据
     */
    List<StockPltrData> findBySymbolAndDataSourceOrderByCrawlTimeDesc(String symbol, String dataSource);
    
    /**
     * 查找指定时间范围内的数据
     */
    @Query("SELECT s FROM StockPltrData s WHERE s.symbol = :symbol AND s.crawlTime BETWEEN :startTime AND :endTime ORDER BY s.crawlTime DESC")
    List<StockPltrData> findBySymbolAndCrawlTimeBetween(
        @Param("symbol") String symbol,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 查找最近更新的数据
     */
    @Query("SELECT s FROM StockPltrData s WHERE s.crawlTime >= :since ORDER BY s.crawlTime DESC")
    List<StockPltrData> findRecentData(@Param("since") LocalDateTime since);
    
    /**
     * 统计股票数据数量
     */
    long countBySymbol(String symbol);
    
    /**
     * 删除指定时间之前的数据
     */
    void deleteByCrawlTimeBefore(LocalDateTime beforeTime);
}
