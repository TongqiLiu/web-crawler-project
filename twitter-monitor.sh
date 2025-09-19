#!/bin/bash

# Twitter监控脚本
# 监控 @xiaozhaolucky 的推文更新

TARGET_USER="xiaozhaolucky"
TARGET_URL="https://x.com/xiaozhaolucky"
LAST_TWEET_FILE="/tmp/last_tweet_${TARGET_USER}.txt"
LOG_FILE="/tmp/twitter_monitor.log"
USER_AGENT="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"

# 创建日志文件
touch "$LOG_FILE"

echo "🐦 开始监控Twitter用户: @$TARGET_USER"
echo "🌐 监控地址: $TARGET_URL"
echo "📝 日志文件: $LOG_FILE"
echo "⏰ 检查间隔: 30秒"
echo "=" | tee -a "$LOG_FILE"

# 获取最新推文ID的函数
get_latest_tweet() {
    # 使用curl获取用户页面
    curl -s "$TARGET_URL" \
         -H "User-Agent: $USER_AGENT" \
         -H "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8" \
         -H "Accept-Language: en-US,en;q=0.5" \
         -H "Accept-Encoding: gzip, deflate" \
         -H "Connection: keep-alive" \
         -H "Upgrade-Insecure-Requests: 1" \
         --connect-timeout 10 | \
    grep -o 'data-tweet-id="[^"]*"' | \
    head -1 | \
    sed 's/data-tweet-id="//;s/"//'
}

# 发送通知的函数
send_notification() {
    local message="$1"
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # macOS系统通知
    osascript -e "display notification \"$message\" with title \"Twitter提醒 @$TARGET_USER\""
    
    # 控制台输出
    echo ""
    echo "🚨 新推文提醒 🚨" | tee -a "$LOG_FILE"
    echo "用户: @$TARGET_USER" | tee -a "$LOG_FILE"
    echo "时间: $timestamp" | tee -a "$LOG_FILE"
    echo "推文ID: $1" | tee -a "$LOG_FILE"
    echo "=" | tee -a "$LOG_FILE"
    echo ""
}

# 主监控循环
monitor_tweets() {
    local current_tweet
    local last_tweet=""
    
    # 读取上次的推文ID
    if [ -f "$LAST_TWEET_FILE" ]; then
        last_tweet=$(cat "$LAST_TWEET_FILE")
    fi
    
    while true; do
        current_tweet=$(get_latest_tweet)
        
        if [ -n "$current_tweet" ] && [ "$current_tweet" != "$last_tweet" ]; then
            if [ -n "$last_tweet" ]; then
                # 有新推文
                send_notification "$current_tweet"
            fi
            last_tweet="$current_tweet"
            echo "$current_tweet" > "$LAST_TWEET_FILE"
        fi
        
        # 等待30秒
        sleep 30
    done
}

# 清理函数
cleanup() {
    echo ""
    echo "🛑 停止监控..."
    echo "📝 日志保存在: $LOG_FILE"
    exit 0
}

# 捕获中断信号
trap cleanup SIGINT SIGTERM

# 开始监控
monitor_tweets
