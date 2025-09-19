#!/bin/bash

# StockPltr功能提交脚本

echo "🚀 开始提交StockPltr功能到GitHub..."

# 添加所有文件
echo "📁 添加文件到Git..."
git add .

# 提交更改
echo "💾 提交更改..."
git commit -m "feat: 完成StockPltr数据爬虫模块开发

✨ 新增功能:
- StockPltr数据爬虫模块完整实现
- 网页界面展示功能
- REST API接口
- 调试工具和测试功能

📊 核心组件:
- StockPltrData/StockComment数据模型
- StockPltrCrawler核心爬虫
- StockPltrDataService业务服务
- StockPltrController REST API
- StockPltrWebController网页界面
- 完整的HTML模板和调试工具

🔧 技术特性:
- Selenium WebDriver自动化爬虫
- JPA数据持久化
- Thymeleaf模板引擎
- Bootstrap响应式UI
- 智能缓存机制
- 情感分析功能

📝 文档:
- README_StockPltr.md详细文档
- API接口说明
- 使用示例和配置说明

🎯 解决的问题:
- 数据获取准确性问题
- 网页输出功能需求
- 完整的调试和测试工具"

# 推送到GitHub
echo "🌐 推送到GitHub..."
git push origin main

echo "✅ StockPltr功能提交完成！"
echo "🔗 访问地址: http://localhost:8080/stockpltr/"
echo "📊 API文档: http://localhost:8080/api/stockpltr/status"
