package com.quant.stock.controller;

import com.quant.stock.model.StockData;
import com.quant.stock.service.StockDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 股票数据控制器
 * 提供股票数据的REST API接口
 */
@RestController
@RequestMapping("/api/stocks")
@CrossOrigin(origins = "*")
public class StockController {
    
    private static final Logger logger = LoggerFactory.getLogger(StockController.class);
    
    @Autowired
    private StockDataService stockDataService;
    
    /**
     * 获取股票数据
     * @param symbol 股票代码
     * @return 股票数据
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<StockData> getStock(@PathVariable String symbol) {
        logger.info("API请求: 获取股票数据 {}", symbol);
        
        Optional<StockData> stockData = stockDataService.getStockData(symbol);
        if (stockData.isPresent()) {
            return ResponseEntity.ok(stockData.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 获取股票历史数据
     * @param symbol 股票代码
     * @return 历史数据列表
     */
    @GetMapping("/{symbol}/history")
    public ResponseEntity<List<StockData>> getStockHistory(@PathVariable String symbol) {
        logger.info("API请求: 获取股票历史数据 {}", symbol);
        
        List<StockData> historyData = stockDataService.getHistoricalData(symbol);
        return ResponseEntity.ok(historyData);
    }
    
    /**
     * 获取所有活跃股票代码
     * @return 股票代码列表
     */
    @GetMapping("/symbols")
    public ResponseEntity<List<String>> getAllSymbols() {
        logger.info("API请求: 获取所有股票代码");
        
        List<String> symbols = stockDataService.getAllActiveSymbols();
        return ResponseEntity.ok(symbols);
    }
    
    /**
     * 手动更新股票数据
     * @param symbol 股票代码
     * @return 更新后的股票数据
     */
    @PostMapping("/{symbol}/update")
    public ResponseEntity<StockData> updateStock(@PathVariable String symbol) {
        logger.info("API请求: 手动更新股票数据 {}", symbol);
        
        try {
            StockData updatedData = stockDataService.manualUpdateStockData(symbol);
            if (updatedData != null) {
                return ResponseEntity.ok(updatedData);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("更新股票数据失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 批量更新股票数据
     * @param symbols 股票代码列表
     * @return 操作结果
     */
    @PostMapping("/batch-update")
    public ResponseEntity<String> batchUpdateStocks(@RequestBody List<String> symbols) {
        logger.info("API请求: 批量更新股票数据 {}", symbols);
        
        try {
            for (String symbol : symbols) {
                stockDataService.updateStockDataAsync(symbol);
            }
            return ResponseEntity.ok("批量更新任务已启动，共" + symbols.size() + "只股票");
        } catch (Exception e) {
            logger.error("批量更新股票数据失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("批量更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Stock Data Service is running");
    }
}
