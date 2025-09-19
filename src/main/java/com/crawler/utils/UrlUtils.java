package com.crawler.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * URL工具类
 * 提供URL相关的工具方法
 */
public class UrlUtils {
    
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?$"
    );
    
    /**
     * 验证URL格式是否正确
     * @param url URL字符串
     * @return 是否有效
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        try {
            new URL(url);
            return URL_PATTERN.matcher(url).matches();
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * 获取域名
     * @param url URL字符串
     * @return 域名
     */
    public static String getDomain(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    /**
     * 获取协议
     * @param url URL字符串
     * @return 协议（http/https）
     */
    public static String getProtocol(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getProtocol();
        } catch (MalformedURLException e) {
            return null;
        }
    }
    
    /**
     * 构建绝对URL
     * @param baseUrl 基础URL
     * @param relativeUrl 相对URL
     * @return 绝对URL
     */
    public static String buildAbsoluteUrl(String baseUrl, String relativeUrl) {
        try {
            URL base = new URL(baseUrl);
            URL absolute = new URL(base, relativeUrl);
            return absolute.toString();
        } catch (MalformedURLException e) {
            return relativeUrl;
        }
    }
    
    /**
     * 清理URL，移除锚点和查询参数
     * @param url 原始URL
     * @return 清理后的URL
     */
    public static String cleanUrl(String url) {
        try {
            URL urlObj = new URL(url);
            return urlObj.getProtocol() + "://" + urlObj.getHost() + urlObj.getPath();
        } catch (MalformedURLException e) {
            return url;
        }
    }
}
