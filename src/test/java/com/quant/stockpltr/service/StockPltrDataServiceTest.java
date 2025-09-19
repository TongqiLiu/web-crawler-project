package com.quant.stockpltr.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quant.stockpltr.crawler.StockPltrCrawler;
import com.quant.stockpltr.model.StockComment;
import com.quant.stockpltr.model.StockPltrData;
import com.quant.stockpltr.repository.StockCommentRepository;
import com.quant.stockpltr.repository.StockPltrDataRepository;

/**
 * StockPltrDataService测试类
 */
@ExtendWith(MockitoExtension.class)
class StockPltrDataServiceTest {
    
    @Mock
    private StockPltrCrawler stockPltrCrawler;
    
    @Mock
    private StockPltrDataRepository stockPltrDataRepository;
    
    @Mock
    private StockCommentRepository stockCommentRepository;
    
    @InjectMocks
    private StockPltrDataService stockPltrDataService;
    
    private StockPltrData testStockData;
    private StockComment testComment;
    
    @BeforeEach
    void setUp() {
        // 创建测试数据
        testStockData = new StockPltrData("AAPL");
        testStockData.setCompanyName("Apple Inc.");
        testStockData.setCurrentPrice(150.0);
        testStockData.setPriceChange(2.5);
        testStockData.setPriceChangePercent(1.69);
        testStockData.setVolume(50000000L);
        testStockData.setMarketCap(2500000000000L);
        testStockData.setPeRatio(25.5);
        testStockData.setPbRatio(5.2);
        testStockData.setDividendYield(0.5);
        testStockData.setRecommendation("BUY");
        testStockData.setTargetPrice(160.0);
        
        testComment = new StockComment("AAPL", "comment123");
        testComment.setUserName("testuser");
        testComment.setContent("Apple stock looks good!");
        testComment.setLikesCount(10);
        testComment.setRepliesCount(2);
        testComment.setSentiment("positive");
        testComment.setSentimentScore(0.8);
        testComment.setCommentTime(LocalDateTime.now());
    }
    
