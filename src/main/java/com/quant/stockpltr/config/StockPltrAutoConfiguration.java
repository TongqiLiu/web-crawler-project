package com.quant.stockpltr.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.quant.stockpltr.service.StockPltrDataService;

/**
 * StockPltr自动配置类
 * 在应用启动时自动初始化StockPltr相关服务
 */
@Component
public class StockPltrAutoConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPltrAutoConfiguration.class);
    
    @Autowired
    private StockPltrConfig stockPltrConfig;
    
    @Autowired
    private StockPltrDataService stockPltrDataService;
    
    /**
     * 应用启动完成后自动初始化
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            logger.info("StockPltr模块初始化开始");
            
            // 检查配置
            if (stockPltrConfig.getCrawl().isEnabled()) {
                logger.info("StockPltr爬虫已启用");
                
                // 启动定时爬取任务
                stockPltrDataService.startScheduledCrawl();
                
                logger.info("StockPltr定时爬取任务已启动，监控股票: {}", 
                    stockPltrConfig.getMonitoring().getSymbols());
            } else {
                logger.info("StockPltr爬虫已禁用");
            }
            
            logger.info("StockPltr模块初始化完成");
            
        } catch (Exception e) {
            logger.error("StockPltr模块初始化失败: {}", e.getMessage());
        }
    }
}
