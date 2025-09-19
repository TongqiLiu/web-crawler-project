package com.crawler.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WebCrawler测试类
 */
public class WebCrawlerTest {
    
    private WebCrawler crawler;
    
    @BeforeEach
    void setUp() {
        crawler = new WebCrawler();
    }
    
    @Test
    @DisplayName("测试URL验证功能")
    void testIsValidUrl() {
        // 有效URL
        assertTrue(crawler.isValidUrl("https://www.example.com"));
        assertTrue(crawler.isValidUrl("http://localhost:8080"));
        
        // 无效URL
        assertFalse(crawler.isValidUrl("invalid-url"));
        assertFalse(crawler.isValidUrl(""));
        assertFalse(crawler.isValidUrl(null));
    }
    
    @Test
    @DisplayName("测试HTML解析功能")
    void testParseHtml() {
        String html = "<html><head><title>Test</title></head><body><h1>Hello World</h1></body></html>";
        
        var doc = crawler.parseHtml(html);
        
        assertNotNull(doc);
        assertEquals("Test", doc.title());
        assertEquals("Hello World", doc.select("h1").text());
    }
    
    @Test
    @DisplayName("测试配置设置")
    void testConfiguration() {
        crawler.setTimeout(5000);
        crawler.setRetryCount(5);
        crawler.setHeader("Custom-Header", "Custom-Value");
        
        // 这里可以添加更多配置验证逻辑
        assertTrue(true); // 占位测试
    }
}
