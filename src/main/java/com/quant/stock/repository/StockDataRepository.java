package com.quant.stock.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.quant.stock.model.StockData;

/**
 * 股票数据仓库接口
 * 提供股票数据的数据库操作
 */
@Repository
public interface StockDataRepository extends JpaRepository<StockData, Long> {
    
    /**
     * 根据股票代码查找最新的股票数据
     * @param symbol 股票代码
     * @return 股票数据
     */
    Optional<StockData> findTopBySymbolOrderByLastUpdatedDesc(String symbol);
    
    /**
     * 根据股票代码查找所有历史数据
     * @param symbol 股票代码
     * @return 股票数据列表
     */
    List<StockData> findBySymbolOrderByLastUpdatedDesc(String symbol);
    
    /**
     * 查找指定时间范围内的股票数据
     * @param symbol 股票代码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 股票数据列表
     */
    @Query("SELECT s FROM StockData s WHERE s.symbol = :symbol AND s.lastUpdated BETWEEN :startTime AND :endTime ORDER BY s.lastUpdated DESC")
    List<StockData> findBySymbolAndTimeRange(@Param("symbol") String symbol, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找所有活跃的股票代码
     * @return 股票代码列表
     */
    @Query("SELECT DISTINCT s.symbol FROM StockData s ORDER BY s.symbol")
    List<String> findAllActiveSymbols();
    
    /**
     * 查找最近更新的股票数据
     * @param hours 小时数
     * @return 股票数据列表
     */
    @Query("SELECT s FROM StockData s WHERE s.lastUpdated > :cutoffTime ORDER BY s.lastUpdated DESC")
    List<StockData> findRecentlyUpdated(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 根据涨跌幅查找股票
     * @param minChange 最小涨跌幅
     * @param maxChange 最大涨跌幅
     * @return 股票数据列表
     */
    @Query("SELECT s FROM StockData s WHERE s.changePercent BETWEEN :minChange AND :maxChange ORDER BY s.changePercent DESC")
    List<StockData> findByChangePercentRange(@Param("minChange") Double minChange, 
                                           @Param("maxChange") Double maxChange);
    
    /**
     * 查找高成交量股票
     * @param minVolume 最小成交量
     * @return 股票数据列表
     */
    @Query("SELECT s FROM StockData s WHERE s.volume >= :minVolume ORDER BY s.volume DESC")
    List<StockData> findHighVolumeStocks(@Param("minVolume") Long minVolume);
    
    /**
     * 删除过期的历史数据
     * @param cutoffTime 截止时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM StockData s WHERE s.lastUpdated < :cutoffTime")
    int deleteOldData(@Param("cutoffTime") LocalDateTime cutoffTime);
}
