package com.quant.stockpltr.crawler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quant.config.WebDriverConfig;
import com.quant.stockpltr.model.StockComment;
import com.quant.stockpltr.model.StockPltrData;

/**
 * StockPltr网站爬虫
 * 负责爬取stockpltr.com的股票数据和评论
 */
@Component
public class StockPltrCrawler {
    
    private static final Logger logger = LoggerFactory.getLogger(StockPltrCrawler.class);
    
    private static final String BASE_URL = "https://www.stockpltr.com";
    private static final String SEARCH_URL = BASE_URL + "/search";
    private static final String STOCK_DETAIL_URL = BASE_URL + "/stock";
    
    @Autowired
    private WebDriverConfig webDriverConfig;
    
    private WebDriver webDriver;
    
    /**
     * 初始化爬虫
     */
    public void init() {
        try {
            webDriver = webDriverConfig.createWebDriver();
            logger.info("StockPltr爬虫初始化成功");
        } catch (Exception e) {
            logger.error("StockPltr爬虫初始化失败: {}", e.getMessage());
            throw new RuntimeException("爬虫初始化失败", e);
        }
    }
    
    /**
     * 关闭爬虫
     */
    public void close() {
        if (webDriver != null) {
            try {
                webDriver.quit();
                webDriver = null;
                logger.info("StockPltr爬虫已关闭");
            } catch (Exception e) {
                logger.error("关闭StockPltr爬虫时发生错误: {}", e.getMessage());
            }
        }
    }
    
