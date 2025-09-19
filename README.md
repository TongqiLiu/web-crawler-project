# 股票量化分析系统 (Stock Quantitative Analysis System)

一个基于Java Spring Boot的股票量化分析系统，集成Twitter监控、美股数据采集、技术分析等功能。

## 🚀 功能特性

### 核心功能
- 🐦 **Twitter监控**: 实时监控指定用户推文更新
- 📊 **美股数据采集**: 集成Yahoo Finance API获取股票数据
- 📈 **技术分析**: 实现常用技术指标和分析工具
- 🌐 **Web界面**: 现代化的数据展示和管理界面
- 🔔 **智能提醒**: 系统通知和邮件提醒功能

### 技术特性
- ☁️ **Spring Boot架构**: 企业级Java应用框架
- 🗄️ **数据持久化**: H2/MySQL数据库支持
- 🔧 **配置管理**: 灵活的配置文件管理
- 📝 **日志系统**: 完善的日志记录和错误处理
- 🧪 **测试覆盖**: 完整的单元测试和集成测试

## 🛠️ 技术栈

### 后端技术
- **Java 11+**: 核心开发语言
- **Spring Boot 3.2.0**: 应用框架
- **Maven**: 项目构建工具
- **H2/MySQL**: 数据库
- **Jsoup**: HTML解析
- **Jackson**: JSON处理
- **SLF4J + Logback**: 日志框架

### 前端技术
- **Thymeleaf**: 模板引擎
- **Bootstrap**: UI框架
- **JavaScript**: 前端交互

### 测试框架
- **JUnit 5**: 单元测试
- **Mockito**: 模拟测试
- **Spring Boot Test**: 集成测试

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── crawler/
│   │           ├── config/     # 配置类
│   │           ├── core/       # 核心爬虫逻辑
│   │           ├── examples/   # 示例代码
│   │           └── utils/      # 工具类
│   └── resources/
└── test/
    └── java/
```

## 快速开始

### 环境要求

- JDK 11 或更高版本
- Maven 3.6 或更高版本

### 构建项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包项目

```bash
mvn clean package
```

## 📱 使用示例

### Twitter监控
```java
// 启动Twitter监控
@Autowired
private TwitterService twitterService;

// 监控会自动启动，监控 @xiaozhaolucky 的推文
// 新推文会通过系统通知提醒
```

### 股票数据获取
```java
// 获取股票数据
@Autowired
private StockDataService stockService;

// 获取苹果公司股票数据
StockData appleStock = stockService.getStockData("AAPL");
```

### Web界面访问
```bash
# 启动应用后访问
http://localhost:8080

# 数据库控制台
http://localhost:8080/h2-console
```

## ⚙️ 配置说明

### Twitter监控配置
```yaml
twitter:
  monitor:
    enabled: true                    # 是否启用监控
    target-user: xiaozhaolucky      # 目标用户
    target-url: https://x.com/xiaozhaolucky  # 监控地址
    check-interval: 30              # 检查间隔(秒)
    timeout: 10000                  # 超时时间(毫秒)
```

### 股票数据配置
```yaml
stock:
  data-source: yahoo-finance         # 数据源
  update-interval: 60               # 更新间隔(秒)
```

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:stockdb        # H2内存数据库
    username: sa
    password:
```

## 🤝 贡献指南

我们欢迎任何形式的贡献！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详细信息。

### 快速贡献
1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 遵循 [开发规范](DEVELOPMENT_RULES.md)
4. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
5. 推送到分支 (`git push origin feature/AmazingFeature`)
6. 创建 Pull Request

## 📋 项目规范

- [开发规范](DEVELOPMENT_RULES.md) - 详细的开发规范和最佳实践
- [贡献指南](CONTRIBUTING.md) - 如何为项目做贡献
- [行为准则](CODE_OF_CONDUCT.md) - 社区行为准则

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- 🐛 **Bug报告**: [创建 Issue](https://github.com/TongqiLiu/web-crawler-project/issues)
- 💡 **功能建议**: [创建 Feature Request](https://github.com/TongqiLiu/web-crawler-project/issues)
- 💬 **讨论交流**: [Discussions](https://github.com/TongqiLiu/web-crawler-project/discussions)

## 🏆 致谢

感谢所有为这个项目做出贡献的开发者！

## ⚠️ 免责声明

- 请遵守相关网站的robots.txt协议和服务条款
- 合理使用API，避免频繁请求
- 投资有风险，分析结果仅供参考
- 请遵守相关法律法规

---

**让我们一起构建更智能的股票量化分析系统！** 🚀📈
