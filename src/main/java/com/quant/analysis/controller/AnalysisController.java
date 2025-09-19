package com.quant.analysis.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quant.analysis.service.TechnicalAnalysisService;
import com.quant.futu.service.FutuDataService;

/**
 * 技术分析控制器
 * 提供技术分析相关的REST API接口
 */
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);
    
    @Autowired
    private TechnicalAnalysisService analysisService;
    
    @Autowired
    private FutuDataService futuDataService;
    
    /**
     * 获取综合技术分析
     * @param symbol 股票代码
     * @return 分析结果
     */
    @GetMapping("/{symbol}")
    public ResponseEntity<Map<String, Object>> getComprehensiveAnalysis(@PathVariable String symbol) {
        logger.info("API请求: 获取综合技术分析 {}", symbol);
        
        try {
            Map<String, Object> analysis = analysisService.comprehensiveAnalysis(symbol);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("获取技术分析失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取简单移动平均线
     * @param symbol 股票代码
     * @param period 周期
     * @return SMA值
     */
    @GetMapping("/{symbol}/sma")
    public ResponseEntity<BigDecimal> getSMA(@PathVariable String symbol, 
                                           @RequestParam(defaultValue = "20") int period) {
        logger.info("API请求: 获取SMA {} 周期: {}", symbol, period);
        
        try {
            BigDecimal sma = analysisService.calculateSMA(symbol, period);
            if (sma != null) {
                return ResponseEntity.ok(sma);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("计算SMA失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取指数移动平均线
     * @param symbol 股票代码
     * @param period 周期
     * @return EMA值
     */
    @GetMapping("/{symbol}/ema")
    public ResponseEntity<BigDecimal> getEMA(@PathVariable String symbol, 
                                           @RequestParam(defaultValue = "12") int period) {
        logger.info("API请求: 获取EMA {} 周期: {}", symbol, period);
        
        try {
            BigDecimal ema = analysisService.calculateEMA(symbol, period);
            if (ema != null) {
                return ResponseEntity.ok(ema);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("计算EMA失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取相对强弱指标
     * @param symbol 股票代码
     * @param period 周期
     * @return RSI值
     */
    @GetMapping("/{symbol}/rsi")
    public ResponseEntity<BigDecimal> getRSI(@PathVariable String symbol, 
                                           @RequestParam(defaultValue = "14") int period) {
        logger.info("API请求: 获取RSI {} 周期: {}", symbol, period);
        
        try {
            BigDecimal rsi = analysisService.calculateRSI(symbol, period);
            if (rsi != null) {
                return ResponseEntity.ok(rsi);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("计算RSI失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取MACD指标
     * @param symbol 股票代码
     * @return MACD结果
     */
    @GetMapping("/{symbol}/macd")
    public ResponseEntity<Map<String, BigDecimal>> getMACD(@PathVariable String symbol) {
        logger.info("API请求: 获取MACD {}", symbol);
        
        try {
            Map<String, BigDecimal> macd = analysisService.calculateMACD(symbol);
            if (!macd.isEmpty()) {
                return ResponseEntity.ok(macd);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("计算MACD失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取布林带
     * @param symbol 股票代码
     * @param period 周期
     * @param stdDev 标准差倍数
     * @return 布林带结果
     */
    @GetMapping("/{symbol}/bollinger")
    public ResponseEntity<Map<String, BigDecimal>> getBollingerBands(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "20") int period,
            @RequestParam(defaultValue = "2.0") double stdDev) {
        
        logger.info("API请求: 获取布林带 {} 周期: {} 标准差: {}", symbol, period, stdDev);
        
        try {
            Map<String, BigDecimal> bollinger = analysisService.calculateBollingerBands(symbol, period, stdDev);
            if (!bollinger.isEmpty()) {
                return ResponseEntity.ok(bollinger);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("计算布林带失败: {}, 错误: {}", symbol, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 获取富途连接状态
     * @return 连接状态
     */
    @GetMapping("/futu/status")
    public ResponseEntity<Map<String, Object>> getFutuStatus() {
        logger.info("API请求: 获取富途连接状态");
        
        Map<String, Object> status = Map.of(
            "connected", futuDataService.isConnected(),
            "timestamp", java.time.LocalDateTime.now()
        );
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * 初始化富途连接
     * @return 操作结果
     */
    @GetMapping("/futu/connect")
    public ResponseEntity<String> connectFutu() {
        logger.info("API请求: 初始化富途连接");
        
        try {
            futuDataService.initConnection();
            if (futuDataService.isConnected()) {
                return ResponseEntity.ok("富途API连接成功");
            } else {
                return ResponseEntity.badRequest().body("富途API连接失败");
            }
        } catch (Exception e) {
            logger.error("初始化富途连接失败: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("连接失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查接口
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Technical Analysis Service is running");
    }
}
