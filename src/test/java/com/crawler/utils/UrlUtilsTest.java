package com.crawler.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UrlUtils测试类
 */
public class UrlUtilsTest {
    
    @Test
    @DisplayName("测试URL验证")
    void testIsValidUrl() {
        // 有效URL
        assertTrue(UrlUtils.isValidUrl("https://www.example.com"));
        assertTrue(UrlUtils.isValidUrl("http://localhost:8080"));
        assertTrue(UrlUtils.isValidUrl("https://example.com/path?param=value"));
        
        // 无效URL
        assertFalse(UrlUtils.isValidUrl("invalid-url"));
        assertFalse(UrlUtils.isValidUrl(""));
        assertFalse(UrlUtils.isValidUrl(null));
        assertFalse(UrlUtils.isValidUrl("ftp://example.com"));
    }
    
    @Test
    @DisplayName("测试域名提取")
    void testGetDomain() {
        assertEquals("www.example.com", UrlUtils.getDomain("https://www.example.com"));
        assertEquals("localhost", UrlUtils.getDomain("http://localhost:8080"));
        assertNull(UrlUtils.getDomain("invalid-url"));
    }
    
    @Test
    @DisplayName("测试协议提取")
    void testGetProtocol() {
        assertEquals("https", UrlUtils.getProtocol("https://www.example.com"));
        assertEquals("http", UrlUtils.getProtocol("http://localhost:8080"));
        assertNull(UrlUtils.getProtocol("invalid-url"));
    }
    
    @Test
    @DisplayName("测试绝对URL构建")
    void testBuildAbsoluteUrl() {
        String baseUrl = "https://www.example.com";
        String relativeUrl = "/path/to/page";
        
        String absoluteUrl = UrlUtils.buildAbsoluteUrl(baseUrl, relativeUrl);
        assertEquals("https://www.example.com/path/to/page", absoluteUrl);
    }
    
    @Test
    @DisplayName("测试URL清理")
    void testCleanUrl() {
        String url = "https://www.example.com/path?param=value#anchor";
        String cleanedUrl = UrlUtils.cleanUrl(url);
        assertEquals("https://www.example.com/path", cleanedUrl);
    }
}
