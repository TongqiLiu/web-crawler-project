package com.quant.stockpltr.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.quant.stockpltr.model.StockComment;
import com.quant.stockpltr.model.StockPltrData;
import com.quant.stockpltr.service.StockPltrDataService;

/**
 * StockPltr Web控制器
 * 提供网页界面展示StockPltr数据
 */
@Controller
@RequestMapping("/stockpltr")
public class StockPltrWebController {
    
    @Autowired
    private StockPltrDataService stockPltrDataService;
    
    /**
     * StockPltr主页
     */
    @GetMapping({"", "/"})
    public String index(Model model) {
        model.addAttribute("title", "StockPltr数据监控");
        model.addAttribute("description", "实时监控stockpltr.com的股票数据和评论");
        return "stockpltr/index";
    }
    
    /**
     * 股票列表页面
     */
    @GetMapping("/stocks")
    public String stocks(Model model) {
        // 默认显示一些热门股票
        String[] defaultSymbols = {"AAPL", "TSLA", "PLTR", "NVDA", "MSFT", "GOOGL", "AMZN", "META"};
        model.addAttribute("symbols", defaultSymbols);
        model.addAttribute("title", "股票列表");
        return "stockpltr/stocks";
    }
    
    /**
     * 股票详情页面
     */
    @GetMapping("/stock/{symbol}")
    public String stockDetail(@PathVariable String symbol, Model model) {
        try {
            // 获取股票完整信息
            StockPltrData stockData = stockPltrDataService.getStockFullInfo(symbol);
            
            model.addAttribute("symbol", symbol);
            model.addAttribute("stockData", stockData);
            model.addAttribute("title", symbol + " - 股票详情");
            
            if (stockData != null) {
                model.addAttribute("hasData", true);
                model.addAttribute("comments", stockData.getComments());
            } else {
                model.addAttribute("hasData", false);
                model.addAttribute("error", "无法获取股票数据");
            }
            
        } catch (Exception e) {
            model.addAttribute("symbol", symbol);
            model.addAttribute("hasData", false);
            model.addAttribute("error", "获取数据时发生错误: " + e.getMessage());
            model.addAttribute("title", symbol + " - 股票详情");
        }
        
        return "stockpltr/stock-detail";
    }
    
    /**
     * 股票评论页面
     */
    @GetMapping("/stock/{symbol}/comments")
    public String stockComments(@PathVariable String symbol, Model model) {
        try {
            List<StockComment> comments = stockPltrDataService.getStockComments(symbol);
            
            model.addAttribute("symbol", symbol);
            model.addAttribute("comments", comments);
            model.addAttribute("commentCount", comments.size());
            model.addAttribute("title", symbol + " - 用户评论");
            
        } catch (Exception e) {
            model.addAttribute("symbol", symbol);
            model.addAttribute("comments", List.of());
            model.addAttribute("commentCount", 0);
            model.addAttribute("error", "获取评论时发生错误: " + e.getMessage());
            model.addAttribute("title", symbol + " - 用户评论");
        }
        
        return "stockpltr/comments";
    }
    
    /**
     * 股票历史数据页面
     */
    @GetMapping("/stock/{symbol}/history")
    public String stockHistory(@PathVariable String symbol, 
                              @RequestParam(defaultValue = "30") int days, 
                              Model model) {
        try {
            List<StockPltrData> history = stockPltrDataService.getStockHistory(symbol, days);
            
            model.addAttribute("symbol", symbol);
            model.addAttribute("history", history);
            model.addAttribute("days", days);
            model.addAttribute("title", symbol + " - 历史数据");
            
        } catch (Exception e) {
            model.addAttribute("symbol", symbol);
            model.addAttribute("history", List.of());
            model.addAttribute("days", days);
            model.addAttribute("error", "获取历史数据时发生错误: " + e.getMessage());
            model.addAttribute("title", symbol + " - 历史数据");
        }
        
        return "stockpltr/history";
    }
    
    /**
     * 股票统计页面
     */
    @GetMapping("/stock/{symbol}/statistics")
    public String stockStatistics(@PathVariable String symbol, Model model) {
        try {
            Object statistics = stockPltrDataService.getCommentStatistics(symbol);
            
            model.addAttribute("symbol", symbol);
            model.addAttribute("statistics", statistics);
            model.addAttribute("title", symbol + " - 数据统计");
            
        } catch (Exception e) {
            model.addAttribute("symbol", symbol);
            model.addAttribute("statistics", null);
            model.addAttribute("error", "获取统计数据时发生错误: " + e.getMessage());
            model.addAttribute("title", symbol + " - 数据统计");
        }
        
        return "stockpltr/statistics";
    }
    
    /**
     * 搜索页面
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("query", q);
        model.addAttribute("title", "股票搜索");
        
        if (q != null && !q.trim().isEmpty()) {
            try {
                StockPltrData searchResult = stockPltrDataService.searchStock(q);
                model.addAttribute("searchResult", searchResult);
                model.addAttribute("hasResult", searchResult != null);
            } catch (Exception e) {
                model.addAttribute("hasResult", false);
                model.addAttribute("error", "搜索时发生错误: " + e.getMessage());
            }
        }
        
        return "stockpltr/search";
    }
    
    /**
     * 调试页面
     */
    @GetMapping("/debug")
    public String debug(Model model) {
        model.addAttribute("title", "StockPltr调试");
        return "stockpltr/debug";
    }
}
