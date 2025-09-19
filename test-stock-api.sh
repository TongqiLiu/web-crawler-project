#!/bin/bash

# 股票API测试脚本

echo "🧪 股票数据API测试"
echo "=================="

BASE_URL="http://localhost:8080"

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

# 测试API端点
echo ""
echo "📊 测试股票API端点:"

# 测试健康检查
echo "1. 健康检查..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/stocks/health" -o /tmp/health_response.txt)
if [ "$response" = "200" ]; then
    echo "✅ 健康检查通过: $(cat /tmp/health_response.txt)"
else
    echo "❌ 健康检查失败 (HTTP $response)"
fi

# 测试获取股票数据
echo ""
echo "2. 获取AAPL股票数据..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/stocks/AAPL" -o /tmp/aapl_response.txt)
if [ "$response" = "200" ]; then
    echo "✅ 获取AAPL数据成功:"
    cat /tmp/aapl_response.txt | head -c 200
    echo "..."
elif [ "$response" = "404" ]; then
    echo "⚠️  AAPL数据不存在，尝试手动更新..."
else
    echo "❌ 获取AAPL数据失败 (HTTP $response)"
fi

# 测试手动更新股票数据
echo ""
echo "3. 手动更新AAPL股票数据..."
response=$(curl -s -w "%{http_code}" -X POST "$BASE_URL/api/stocks/AAPL/update" -o /tmp/update_response.txt)
if [ "$response" = "200" ]; then
    echo "✅ 更新AAPL数据成功:"
    cat /tmp/update_response.txt | head -c 200
    echo "..."
else
    echo "❌ 更新AAPL数据失败 (HTTP $response): $(cat /tmp/update_response.txt)"
fi

# 再次获取股票数据
echo ""
echo "4. 再次获取AAPL股票数据..."
sleep 2
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/stocks/AAPL" -o /tmp/aapl_response2.txt)
if [ "$response" = "200" ]; then
    echo "✅ 获取AAPL数据成功:"
    cat /tmp/aapl_response2.txt | head -c 300
    echo "..."
else
    echo "❌ 获取AAPL数据失败 (HTTP $response)"
fi

# 测试获取所有股票代码
echo ""
echo "5. 获取所有股票代码..."
response=$(curl -s -w "%{http_code}" "$BASE_URL/api/stocks/symbols" -o /tmp/symbols_response.txt)
if [ "$response" = "200" ]; then
    echo "✅ 获取股票代码成功:"
    cat /tmp/symbols_response.txt
else
    echo "❌ 获取股票代码失败 (HTTP $response)"
fi

echo ""
echo "🎯 测试完成!"

# 清理临时文件
rm -f /tmp/health_response.txt /tmp/aapl_response.txt /tmp/update_response.txt /tmp/aapl_response2.txt /tmp/symbols_response.txt
