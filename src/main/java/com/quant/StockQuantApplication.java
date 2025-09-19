package com.quant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 股票量化分析系统主应用类
 */
@SpringBootApplication
@EnableScheduling
public class StockQuantApplication {
    
    public static void main(String[] args) {
        System.out.println("🚀 启动股票量化分析系统...");
        System.out.println("📊 系统功能:");
        System.out.println("   - 美股数据采集");
        System.out.println("   - 技术分析指标");
        System.out.println("   - Twitter监控 (@xiaozhaolucky)");
        System.out.println("   - Web界面");
        System.out.println("=".repeat(50));
        
        SpringApplication.run(StockQuantApplication.class, args);
    }
}
