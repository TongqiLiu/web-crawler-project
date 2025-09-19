#!/bin/bash

# StockPltr API测试脚本
# 测试StockPltr数据爬虫的REST API接口

BASE_URL="http://localhost:8080/api/stockpltr"
SYMBOL="AAPL"

echo "🚀 开始测试StockPltr API接口..."
echo "=================================="

# 测试1: 获取API状态
echo "📊 测试1: 获取API状态"
curl -s -X GET "$BASE_URL/status" | jq '.'
echo ""

# 测试2: 搜索股票
echo "🔍 测试2: 搜索股票"
curl -s -X GET "$BASE_URL/search?q=Apple" | jq '.'
echo ""

# 测试3: 获取股票数据
echo "📈 测试3: 获取股票数据"
curl -s -X GET "$BASE_URL/stock/$SYMBOL" | jq '.'
echo ""

# 测试4: 获取股票评论
echo "💬 测试4: 获取股票评论"
curl -s -X GET "$BASE_URL/stock/$SYMBOL/comments" | jq '.'
echo ""

# 测试5: 获取股票完整信息
echo "📋 测试5: 获取股票完整信息"
curl -s -X GET "$BASE_URL/stock/$SYMBOL/full" | jq '.'
echo ""

# 测试6: 获取股票历史数据
echo "📅 测试6: 获取股票历史数据"
curl -s -X GET "$BASE_URL/stock/$SYMBOL/history?days=7" | jq '.'
echo ""

# 测试7: 获取股票评论统计
echo "📊 测试7: 获取股票评论统计"
curl -s -X GET "$BASE_URL/stock/$SYMBOL/statistics" | jq '.'
echo ""

# 测试8: 批量更新股票数据
echo "🔄 测试8: 批量更新股票数据"
curl -s -X POST "$BASE_URL/batch-update" \
  -H "Content-Type: application/json" \
  -d '{"symbols":["AAPL","TSLA","PLTR"]}' | jq '.'
echo ""

# 测试9: 启动定时爬取任务
echo "⏰ 测试9: 启动定时爬取任务"
curl -s -X POST "$BASE_URL/scheduler/start" | jq '.'
echo ""

# 等待5秒
echo "⏳ 等待5秒..."
sleep 5

# 测试10: 停止定时爬取任务
echo "⏹️ 测试10: 停止定时爬取任务"
curl -s -X POST "$BASE_URL/scheduler/stop" | jq '.'
echo ""

# 测试11: 清理旧数据
echo "🧹 测试11: 清理旧数据"
curl -s -X POST "$BASE_URL/cleanup" | jq '.'
echo ""

echo "✅ StockPltr API测试完成！"
echo "=================================="