    @Test
    void testGetStockData_WithFreshData() {
        // 准备测试数据
        when(stockPltrDataRepository.findLatestBySymbol("AAPL"))
            .thenReturn(Optional.of(testStockData));
        
        // 执行测试
        StockPltrData result = stockPltrDataService.getStockData("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals("Apple Inc.", result.getCompanyName());
        assertEquals(150.0, result.getCurrentPrice());
        
        // 验证方法调用
        verify(stockPltrDataRepository).findLatestBySymbol("AAPL");
        verify(stockPltrCrawler, never()).getStockDetail(anyString());
    }
    
    @Test
    void testGetStockData_WithStaleData() {
        // 准备测试数据 - 数据超过5分钟
        testStockData.setCrawlTime(LocalDateTime.now().minusMinutes(10));
        when(stockPltrDataRepository.findLatestBySymbol("AAPL"))
            .thenReturn(Optional.of(testStockData));
        when(stockPltrCrawler.getStockDetail("AAPL"))
            .thenReturn(testStockData);
        when(stockPltrDataRepository.save(any(StockPltrData.class)))
            .thenReturn(testStockData);
        
        // 执行测试
        StockPltrData result = stockPltrDataService.getStockData("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        
        // 验证方法调用
        verify(stockPltrDataRepository).findLatestBySymbol("AAPL");
        verify(stockPltrCrawler).getStockDetail("AAPL");
        verify(stockPltrDataRepository).save(any(StockPltrData.class));
    }
    
    @Test
    void testGetStockData_WithNoData() {
        // 准备测试数据 - 没有缓存数据
        when(stockPltrDataRepository.findLatestBySymbol("AAPL"))
            .thenReturn(Optional.empty());
        when(stockPltrCrawler.getStockDetail("AAPL"))
            .thenReturn(testStockData);
        when(stockPltrDataRepository.save(any(StockPltrData.class)))
            .thenReturn(testStockData);
        
        // 执行测试
        StockPltrData result = stockPltrDataService.getStockData("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        
        // 验证方法调用
        verify(stockPltrDataRepository).findLatestBySymbol("AAPL");
        verify(stockPltrCrawler).getStockDetail("AAPL");
        verify(stockPltrDataRepository).save(any(StockPltrData.class));
    }
    
    @Test
    void testGetStockComments_WithFreshComments() {
        // 准备测试数据
        List<StockComment> comments = List.of(testComment);
        when(stockCommentRepository.findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr"))
            .thenReturn(comments);
        
        // 执行测试
        List<StockComment> result = stockPltrDataService.getStockComments("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("comment123", result.get(0).getCommentId());
        
        // 验证方法调用
        verify(stockCommentRepository).findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr");
        verify(stockPltrCrawler, never()).getStockComments(anyString());
    }
    
    @Test
    void testGetStockComments_WithStaleComments() {
        // 准备测试数据 - 评论超过10分钟
        testComment.setCrawlTime(LocalDateTime.now().minusMinutes(15));
        List<StockComment> comments = List.of(testComment);
        
        when(stockCommentRepository.findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr"))
            .thenReturn(comments);
        when(stockPltrCrawler.getStockComments("AAPL"))
            .thenReturn(comments);
        when(stockCommentRepository.saveAll(anyList()))
            .thenReturn(comments);
        
        // 执行测试
        List<StockComment> result = stockPltrDataService.getStockComments("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证方法调用
        verify(stockCommentRepository).findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr");
        verify(stockPltrCrawler).getStockComments("AAPL");
        verify(stockCommentRepository).saveAll(anyList());
    }
    
    @Test
    void testGetStockFullInfo() {
        // 准备测试数据
        when(stockPltrDataRepository.findLatestBySymbol("AAPL"))
            .thenReturn(Optional.of(testStockData));
        when(stockCommentRepository.findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr"))
            .thenReturn(List.of(testComment));
        
        // 执行测试
        StockPltrData result = stockPltrDataService.getStockFullInfo("AAPL");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
        
        // 验证方法调用
        verify(stockPltrDataRepository).findLatestBySymbol("AAPL");
        verify(stockCommentRepository).findByStockSymbolAndDataSourceOrderByCommentTimeDesc("AAPL", "stockpltr");
    }
    
    @Test
    void testSaveStockData() {
        // 准备测试数据
        when(stockPltrDataRepository.save(any(StockPltrData.class)))
            .thenReturn(testStockData);
        
        // 执行测试
        StockPltrData result = stockPltrDataService.saveStockData(testStockData);
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        
        // 验证方法调用
        verify(stockPltrDataRepository).save(testStockData);
    }
    
    @Test
    void testSaveStockComments() {
        // 准备测试数据
        List<StockComment> comments = List.of(testComment);
        when(stockCommentRepository.saveAll(anyList()))
            .thenReturn(comments);
        
        // 执行测试
        List<StockComment> result = stockPltrDataService.saveStockComments(comments);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证方法调用
        verify(stockCommentRepository).saveAll(comments);
    }
    
    @Test
    void testGetStockHistory() {
        // 准备测试数据
        List<StockPltrData> history = List.of(testStockData);
        when(stockPltrDataRepository.findBySymbolAndCrawlTimeBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(history);
        
        // 执行测试
        List<StockPltrData> result = stockPltrDataService.getStockHistory("AAPL", 30);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        
        // 验证方法调用
        verify(stockPltrDataRepository).findBySymbolAndCrawlTimeBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testGetCommentStatistics() {
        // 准备测试数据
        when(stockCommentRepository.countByStockSymbol("AAPL"))
            .thenReturn(100L);
        when(stockCommentRepository.countByStockSymbolAndSentiment("AAPL"))
            .thenReturn(List.of(new Object[]{"positive", 60L}, new Object[]{"negative", 20L}, new Object[]{"neutral", 20L}));
        when(stockCommentRepository.findPopularComments("AAPL"))
            .thenReturn(List.of(testComment));
        
        // 执行测试
        Object result = stockPltrDataService.getCommentStatistics("AAPL");
        
        // 验证结果
        assertNotNull(result);
        
        // 验证方法调用
        verify(stockCommentRepository).countByStockSymbol("AAPL");
        verify(stockCommentRepository).countByStockSymbolAndSentiment("AAPL");
        verify(stockCommentRepository).findPopularComments("AAPL");
    }
    
    @Test
    void testSearchStock() {
        // 准备测试数据
        when(stockPltrCrawler.searchStock("Apple"))
            .thenReturn(testStockData);
        when(stockPltrDataRepository.save(any(StockPltrData.class)))
            .thenReturn(testStockData);
        
        // 执行测试
        StockPltrData result = stockPltrDataService.searchStock("Apple");
        
        // 验证结果
        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        
        // 验证方法调用
        verify(stockPltrCrawler).searchStock("Apple");
        verify(stockPltrDataRepository).save(any(StockPltrData.class));
    }
}
