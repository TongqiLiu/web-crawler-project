package com.crawler.core;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Web爬虫核心类
 * 提供网页抓取和HTML解析功能
 */
public class WebCrawler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    
    private final Map<String, String> headers;
    private int timeout = 10000; // 10秒超时
    private int retryCount = 3;
    
    public WebCrawler() {
        this.headers = new HashMap<>();
        this.headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
    }
    
    /**
     * 抓取网页内容
     * @param url 目标URL
     * @return 网页HTML内容
     * @throws IOException 网络异常
     */
    public String fetchPage(String url) throws IOException {
        logger.info("开始抓取网页: {}", url);
        
        for (int i = 0; i < retryCount; i++) {
            try {
                Document doc = Jsoup.connect(url)
                    .headers(headers)
                    .timeout(timeout)
                    .get();
                
                logger.info("成功抓取网页: {}", url);
                return doc.html();
                
            } catch (IOException e) {
                logger.warn("第{}次抓取失败: {}, 错误: {}", i + 1, url, e.getMessage());
                if (i == retryCount - 1) {
                    throw e;
                }
                try {
                    Thread.sleep(1000 * (i + 1)); // 递增延迟
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("抓取被中断", ie);
                }
            }
        }
        
        throw new IOException("抓取失败，已达到最大重试次数");
    }
    
    /**
     * 解析HTML内容
     * @param html HTML字符串
     * @return Document对象
     */
    public Document parseHtml(String html) {
        return Jsoup.parse(html);
    }
    
    /**
     * 抓取并解析网页
     * @param url 目标URL
     * @return Document对象
     * @throws IOException 网络异常
     */
    public Document fetchAndParse(String url) throws IOException {
        String html = fetchPage(url);
        return parseHtml(html);
    }
    
    /**
     * 设置请求头
     * @param key 键
     * @param value 值
     */
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }
    
    /**
     * 设置超时时间
     * @param timeout 超时时间（毫秒）
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    /**
     * 设置重试次数
     * @param retryCount 重试次数
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
    /**
     * 验证URL格式
     * @param url URL字符串
     * @return 是否有效
     */
    public boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
