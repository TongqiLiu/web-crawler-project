#!/bin/bash

# 技术分析API测试脚本

echo "🧪 技术分析API测试"
echo "=================="

BASE_URL="http://localhost:8080"
SYMBOL="AAPL"

# 等待应用启动
echo "⏳ 等待应用启动..."
sleep 5

# 测试应用是否运行
echo "🔍 检查应用状态..."
if curl -s "$BASE_URL" > /dev/null 2>&1; then
    echo "✅ 应用正在运行"
else
    echo "❌ 应用未运行，请先启动应用"
    exit 1
fi

echo ""
echo "📊 测试技术分析API端点:"

# 1. 测试健康检查
echo "1. 技术分析服务健康检查..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/health" -o /tmp/analysis_health.txt)
if [ "$response" = "200" ]; then
    echo "✅ 技术分析服务健康: $(cat /tmp/analysis_health.txt)"
else
    echo "❌ 技术分析服务异常 (HTTP $response)"
fi

# 2. 测试富途连接状态
echo ""
echo "2. 检查富途连接状态..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/futu/status" -o /tmp/futu_status.txt)
if [ "$response" = "200" ]; then
    echo "✅ 富途状态获取成功:"
    cat /tmp/futu_status.txt | head -c 100
    echo "..."
else
    echo "❌ 获取富途状态失败 (HTTP $response)"
fi

# 3. 初始化富途连接
echo ""
echo "3. 初始化富途连接..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/futu/connect" -o /tmp/futu_connect.txt)
echo "富途连接结果 (HTTP $response): $(cat /tmp/futu_connect.txt)"

# 4. 测试SMA指标
echo ""
echo "4. 计算SMA指标 (20日)..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL/sma?period=20" -o /tmp/sma.txt)
if [ "$response" = "200" ]; then
    echo "✅ SMA计算成功: $(cat /tmp/sma.txt)"
elif [ "$response" = "404" ]; then
    echo "⚠️  SMA数据不存在，可能需要先获取历史数据"
else
    echo "❌ SMA计算失败 (HTTP $response)"
fi

# 5. 测试EMA指标
echo ""
echo "5. 计算EMA指标 (12日)..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL/ema?period=12" -o /tmp/ema.txt)
if [ "$response" = "200" ]; then
    echo "✅ EMA计算成功: $(cat /tmp/ema.txt)"
elif [ "$response" = "404" ]; then
    echo "⚠️  EMA数据不存在"
else
    echo "❌ EMA计算失败 (HTTP $response)"
fi

# 6. 测试RSI指标
echo ""
echo "6. 计算RSI指标 (14日)..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL/rsi?period=14" -o /tmp/rsi.txt)
if [ "$response" = "200" ]; then
    echo "✅ RSI计算成功: $(cat /tmp/rsi.txt)"
elif [ "$response" = "404" ]; then
    echo "⚠️  RSI数据不存在"
else
    echo "❌ RSI计算失败 (HTTP $response)"
fi

# 7. 测试MACD指标
echo ""
echo "7. 计算MACD指标..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL/macd" -o /tmp/macd.txt)
if [ "$response" = "200" ]; then
    echo "✅ MACD计算成功:"
    cat /tmp/macd.txt | head -c 200
    echo "..."
elif [ "$response" = "404" ]; then
    echo "⚠️  MACD数据不存在"
else
    echo "❌ MACD计算失败 (HTTP $response)"
fi

# 8. 测试布林带指标
echo ""
echo "8. 计算布林带指标..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL/bollinger?period=20&stdDev=2.0" -o /tmp/bollinger.txt)
if [ "$response" = "200" ]; then
    echo "✅ 布林带计算成功:"
    cat /tmp/bollinger.txt | head -c 200
    echo "..."
elif [ "$response" = "404" ]; then
    echo "⚠️  布林带数据不存在"
else
    echo "❌ 布林带计算失败 (HTTP $response)"
fi

# 9. 测试综合技术分析
echo ""
echo "9. 综合技术分析..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL" -o /tmp/comprehensive.txt)
if [ "$response" = "200" ]; then
    echo "✅ 综合分析成功:"
    cat /tmp/comprehensive.txt | head -c 300
    echo "..."
elif [ "$response" = "404" ]; then
    echo "⚠️  综合分析数据不存在"
else
    echo "❌ 综合分析失败 (HTTP $response)"
fi

# 10. 先更新股票数据再测试分析
echo ""
echo "10. 先更新股票数据再重新测试..."
echo "更新$SYMBOL股票数据..."
curl -s -X POST "$BASE_URL/api/stocks/$SYMBOL/update" > /dev/null

echo "等待数据更新..."
sleep 3

echo "重新测试综合分析..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/analysis/$SYMBOL" -o /tmp/comprehensive2.txt)
if [ "$response" = "200" ]; then
    echo "✅ 更新后综合分析成功:"
    cat /tmp/comprehensive2.txt | head -c 400
    echo "..."
else
    echo "❌ 更新后综合分析失败 (HTTP $response)"
fi

echo ""
echo "🎯 技术分析API测试完成!"

# 清理临时文件
rm -f /tmp/analysis_health.txt /tmp/futu_status.txt /tmp/futu_connect.txt
rm -f /tmp/sma.txt /tmp/ema.txt /tmp/rsi.txt /tmp/macd.txt /tmp/bollinger.txt
rm -f /tmp/comprehensive.txt /tmp/comprehensive2.txt
