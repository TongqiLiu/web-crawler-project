package com.quant.stock.service;

import com.quant.stock.model.StockData;
import com.quant.stock.repository.StockDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 股票数据服务测试类
 */
@ExtendWith(MockitoExtension.class)
class StockDataServiceTest {
    
    @Mock
    private StockDataRepository stockDataRepository;
    
    @InjectMocks
    private StockDataService stockDataService;
    
    private StockData mockStockData;
    
    @BeforeEach
    void setUp() {
        mockStockData = new StockData("AAPL", "Apple Inc.");
        mockStockData.setId(1L);
        mockStockData.setCurrentPrice(new BigDecimal("150.00"));
        mockStockData.setChangePercent(new BigDecimal("2.5"));
    }
    
    @Test
    @DisplayName("应该成功获取股票数据")
    void shouldGetStockDataSuccessfully() {
        // Given
        when(stockDataRepository.findTopBySymbolOrderByLastUpdatedDesc("AAPL"))
            .thenReturn(Optional.of(mockStockData));
        
        // When
        Optional<StockData> result = stockDataService.getStockData("AAPL");
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("AAPL", result.get().getSymbol());
        assertEquals("Apple Inc.", result.get().getName());
        assertEquals(new BigDecimal("150.00"), result.get().getCurrentPrice());
        
        verify(stockDataRepository).findTopBySymbolOrderByLastUpdatedDesc("AAPL");
    }
    
    @Test
    @DisplayName("应该返回空结果当股票不存在时")
    void shouldReturnEmptyWhenStockNotFound() {
        // Given
        when(stockDataRepository.findTopBySymbolOrderByLastUpdatedDesc("INVALID"))
            .thenReturn(Optional.empty());
        
        // When
        Optional<StockData> result = stockDataService.getStockData("INVALID");
        
        // Then
        assertFalse(result.isPresent());
        verify(stockDataRepository).findTopBySymbolOrderByLastUpdatedDesc("INVALID");
    }
    
    @Test
    @DisplayName("应该获取股票历史数据")
    void shouldGetHistoricalData() {
        // Given
        List<StockData> historyData = Arrays.asList(mockStockData);
        when(stockDataRepository.findBySymbolOrderByLastUpdatedDesc("AAPL"))
            .thenReturn(historyData);
        
        // When
        List<StockData> result = stockDataService.getHistoricalData("AAPL");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        
        verify(stockDataRepository).findBySymbolOrderByLastUpdatedDesc("AAPL");
    }
    
    @Test
    @DisplayName("应该获取所有活跃股票代码")
    void shouldGetAllActiveSymbols() {
        // Given
        List<String> symbols = Arrays.asList("AAPL", "GOOGL", "MSFT");
        when(stockDataRepository.findAllActiveSymbols()).thenReturn(symbols);
        
        // When
        List<String> result = stockDataService.getAllActiveSymbols();
        
        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("AAPL"));
        assertTrue(result.contains("GOOGL"));
        assertTrue(result.contains("MSFT"));
        
        verify(stockDataRepository).findAllActiveSymbols();
    }
    
    @Test
    @DisplayName("应该处理大小写转换")
    void shouldHandleCaseConversion() {
        // Given
        when(stockDataRepository.findTopBySymbolOrderByLastUpdatedDesc("AAPL"))
            .thenReturn(Optional.of(mockStockData));
        
        // When
        Optional<StockData> result = stockDataService.getStockData("aapl");
        
        // Then
        assertTrue(result.isPresent());
        verify(stockDataRepository).findTopBySymbolOrderByLastUpdatedDesc("AAPL");
    }
}