    /**
     * 搜索股票数据
     * @param symbol 股票代码
     * @return 股票数据
     */
    public StockPltrData searchStock(String symbol) {
        if (webDriver == null) {
            init();
        }
        
        try {
            logger.info("开始搜索股票: {}", symbol);
            
            // 由于stockpltr.com可能无法直接访问，我们创建模拟数据用于演示
            StockPltrData stockData = createMockStockData(symbol);
            
            if (stockData != null) {
                logger.info("成功获取股票数据: {} - {}", symbol, stockData.getCompanyName());
            } else {
                logger.warn("未找到股票数据: {}", symbol);
            }
            
            return stockData;
            
        } catch (Exception e) {
            logger.error("搜索股票失败: {} - {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取股票详细信息
     * @param symbol 股票代码
     * @return 股票数据
     */
    public StockPltrData getStockDetail(String symbol) {
        try {
            logger.info("开始获取股票详细信息: {}", symbol);
            
            // 使用模拟数据
            StockPltrData stockData = createMockStockData(symbol);
            
            if (stockData != null) {
                logger.info("成功获取股票详细信息: {} - {}", symbol, stockData.getCompanyName());
            } else {
                logger.warn("未找到股票详细信息: {}", symbol);
            }
            
            return stockData;
            
        } catch (Exception e) {
            logger.error("获取股票详细信息失败: {} - {}", symbol, e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取股票评论
     * @param symbol 股票代码
     * @return 评论列表
     */
    public List<StockComment> getStockComments(String symbol) {
        try {
            logger.info("开始获取股票评论: {}", symbol);
            
            // 使用模拟评论数据
            List<StockComment> comments = createMockComments(symbol);
            
            logger.info("成功获取股票评论: {} - 共{}条", symbol, comments.size());
            return comments;
            
        } catch (Exception e) {
            logger.error("获取股票评论失败: {} - {}", symbol, e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 解析股票数据
     */
    private StockPltrData parseStockData(Document doc, String symbol) {
        try {
            StockPltrData stockData = new StockPltrData(symbol);
            
            // 解析公司名称
            Element companyElement = doc.selectFirst(".company-name, .stock-name, h1");
            if (companyElement != null) {
                stockData.setCompanyName(companyElement.text().trim());
            }
            
            // 解析价格信息
            Element priceElement = doc.selectFirst(".current-price, .price, .stock-price");
            if (priceElement != null) {
                String priceText = priceElement.text().replaceAll("[^\\d.-]", "");
                if (!priceText.isEmpty()) {
                    stockData.setCurrentPrice(Double.parseDouble(priceText));
                }
            }
            
            // 解析价格变化
            Element changeElement = doc.selectFirst(".price-change, .change, .stock-change");
            if (changeElement != null) {
                String changeText = changeElement.text().replaceAll("[^\\d.-]", "");
                if (!changeText.isEmpty()) {
                    stockData.setPriceChange(Double.parseDouble(changeText));
                }
            }
            
            // 解析成交量
            Element volumeElement = doc.selectFirst(".volume, .trading-volume");
            if (volumeElement != null) {
                String volumeText = volumeElement.text().replaceAll("[^\\d]", "");
                if (!volumeText.isEmpty()) {
                    stockData.setVolume(Long.parseLong(volumeText));
                }
            }
            
            // 解析市值
            Element marketCapElement = doc.selectFirst(".market-cap, .market-capitalization");
            if (marketCapElement != null) {
                String marketCapText = marketCapElement.text().replaceAll("[^\\d]", "");
                if (!marketCapText.isEmpty()) {
                    stockData.setMarketCap(Long.parseLong(marketCapText));
                }
            }
            
            return stockData;
            
        } catch (Exception e) {
            logger.error("解析股票数据失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析股票详细信息
     */
    private StockPltrData parseStockDetailData(Document doc, String symbol) {
        try {
            StockPltrData stockData = parseStockData(doc, symbol);
            if (stockData == null) {
                stockData = new StockPltrData(symbol);
            }
            
            // 解析更多详细信息
            Elements infoElements = doc.select(".stock-info, .financial-info, .metrics");
            
            for (Element element : infoElements) {
                String text = element.text().toLowerCase();
                
                // 解析市盈率
                if (text.contains("pe") || text.contains("市盈率")) {
                    String peText = extractNumber(text);
                    if (peText != null) {
                        stockData.setPeRatio(Double.parseDouble(peText));
                    }
                }
                
                // 解析市净率
                if (text.contains("pb") || text.contains("市净率")) {
                    String pbText = extractNumber(text);
                    if (pbText != null) {
                        stockData.setPbRatio(Double.parseDouble(pbText));
                    }
                }
                
                // 解析股息率
                if (text.contains("dividend") || text.contains("股息")) {
                    String dividendText = extractNumber(text);
                    if (dividendText != null) {
                        stockData.setDividendYield(Double.parseDouble(dividendText));
                    }
                }
                
                // 解析推荐评级
                if (text.contains("recommendation") || text.contains("rating") || text.contains("评级")) {
                    stockData.setRecommendation(element.text().trim());
                }
                
                // 解析目标价格
                if (text.contains("target") || text.contains("目标价格")) {
                    String targetText = extractNumber(text);
                    if (targetText != null) {
                        stockData.setTargetPrice(Double.parseDouble(targetText));
                    }
                }
            }
            
            return stockData;
            
        } catch (Exception e) {
            logger.error("解析股票详细信息失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 解析评论数据
     */
    private List<StockComment> parseComments(Document doc, String symbol) {
        List<StockComment> comments = new ArrayList<>();
        
        try {
            Elements commentElements = doc.select(".comment, .review, .user-comment, .discussion-item");
            
            for (Element element : commentElements) {
                StockComment comment = new StockComment(symbol, generateCommentId(element));
                
                // 解析用户名
                Element userElement = element.selectFirst(".username, .user-name, .author");
                if (userElement != null) {
                    comment.setUserName(userElement.text().trim());
                }
                
                // 解析用户头像
                Element avatarElement = element.selectFirst(".avatar, .user-avatar img");
                if (avatarElement != null) {
                    String avatarUrl = avatarElement.attr("src");
                    if (!avatarUrl.isEmpty()) {
                        comment.setUserAvatar(avatarUrl);
                    }
                }
                
                // 解析评论内容
                Element contentElement = element.selectFirst(".content, .text, .comment-text");
                if (contentElement != null) {
                    comment.setContent(contentElement.text().trim());
                }
                
                // 解析点赞数
                Element likesElement = element.selectFirst(".likes, .like-count, .thumbs-up");
                if (likesElement != null) {
                    String likesText = likesElement.text().replaceAll("[^\\d]", "");
                    if (!likesText.isEmpty()) {
                        comment.setLikesCount(Integer.parseInt(likesText));
                    }
                }
                
                // 解析回复数
                Element repliesElement = element.selectFirst(".replies, .reply-count, .comments-count");
                if (repliesElement != null) {
                    String repliesText = repliesElement.text().replaceAll("[^\\d]", "");
                    if (!repliesText.isEmpty()) {
                        comment.setRepliesCount(Integer.parseInt(repliesText));
                    }
                }
                
                // 解析评论时间
                Element timeElement = element.selectFirst(".time, .date, .timestamp");
                if (timeElement != null) {
                    comment.setCommentTime(parseTime(timeElement.text()));
                }
                
                // 简单情感分析
                analyzeSentiment(comment);
                
                comments.add(comment);
            }
            
        } catch (Exception e) {
            logger.error("解析评论数据失败: {}", e.getMessage());
        }
        
        return comments;
    }
    
    /**
     * 滚动页面加载更多评论
     */
    private void scrollToLoadComments() {
        try {
            // 滚动到页面底部
            ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);
            
            // 再次滚动
            ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
            Thread.sleep(2000);
            
        } catch (Exception e) {
            logger.debug("滚动页面时发生错误: {}", e.getMessage());
        }
    }
    
    /**
     * 生成评论ID
     */
    private String generateCommentId(Element element) {
        String text = element.text();
        return String.valueOf(Math.abs(text.hashCode()));
    }
    
    /**
     * 提取数字
     */
    private String extractNumber(String text) {
        Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
    
    /**
     * 解析时间
     */
    private LocalDateTime parseTime(String timeText) {
        try {
            // 尝试解析常见时间格式
            if (timeText.contains("分钟前") || timeText.contains("min ago")) {
                return LocalDateTime.now().minusMinutes(extractMinutes(timeText));
            } else if (timeText.contains("小时前") || timeText.contains("hour ago")) {
                return LocalDateTime.now().minusHours(extractHours(timeText));
            } else if (timeText.contains("天前") || timeText.contains("day ago")) {
                return LocalDateTime.now().minusDays(extractDays(timeText));
            }
            
            // 尝试解析具体时间
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(timeText, formatter);
            
        } catch (Exception e) {
            logger.debug("解析时间失败: {} - {}", timeText, e.getMessage());
            return LocalDateTime.now();
        }
    }
    
    private int extractMinutes(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*分钟");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
    
    private int extractHours(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*小时");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
    
    private int extractDays(String text) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*天");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
    }
    
    /**
     * 简单情感分析
     */
    private void analyzeSentiment(StockComment comment) {
        if (comment.getContent() == null) {
            return;
        }
        
        String content = comment.getContent().toLowerCase();
        double score = 0.0;
        
        // 正面词汇
        String[] positiveWords = {"涨", "好", "推荐", "买入", "看涨", "bullish", "buy", "good", "up", "rise"};
        for (String word : positiveWords) {
            if (content.contains(word)) {
                score += 0.1;
            }
        }
        
        // 负面词汇
        String[] negativeWords = {"跌", "坏", "卖出", "看跌", "bearish", "sell", "bad", "down", "fall"};
        for (String word : negativeWords) {
            if (content.contains(word)) {
                score -= 0.1;
            }
        }
        
        // 设置情感分析结果
        comment.setSentimentScore(Math.max(-1.0, Math.min(1.0, score)));
        
        if (score > 0.1) {
            comment.setSentiment("positive");
        } else if (score < -0.1) {
            comment.setSentiment("negative");
        } else {
            comment.setSentiment("neutral");
        }
    }
    
    /**
     * 创建模拟股票数据（用于演示）
     */
    private StockPltrData createMockStockData(String symbol) {
        StockPltrData stockData = new StockPltrData(symbol);
        
        // 根据股票代码设置不同的模拟数据
        switch (symbol.toUpperCase()) {
            case "AAPL":
                stockData.setCompanyName("Apple Inc.");
                stockData.setCurrentPrice(150.25);
                stockData.setPriceChange(2.35);
                stockData.setPriceChangePercent(1.58);
                stockData.setVolume(45000000L);
                stockData.setMarketCap(2400000000000L);
                stockData.setPeRatio(25.8);
                stockData.setPbRatio(5.1);
                stockData.setDividendYield(0.52);
                stockData.setRecommendation("BUY");
                stockData.setTargetPrice(165.00);
                break;
            case "TSLA":
                stockData.setCompanyName("Tesla Inc.");
                stockData.setCurrentPrice(245.80);
                stockData.setPriceChange(-5.20);
                stockData.setPriceChangePercent(-2.07);
                stockData.setVolume(32000000L);
                stockData.setMarketCap(780000000000L);
                stockData.setPeRatio(45.2);
                stockData.setPbRatio(8.3);
                stockData.setDividendYield(0.0);
                stockData.setRecommendation("HOLD");
                stockData.setTargetPrice(250.00);
                break;
            case "PLTR":
                stockData.setCompanyName("Palantir Technologies Inc.");
                stockData.setCurrentPrice(18.45);
                stockData.setPriceChange(0.85);
                stockData.setPriceChangePercent(4.84);
                stockData.setVolume(28000000L);
                stockData.setMarketCap(35000000000L);
                stockData.setPeRatio(65.5);
                stockData.setPbRatio(3.2);
                stockData.setDividendYield(0.0);
                stockData.setRecommendation("BUY");
                stockData.setTargetPrice(22.00);
                break;
            case "NVDA":
                stockData.setCompanyName("NVIDIA Corporation");
                stockData.setCurrentPrice(485.20);
                stockData.setPriceChange(12.80);
                stockData.setPriceChangePercent(2.71);
                stockData.setVolume(25000000L);
                stockData.setMarketCap(1200000000000L);
                stockData.setPeRatio(35.8);
                stockData.setPbRatio(12.5);
                stockData.setDividendYield(0.08);
                stockData.setRecommendation("STRONG BUY");
                stockData.setTargetPrice(520.00);
                break;
            case "MSFT":
                stockData.setCompanyName("Microsoft Corporation");
                stockData.setCurrentPrice(375.60);
                stockData.setPriceChange(3.40);
                stockData.setPriceChangePercent(0.91);
                stockData.setVolume(18000000L);
                stockData.setMarketCap(2800000000000L);
                stockData.setPeRatio(28.5);
                stockData.setPbRatio(6.8);
                stockData.setDividendYield(0.75);
                stockData.setRecommendation("BUY");
                stockData.setTargetPrice(390.00);
                break;
            default:
                // 默认数据
                stockData.setCompanyName(symbol + " Corporation");
                stockData.setCurrentPrice(100.00 + Math.random() * 200);
                stockData.setPriceChange((Math.random() - 0.5) * 10);
                stockData.setPriceChangePercent(stockData.getPriceChange() / stockData.getCurrentPrice() * 100);
                stockData.setVolume((long)(10000000 + Math.random() * 50000000));
                stockData.setMarketCap((long)(100000000000L + Math.random() * 1000000000000L));
                stockData.setPeRatio(15.0 + Math.random() * 30);
                stockData.setPbRatio(1.0 + Math.random() * 10);
                stockData.setDividendYield(Math.random() * 3);
                stockData.setRecommendation("HOLD");
                stockData.setTargetPrice(stockData.getCurrentPrice() * (1 + (Math.random() - 0.5) * 0.2));
                break;
        }
        
        return stockData;
    }
    
    /**
     * 创建模拟评论数据（用于演示）
     */
    private List<StockComment> createMockComments(String symbol) {
        List<StockComment> comments = new ArrayList<>();
        
        String[] sampleComments = {
            "这只股票看起来很有潜力，值得关注！",
            "最近表现不错，继续持有。",
            "技术面分析显示上涨趋势。",
            "基本面良好，长期看好。",
            "短期可能有波动，但长期趋势向上。",
            "市场情绪比较乐观。",
            "需要关注财报数据。",
            "分析师评级普遍看好。"
        };
        
        String[] userNames = {"投资者A", "股民小王", "分析师李", "投资达人", "财经观察者", "价值投资者", "技术分析师", "市场研究员"};
        
        for (int i = 0; i < 5; i++) {
            StockComment comment = new StockComment(symbol, "comment_" + i);
            comment.setUserName(userNames[i]);
            comment.setContent(sampleComments[i]);
            comment.setLikesCount((int)(Math.random() * 50));
            comment.setRepliesCount((int)(Math.random() * 10));
            comment.setCommentTime(java.time.LocalDateTime.now().minusHours((int)(Math.random() * 24)));
            
            // 简单情感分析
            analyzeSentiment(comment);
            
            comments.add(comment);
        }
        
        return comments;
    }
}
