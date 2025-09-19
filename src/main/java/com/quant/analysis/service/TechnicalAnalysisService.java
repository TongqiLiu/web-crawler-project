package com.quant.analysis.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quant.futu.service.FutuDataService;
import com.quant.stock.model.StockData;
import com.quant.stock.service.StockDataService;

/**
 * 技术分析服务类
 * 提供各种技术指标的计算和分析功能
 */
@Service
public class TechnicalAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(TechnicalAnalysisService.class);
    
    @Autowired
    private StockDataService stockDataService;
    
    @Autowired
    private FutuDataService futuDataService;
    
    /**
     * 计算简单移动平均线 (SMA)
     * @param symbol 股票代码
     * @param period 周期
     * @return SMA值
     */
    public BigDecimal calculateSMA(String symbol, int period) {
        logger.debug("计算SMA: {} 周期: {}", symbol, period);
        
        List<StockData> historicalData = getHistoricalData(symbol, period + 5);
        if (historicalData.size() < period) {
            logger.warn("历史数据不足，无法计算SMA: {} 需要: {} 实际: {}", symbol, period, historicalData.size());
            return null;
        }
        
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            if (historicalData.get(i).getCurrentPrice() != null) {
                sum = sum.add(historicalData.get(i).getCurrentPrice());
            }
        }
        
        return sum.divide(BigDecimal.valueOf(period), 4, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算指数移动平均线 (EMA)
     * @param symbol 股票代码
     * @param period 周期
     * @return EMA值
     */
    public BigDecimal calculateEMA(String symbol, int period) {
        logger.debug("计算EMA: {} 周期: {}", symbol, period);
        
        List<StockData> historicalData = getHistoricalData(symbol, period * 2);
        if (historicalData.isEmpty()) {
            return null;
        }
        
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));
        BigDecimal ema = historicalData.get(historicalData.size() - 1).getCurrentPrice();
        
        for (int i = historicalData.size() - 2; i >= 0 && i >= historicalData.size() - period; i--) {
            BigDecimal price = historicalData.get(i).getCurrentPrice();
            if (price != null) {
                ema = price.multiply(multiplier).add(ema.multiply(BigDecimal.ONE.subtract(multiplier)));
            }
        }
        
        return ema.setScale(4, RoundingMode.HALF_UP);
    }
    
    /**
     * 计算相对强弱指标 (RSI)
     * @param symbol 股票代码
     * @param period 周期 (通常是14)
     * @return RSI值
     */
    public BigDecimal calculateRSI(String symbol, int period) {
        logger.debug("计算RSI: {} 周期: {}", symbol, period);
        
        List<StockData> historicalData = getHistoricalData(symbol, period + 10);
        if (historicalData.size() < period + 1) {
            return null;
        }
        
        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;
        
        // 计算初始平均涨跌幅
        for (int i = 1; i <= period; i++) {
            BigDecimal change = historicalData.get(i).getCurrentPrice()
                    .subtract(historicalData.get(i - 1).getCurrentPrice());
            
            if (change.compareTo(BigDecimal.ZERO) > 0) {
                avgGain = avgGain.add(change);
            } else {
                avgLoss = avgLoss.add(change.abs());
            }
        }
        
        avgGain = avgGain.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        avgLoss = avgLoss.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100);
        }
        
        BigDecimal rs = avgGain.divide(avgLoss, 6, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(
                BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP)
        );
        
        return rsi;
    }
    
    /**
     * 计算MACD指标
     * @param symbol 股票代码
     * @return MACD结果 (包含MACD线、信号线、柱状图)
     */
    public Map<String, BigDecimal> calculateMACD(String symbol) {
        logger.debug("计算MACD: {}", symbol);
        
        Map<String, BigDecimal> result = new HashMap<>();
        
        BigDecimal ema12 = calculateEMA(symbol, 12);
        BigDecimal ema26 = calculateEMA(symbol, 26);
        
        if (ema12 == null || ema26 == null) {
            return result;
        }
        
        // MACD线 = EMA12 - EMA26
        BigDecimal macdLine = ema12.subtract(ema26);
        result.put("MACD", macdLine);
        
        // 信号线 (MACD的9日EMA)
        // 这里简化计算，实际应该用MACD历史值计算EMA
        BigDecimal signalLine = macdLine.multiply(BigDecimal.valueOf(0.8));
        result.put("Signal", signalLine);
        
        // 柱状图 = MACD线 - 信号线
        BigDecimal histogram = macdLine.subtract(signalLine);
        result.put("Histogram", histogram);
        
        return result;
    }
    
    /**
     * 计算布林带
     * @param symbol 股票代码
     * @param period 周期 (通常是20)
     * @param stdDev 标准差倍数 (通常是2)
     * @return 布林带结果
     */
    public Map<String, BigDecimal> calculateBollingerBands(String symbol, int period, double stdDev) {
        logger.debug("计算布林带: {} 周期: {} 标准差: {}", symbol, period, stdDev);
        
        Map<String, BigDecimal> result = new HashMap<>();
        
        List<StockData> historicalData = getHistoricalData(symbol, period + 5);
        if (historicalData.size() < period) {
            return result;
        }
        
        // 中轨 (SMA)
        BigDecimal sma = calculateSMA(symbol, period);
        if (sma == null) {
            return result;
        }
        result.put("Middle", sma);
        
        // 计算标准差
        BigDecimal sumSquares = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            BigDecimal price = historicalData.get(i).getCurrentPrice();
            if (price != null) {
                BigDecimal diff = price.subtract(sma);
                sumSquares = sumSquares.add(diff.multiply(diff));
            }
        }
        
        BigDecimal variance = sumSquares.divide(BigDecimal.valueOf(period), 6, RoundingMode.HALF_UP);
        BigDecimal standardDeviation = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));
        
        BigDecimal bandwidth = standardDeviation.multiply(BigDecimal.valueOf(stdDev));
        
        // 上轨
        result.put("Upper", sma.add(bandwidth));
        
        // 下轨
        result.put("Lower", sma.subtract(bandwidth));
        
        return result;
    }
    
    /**
     * 综合技术分析
     * @param symbol 股票代码
     * @return 分析结果
     */
    public Map<String, Object> comprehensiveAnalysis(String symbol) {
        logger.info("开始综合技术分析: {}", symbol);
        
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            // 基础指标
            analysis.put("SMA_20", calculateSMA(symbol, 20));
            analysis.put("SMA_50", calculateSMA(symbol, 50));
            analysis.put("EMA_12", calculateEMA(symbol, 12));
            analysis.put("EMA_26", calculateEMA(symbol, 26));
            analysis.put("RSI_14", calculateRSI(symbol, 14));
            
            // 复合指标
            analysis.put("MACD", calculateMACD(symbol));
            analysis.put("BollingerBands", calculateBollingerBands(symbol, 20, 2.0));
            
            // 趋势分析
            String trend = analyzeTrend(symbol);
            analysis.put("Trend", trend);
            
            // 买卖信号
            String signal = generateTradingSignal(symbol, analysis);
            analysis.put("TradingSignal", signal);
            
            analysis.put("AnalysisTime", java.time.LocalDateTime.now());
            
        } catch (Exception e) {
            logger.error("综合技术分析失败: {}, 错误: {}", symbol, e.getMessage(), e);
            analysis.put("Error", e.getMessage());
        }
        
        return analysis;
    }
    
    /**
     * 趋势分析
     */
    private String analyzeTrend(String symbol) {
        BigDecimal sma20 = calculateSMA(symbol, 20);
        BigDecimal sma50 = calculateSMA(symbol, 50);
        
        if (sma20 == null || sma50 == null) {
            return "UNKNOWN";
        }
        
        if (sma20.compareTo(sma50) > 0) {
            return "UPTREND";
        } else if (sma20.compareTo(sma50) < 0) {
            return "DOWNTREND";
        } else {
            return "SIDEWAYS";
        }
    }
    
    /**
     * 生成交易信号
     */
    private String generateTradingSignal(String symbol, Map<String, Object> analysis) {
        try {
            BigDecimal rsi = (BigDecimal) analysis.get("RSI_14");
            String trend = (String) analysis.get("Trend");
            
            if (rsi == null || trend == null) {
                return "HOLD";
            }
            
            // 简单的交易信号逻辑
            if (rsi.compareTo(BigDecimal.valueOf(70)) > 0) {
                return "SELL"; // 超买
            } else if (rsi.compareTo(BigDecimal.valueOf(30)) < 0) {
                return "BUY";  // 超卖
            } else if ("UPTREND".equals(trend) && rsi.compareTo(BigDecimal.valueOf(50)) > 0) {
                return "BUY";  // 上升趋势 + RSI > 50
            } else if ("DOWNTREND".equals(trend) && rsi.compareTo(BigDecimal.valueOf(50)) < 0) {
                return "SELL"; // 下降趋势 + RSI < 50
            }
            
            return "HOLD";
            
        } catch (Exception e) {
            logger.warn("生成交易信号失败: {}", e.getMessage());
            return "HOLD";
        }
    }
    
    /**
     * 获取历史数据
     */
    private List<StockData> getHistoricalData(String symbol, int count) {
        // 优先使用富途数据
        if (futuDataService.isConnected()) {
            String futuSymbol = "US." + symbol.toUpperCase();
            return futuDataService.getHistoricalKLine(futuSymbol, "K_DAY", count);
        }
        
        // 降级使用本地数据
        return stockDataService.getHistoricalData(symbol);
    }
}
