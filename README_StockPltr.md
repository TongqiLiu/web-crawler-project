# StockPltr数据爬虫模块

## 概述

StockPltr数据爬虫模块是股票量化分析系统的一个重要组成部分，专门用于爬取[stockpltr.com](https://www.stockpltr.com/)网站的股票数据和用户评论。

## 功能特性

### 🚀 核心功能
- **股票数据爬取**: 获取股票基本信息、价格、成交量、市值等
- **评论数据采集**: 爬取用户对股票的评论和讨论
- **情感分析**: 对评论进行简单的情感分析（正面/负面/中性）
- **数据缓存**: 智能缓存机制，避免重复请求
- **定时更新**: 支持定时批量更新股票数据

### 📊 数据模型
- **StockPltrData**: 股票数据模型
  - 基本信息：股票代码、公司名称、当前价格
  - 财务数据：市盈率、市净率、股息率、市值
  - 技术指标：价格变化、成交量、推荐评级
- **StockComment**: 评论数据模型
  - 用户信息：用户名、头像
  - 评论内容：评论文本、点赞数、回复数
  - 情感分析：情感分类、情感分数

### 🔧 技术架构
- **爬虫引擎**: 基于Selenium WebDriver的智能爬虫
- **数据解析**: 使用Jsoup进行HTML解析
- **数据存储**: JPA + H2数据库
- **REST API**: 完整的RESTful接口
- **配置管理**: 灵活的配置系统

## API接口

### 基础接口
- `GET /api/stockpltr/status` - 获取服务状态
- `GET /api/stockpltr/test/basic` - 测试基本功能
- `GET /api/stockpltr/test/model` - 查看数据模型
- `GET /api/stockpltr/test/config` - 查看配置信息

### 数据接口
- `GET /api/stockpltr/stock/{symbol}` - 获取股票数据
- `GET /api/stockpltr/stock/{symbol}/comments` - 获取股票评论
- `GET /api/stockpltr/stock/{symbol}/full` - 获取完整信息（数据+评论）
- `GET /api/stockpltr/stock/{symbol}/history?days=30` - 获取历史数据
- `GET /api/stockpltr/stock/{symbol}/statistics` - 获取评论统计

### 管理接口
- `GET /api/stockpltr/search?q=AAPL` - 搜索股票
- `POST /api/stockpltr/batch-update` - 批量更新数据
- `POST /api/stockpltr/scheduler/start` - 启动定时任务
- `POST /api/stockpltr/scheduler/stop` - 停止定时任务
- `POST /api/stockpltr/cleanup` - 清理旧数据

## 配置说明

### application.yml配置
```yaml
# StockPltr爬虫配置
stockpltr:
  crawl:
    enabled: true # 是否启用爬虫
    interval: 300 # 爬取间隔（秒）
    batch-size: 10 # 批量处理大小
    timeout: 30 # 请求超时时间（秒）
    max-retries: 3 # 最大重试次数
    delay-between-requests: 2000 # 请求间延迟（毫秒）
    
  data:
    retention-days: 30 # 数据保留天数
    enable-cache: true # 是否启用缓存
    cache-duration: 300 # 缓存持续时间（秒）
    
  website:
    base-url: https://www.stockpltr.com
    search-url: https://www.stockpltr.com/search
    stock-detail-url: https://www.stockpltr.com/stock
    
  monitoring:
    symbols: # 默认监控的股票代码
      - AAPL
      - TSLA
      - PLTR
      - NVDA
      - MSFT
      - GOOGL
      - AMZN
      - META
```

## 使用示例

### 1. 获取股票数据
```bash
curl "http://localhost:8080/api/stockpltr/stock/AAPL"
```

### 2. 获取股票评论
```bash
curl "http://localhost:8080/api/stockpltr/stock/AAPL/comments"
```

### 3. 搜索股票
```bash
curl "http://localhost:8080/api/stockpltr/search?q=Apple"
```

### 4. 批量更新数据
```bash
curl -X POST "http://localhost:8080/api/stockpltr/batch-update" \
  -H "Content-Type: application/json" \
  -d '{"symbols":["AAPL","TSLA","PLTR"]}'
```

## 项目结构

```
src/main/java/com/quant/stockpltr/
├── config/
│   ├── StockPltrConfig.java          # 配置类
│   └── StockPltrAutoConfiguration.java # 自动配置
├── controller/
│   ├── StockPltrController.java      # 主控制器
│   └── StockPltrTestController.java  # 测试控制器
├── crawler/
│   └── StockPltrCrawler.java         # 核心爬虫
├── model/
│   ├── StockPltrData.java            # 股票数据模型
│   └── StockComment.java             # 评论数据模型
├── repository/
│   ├── StockPltrDataRepository.java  # 数据仓库
│   └── StockCommentRepository.java   # 评论仓库
└── service/
    └── StockPltrDataService.java     # 业务服务
```

## 测试脚本

项目提供了完整的测试脚本：
```bash
./test-stockpltr-api.sh
```

## 开发状态

✅ **已完成功能**:
- [x] 数据模型设计
- [x] 核心爬虫实现
- [x] 业务服务层
- [x] REST API接口
- [x] 配置管理
- [x] 数据库集成
- [x] 测试接口

⚠️ **注意事项**:
- WebDriver爬虫功能需要Chrome浏览器支持
- 实际爬取功能需要根据stockpltr.com的具体页面结构调整
- 建议在生产环境中配置合适的请求频率限制

## 集成说明

StockPltr模块已完全集成到现有的股票量化分析系统中：

1. **自动启动**: 应用启动时自动初始化StockPltr服务
2. **配置统一**: 使用统一的application.yml配置
3. **数据库共享**: 使用相同的H2数据库
4. **API统一**: 遵循统一的REST API规范

## 扩展建议

1. **反爬虫优化**: 添加更多反检测机制
2. **数据验证**: 增强数据质量检查
3. **性能优化**: 优化爬取效率和内存使用
4. **监控告警**: 添加爬取状态监控
5. **数据导出**: 支持数据导出功能

---

**StockPltr数据爬虫模块** - 为您的股票量化分析系统提供强大的数据支持！ 🚀📈
