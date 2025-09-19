#!/bin/bash

# Twitter监控测试脚本

echo "🐦 Twitter监控测试"
echo "=================="

# 检查Twitter监控脚本是否在运行
if pgrep -f "twitter-monitor.sh" > /dev/null; then
    echo "✅ Twitter监控脚本正在运行"
    echo "📊 监控用户: @xiaozhaolucky"
    echo "⏰ 检查间隔: 30秒"
    echo ""
    echo "📝 监控日志:"
    tail -5 /tmp/twitter_monitor.log 2>/dev/null || echo "日志文件不存在或为空"
else
    echo "❌ Twitter监控脚本未运行"
    echo "💡 启动命令: ./twitter-monitor.sh"
fi

echo ""
echo "🔍 系统进程状态:"
ps aux | grep -E "(twitter|java)" | grep -v grep

echo ""
echo "📱 测试系统通知:"
osascript -e 'display notification "Twitter监控测试成功！" with title "股票量化系统"'

echo ""
echo "✅ 测试完成"
