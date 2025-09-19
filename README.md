# Web Crawler Project

一个基于Java的Web爬虫项目，用于数据提取和网页内容抓取。

## 功能特性

- 支持HTTP/HTTPS网页抓取
- HTML内容解析和数据提取
- JSON数据处理
- 可配置的爬虫策略
- 日志记录和错误处理
- 单元测试支持

## 技术栈

- Java 11+
- Maven
- Jsoup (HTML解析)
- Apache HttpClient (HTTP客户端)
- Jackson (JSON处理)
- SLF4J + Logback (日志)
- JUnit 5 (测试)

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

## 使用示例

```java
// 创建爬虫实例
WebCrawler crawler = new WebCrawler();

// 抓取网页内容
String content = crawler.fetchPage("https://example.com");

// 解析HTML
Document doc = crawler.parseHtml(content);

// 提取数据
String title = doc.select("title").text();
```

## 配置说明

项目支持通过配置文件进行参数调整：

- 请求超时时间
- 重试次数
- 用户代理设置
- 代理配置

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 创建 Issue
- 发送邮件

---

**注意**: 请遵守网站的robots.txt协议和相关法律法规，合理使用爬虫工具。
