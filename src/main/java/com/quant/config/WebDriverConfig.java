package com.quant.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * WebDriver配置类
 * 管理Selenium WebDriver的创建和配置
 */
@Configuration
public class WebDriverConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDriverConfig.class);
    
    @Value("${webdriver.headless:true}")
    private boolean headless;
    
    @Value("${webdriver.user-agent:Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36}")
    private String userAgent;
    
    @Value("${webdriver.timeout:30}")
    private int timeout;
    
    @Value("${webdriver.window-size:1920,1080}")
    private String windowSize;
    
    private WebDriver webDriver;
    
    @PostConstruct
    public void init() {
        // 设置Chrome驱动
        WebDriverManager.chromedriver().setup();
        logger.info("WebDriver配置初始化完成");
    }
    
    /**
     * 创建WebDriver实例
     */
    public WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        
        // 基本配置
        if (headless) {
            options.addArguments("--headless");
        }
        
        // 性能优化配置
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images");
        options.addArguments("--disable-javascript");
        
        // 反检测配置
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--user-agent=" + userAgent);
        options.addArguments("--window-size=" + windowSize);
        
        // 网络配置
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        
        // 避免检测
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        try {
            WebDriver driver = new ChromeDriver(options);
            logger.info("WebDriver创建成功，配置: headless={}, userAgent={}", headless, userAgent);
            return driver;
        } catch (Exception e) {
            logger.error("创建WebDriver失败: {}", e.getMessage());
            throw new RuntimeException("WebDriver初始化失败", e);
        }
    }
    
    /**
     * 获取单例WebDriver
     */
    @Bean
    public WebDriver webDriver() {
        if (webDriver == null) {
            webDriver = createWebDriver();
        }
        return webDriver;
    }
    
    @PreDestroy
    public void cleanup() {
        if (webDriver != null) {
            try {
                webDriver.quit();
                logger.info("WebDriver已关闭");
            } catch (Exception e) {
                logger.error("关闭WebDriver时发生错误: {}", e.getMessage());
            }
        }
    }
}
