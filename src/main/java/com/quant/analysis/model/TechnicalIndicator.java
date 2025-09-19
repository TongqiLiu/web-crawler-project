package com.quant.analysis.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 技术指标实体类
 * 存储各种技术分析指标的计算结果
 */
@Entity
@Table(name = "technical_indicators")
public class TechnicalIndicator {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String symbol;
    
    @Column(nullable = false, length = 20)
    private String indicatorType;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal value;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal signal;
    
    @Column
    private Integer period;
    
    @Column(nullable = false)
    private LocalDateTime calculatedAt;
    
    // 构造函数
    public TechnicalIndicator() {
        this.calculatedAt = LocalDateTime.now();
    }
    
    public TechnicalIndicator(String symbol, String indicatorType, BigDecimal value) {
        this();
        this.symbol = symbol;
        this.indicatorType = indicatorType;
        this.value = value;
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
    
    public String getIndicatorType() {
        return indicatorType;
    }
    
    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    
    public BigDecimal getSignal() {
        return signal;
    }
    
    public void setSignal(BigDecimal signal) {
        this.signal = signal;
    }
    
    public Integer getPeriod() {
        return period;
    }
    
    public void setPeriod(Integer period) {
        this.period = period;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
    
    @Override
    public String toString() {
        return "TechnicalIndicator{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", indicatorType='" + indicatorType + '\'' +
                ", value=" + value +
                ", signal=" + signal +
                ", period=" + period +
                ", calculatedAt=" + calculatedAt +
                '}';
    }
}

/**
 * 技术指标类型枚举
 */
enum IndicatorType {
    SMA,    // 简单移动平均线
    EMA,    // 指数移动平均线
    RSI,    // 相对强弱指标
    MACD,   // 移动平均收敛散度
    BB,     // 布林带
    STOCH,  // 随机指标
    ATR,    // 平均真实范围
    CCI,    // 顺势指标
    WR,     // 威廉指标
    OBV     // 成交量平衡指标
}

/**
 * 技术指标计算结果
 */
class IndicatorResult {
    private String indicatorType;
    private List<BigDecimal> values;
    private List<BigDecimal> signals;
    private String interpretation;
    
    public IndicatorResult(String indicatorType) {
        this.indicatorType = indicatorType;
    }
    
    // Getter和Setter方法
    public String getIndicatorType() {
        return indicatorType;
    }
    
    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }
    
    public List<BigDecimal> getValues() {
        return values;
    }
    
    public void setValues(List<BigDecimal> values) {
        this.values = values;
    }
    
    public List<BigDecimal> getSignals() {
        return signals;
    }
    
    public void setSignals(List<BigDecimal> signals) {
        this.signals = signals;
    }
    
    public String getInterpretation() {
        return interpretation;
    }
    
    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }
}
