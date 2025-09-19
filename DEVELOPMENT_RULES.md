# 股票量化分析系统 - 开发规范

## 📋 项目概述

本项目是一个基于Java Spring Boot的股票量化分析系统，集成了Twitter监控、美股数据采集、技术分析等功能。

## 🏗️ 项目架构

### 技术栈
- **后端**: Java 11+, Spring Boot 3.2.0, Maven
- **数据库**: H2 (开发), MySQL/PostgreSQL (生产)
- **前端**: Thymeleaf + Bootstrap (后续可扩展React/Vue)
- **监控**: Twitter API, 系统通知
- **数据源**: Yahoo Finance API, Alpha Vantage

### 项目结构
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       ├── quant/                 # 主包
│   │       │   ├── config/            # 配置类
│   │       │   ├── twitter/           # Twitter监控模块
│   │       │   ├── stock/             # 股票数据模块
│   │       │   ├── analysis/          # 技术分析模块
│   │       │   ├── web/               # Web控制器
│   │       │   └── StockQuantApplication.java
│   │       └── crawler/               # 原爬虫模块(兼容)
│   └── resources/
│       ├── application.yml            # 主配置文件
│       ├── static/                    # 静态资源
│       └── templates/                 # 模板文件
└── test/                              # 测试代码
```

## 📝 编码规范

### 1. 命名规范
- **类名**: PascalCase (如: `TwitterMonitor`, `StockDataService`)
- **方法名**: camelCase (如: `startMonitoring`, `fetchStockData`)
- **变量名**: camelCase (如: `targetUser`, `checkInterval`)
- **常量名**: UPPER_SNAKE_CASE (如: `DEFAULT_TIMEOUT`, `MAX_RETRY_COUNT`)
- **包名**: 全小写 (如: `com.quant.twitter`, `com.quant.analysis`)

### 2. 注释规范
```java
/**
 * Twitter监控器
 * 监控指定用户的推文更新并发送通知
 * 
 * @author 系统
 * @version 1.0
 * @since 2025-09-20
 */
@Component
public class TwitterMonitor {
    
    /**
     * 开始监控Twitter用户
     * 
     * @param user 目标用户名
     * @throws IllegalArgumentException 当用户名为空时
     */
    public void startMonitoring(String user) {
        // 实现代码
    }
}
```

### 3. 异常处理
```java
// 使用具体的异常类型
try {
    // 业务逻辑
} catch (IOException e) {
    logger.error("网络请求失败: {}", e.getMessage(), e);
    throw new ServiceException("数据获取失败", e);
} catch (Exception e) {
    logger.error("未知错误: {}", e.getMessage(), e);
    throw new SystemException("系统异常", e);
}
```

### 4. 日志规范
```java
// 使用SLF4J
private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

// 日志级别使用
logger.debug("调试信息: {}", debugInfo);
logger.info("业务信息: {}", businessInfo);
logger.warn("警告信息: {}", warnInfo);
logger.error("错误信息: {}", errorInfo, exception);
```

## 🔧 配置管理

### 1. 配置文件结构
```yaml
# 服务器配置
server:
  port: 8080

# Spring配置
spring:
  application:
    name: stock-quant-system
  profiles:
    active: dev

# 业务配置
twitter:
  monitor:
    enabled: true
    target-user: xiaozhaolucky
    target-url: https://x.com/xiaozhaolucky
    check-interval: 30

stock:
  data-source: yahoo-finance
  update-interval: 60
```

### 2. 配置类规范
```java
@Component
@ConfigurationProperties(prefix = "twitter.monitor")
public class TwitterConfig {
    private boolean enabled = true;
    private String targetUser;
    // getter/setter
}
```

## 🧪 测试规范

### 1. 单元测试
```java
@ExtendWith(MockitoExtension.class)
class TwitterMonitorTest {
    
    @Mock
    private TwitterConfig config;
    
    @InjectMocks
    private TwitterMonitor monitor;
    
    @Test
    @DisplayName("应该成功启动监控")
    void shouldStartMonitoringSuccessfully() {
        // Given
        when(config.isEnabled()).thenReturn(true);
        
        // When
        monitor.startMonitoring();
        
        // Then
        assertTrue(monitor.isMonitoring());
    }
}
```

### 2. 集成测试
```java
@SpringBootTest
@TestPropertySource(properties = {
    "twitter.monitor.enabled=false"
})
class TwitterServiceIntegrationTest {
    
    @Autowired
    private TwitterService service;
    
    @Test
    void contextLoads() {
        assertNotNull(service);
    }
}
```

## 📊 性能规范

### 1. 数据库操作
- 使用连接池管理数据库连接
- 避免N+1查询问题
- 合理使用索引
- 批量操作优于循环单次操作

### 2. 缓存策略
```java
@Cacheable(value = "stockData", key = "#symbol")
public StockData getStockData(String symbol) {
    // 数据获取逻辑
}
```

### 3. 异步处理
```java
@Async
@Scheduled(fixedRate = 30000)
public void checkTwitterUpdates() {
    // 异步执行监控逻辑
}
```

## 🔒 安全规范

### 1. 敏感信息保护
- API密钥使用环境变量或加密配置
- 不在代码中硬编码密码
- 使用HTTPS进行数据传输

### 2. 输入验证
```java
public void updateUser(@Valid @RequestBody UserRequest request) {
    // Spring Validation自动验证
}
```

### 3. 错误处理
- 不向客户端暴露内部错误详情
- 记录安全相关的操作日志

## 🚀 部署规范

### 1. 环境配置
- **开发环境**: H2内存数据库，详细日志
- **测试环境**: MySQL数据库，中等日志级别
- **生产环境**: MySQL/PostgreSQL，错误日志

### 2. 监控指标
- 应用健康检查: `/actuator/health`
- 系统指标: `/actuator/metrics`
- Twitter监控状态
- 股票数据更新频率

## 📋 Git规范

### 1. 分支策略
- `main`: 生产分支
- `develop`: 开发分支
- `feature/功能名`: 功能分支
- `hotfix/修复名`: 热修复分支

### 2. 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型说明:
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建/工具相关

示例:
```
feat(twitter): 添加Twitter地址配置功能

- 支持动态配置Twitter监控地址
- 添加TwitterConfig配置类
- 更新监控脚本支持新配置

Closes #123
```

## 🔄 持续集成

### 1. 自动化测试
- 提交时运行单元测试
- 合并前运行集成测试
- 部署前运行端到端测试

### 2. 代码质量
- 使用SonarQube进行代码质量检查
- 保持测试覆盖率 > 80%
- 遵循代码规范检查

## 📖 文档规范

### 1. API文档
- 使用Swagger/OpenAPI生成API文档
- 提供完整的请求/响应示例

### 2. 系统文档
- README.md: 项目介绍和快速开始
- DEVELOPMENT_RULES.md: 开发规范(本文档)
- DEPLOYMENT.md: 部署指南
- API.md: API文档

## 🐛 问题处理

### 1. Bug报告
- 使用GitHub Issues跟踪问题
- 提供详细的重现步骤
- 包含错误日志和环境信息

### 2. 紧急问题
- 生产环境问题优先级最高
- 建立问题升级机制
- 保持问题处理记录

---

**最后更新**: 2025-09-20  
**版本**: 1.0  
**维护者**: 股票量化分析团队
