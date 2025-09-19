package com.quant.web.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.quant.analysis.service.TechnicalAnalysisService;
import com.quant.futu.service.FutuDataService;
import com.quant.stock.service.StockDataService;

/**
 * Web界面控制器
 * 提供股票量化分析系统的Web界面
 */
@Controller
public class WebController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    
    @Autowired
    private StockDataService stockDataService;
    
    @Autowired
    private TechnicalAnalysisService analysisService;
    
    @Autowired
    private FutuDataService futuDataService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index(Model model) {
        logger.info("访问首页");
        
        try {
            // 获取活跃股票列表
            List<String> symbols = stockDataService.getAllActiveSymbols();
            if (symbols.isEmpty()) {
                // 如果没有数据，使用默认股票列表
                symbols = List.of("AAPL", "GOOGL", "MSFT", "AMZN", "TSLA", "META", "NVDA");
            }
            
            model.addAttribute("symbols", symbols);
            model.addAttribute("futuConnected", futuDataService.isConnected());
            model.addAttribute("title", "股票量化分析系统");
            
            return "index";
            
        } catch (Exception e) {
            logger.error("加载首页失败: {}", e.getMessage(), e);
            model.addAttribute("error", "加载数据失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 股票详情页面
     */
    @GetMapping("/stock/{symbol}")
    public String stockDetail(@PathVariable String symbol, Model model) {
        logger.info("访问股票详情页: {}", symbol);
        
        try {
            // 获取股票数据
            var stockData = stockDataService.getStockData(symbol);
            
            // 获取技术分析结果
            Map<String, Object> analysis = analysisService.comprehensiveAnalysis(symbol);
            
            model.addAttribute("symbol", symbol.toUpperCase());
            model.addAttribute("stockData", stockData.orElse(null));
            model.addAttribute("analysis", analysis);
            model.addAttribute("title", symbol.toUpperCase() + " - 技术分析");
            
            return "stock-detail";
            
        } catch (Exception e) {
            logger.error("加载股票详情失败: {}, 错误: {}", symbol, e.getMessage(), e);
            model.addAttribute("error", "加载股票数据失败: " + e.getMessage());
            model.addAttribute("symbol", symbol);
            return "error";
        }
    }
    
    /**
     * 技术分析页面
     */
    @GetMapping("/analysis")
    public String analysis(@RequestParam(defaultValue = "AAPL") String symbol, Model model) {
        logger.info("访问技术分析页: {}", symbol);
        
        try {
            // 获取综合技术分析
            Map<String, Object> analysis = analysisService.comprehensiveAnalysis(symbol);
            
            // 获取活跃股票列表
            List<String> symbols = stockDataService.getAllActiveSymbols();
            
            model.addAttribute("symbol", symbol.toUpperCase());
            model.addAttribute("analysis", analysis);
            model.addAttribute("symbols", symbols);
            model.addAttribute("title", "技术分析 - " + symbol.toUpperCase());
            
            return "analysis";
            
        } catch (Exception e) {
            logger.error("加载技术分析失败: {}, 错误: {}", symbol, e.getMessage(), e);
            model.addAttribute("error", "技术分析失败: " + e.getMessage());
            model.addAttribute("symbol", symbol);
            return "error";
        }
    }
    
    /**
     * Twitter监控页面
     */
    @GetMapping("/twitter")
    public String twitter(Model model) {
        logger.info("访问Twitter监控页");
        
        model.addAttribute("title", "Twitter监控");
        model.addAttribute("targetUser", "xiaozhaolucky");
        
        return "twitter";
    }
    
    /**
     * 系统设置页面
     */
    @GetMapping("/settings")
    public String settings(Model model) {
        logger.info("访问系统设置页");
        
        model.addAttribute("title", "系统设置");
        model.addAttribute("futuConnected", futuDataService.isConnected());
        
        return "settings";
    }
    
    /**
     * 关于页面
     */
    @GetMapping("/about")
    public String about(Model model) {
        logger.info("访问关于页");
        
        model.addAttribute("title", "关于系统");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("description", "股票量化分析系统");
        
        return "about";
    }
}
