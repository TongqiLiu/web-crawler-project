package com.crawler.examples;

import com.crawler.core.WebCrawler;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 简单爬虫示例
 * 演示如何使用WebCrawler抓取网页内容
 */
public class SimpleCrawlerExample {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleCrawlerExample.class);
    
    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        
        // 示例URL - 可以替换为任何有效的网页地址
        String url = "https://httpbin.org/html";
        
        try {
            logger.info("开始爬虫示例...");
            
            // 验证URL
            if (!crawler.isValidUrl(url)) {
                logger.error("无效的URL: {}", url);
                return;
            }
            
            // 抓取并解析网页
            Document doc = crawler.fetchAndParse(url);
            
            // 提取页面标题
            String title = doc.title();
            logger.info("页面标题: {}", title);
            
            // 提取所有链接
            logger.info("页面链接:");
            doc.select("a[href]").forEach(link -> {
                String href = link.attr("href");
                String text = link.text();
                if (!href.isEmpty()) {
                    logger.info("  - {}: {}", text.isEmpty() ? href : text, href);
                }
            });
            
            // 提取所有图片
            logger.info("页面图片:");
            doc.select("img[src]").forEach(img -> {
                String src = img.attr("src");
                String alt = img.attr("alt");
                logger.info("  - {}: {}", alt.isEmpty() ? "无描述" : alt, src);
            });
            
            logger.info("爬虫示例完成!");
            
        } catch (IOException e) {
            logger.error("爬虫执行失败: {}", e.getMessage(), e);
        }
    }
}
