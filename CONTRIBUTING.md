# 贡献指南

感谢您对股票量化分析系统项目的关注！我们欢迎任何形式的贡献。

## 🚀 快速开始

### 环境要求
- JDK 11 或更高版本
- Maven 3.6 或更高版本
- Git

### 本地开发设置
```bash
# 1. 克隆项目
git clone https://github.com/TongqiLiu/web-crawler-project.git
cd web-crawler-project

# 2. 编译项目
mvn clean compile

# 3. 运行测试
mvn test

# 4. 启动应用
mvn spring-boot:run
```

## 📋 贡献类型

我们欢迎以下类型的贡献：

### 🐛 Bug报告
- 使用GitHub Issues报告bug
- 提供详细的重现步骤
- 包含错误日志和环境信息
- 使用bug报告模板

### 💡 功能建议
- 通过GitHub Issues提交功能请求
- 详细描述功能需求和使用场景
- 说明功能的预期行为

### 🔧 代码贡献
- Fork项目到您的GitHub账户
- 创建功能分支
- 遵循编码规范
- 添加适当的测试
- 提交Pull Request

### 📖 文档改进
- 修复文档错误
- 添加使用示例
- 翻译文档
- 改进API文档

## 🔄 开发流程

### 1. Fork和Clone
```bash
# Fork项目到您的GitHub账户，然后clone
git clone https://github.com/your-username/web-crawler-project.git
cd web-crawler-project

# 添加原始仓库作为upstream
git remote add upstream https://github.com/TongqiLiu/web-crawler-project.git
```

### 2. 创建功能分支
```bash
# 从main分支创建新的功能分支
git checkout -b feature/your-feature-name

# 或者修复bug
git checkout -b fix/bug-description
```

### 3. 开发和测试
```bash
# 进行开发
# ...

# 运行测试确保功能正常
mvn test

# 检查代码格式
mvn checkstyle:check
```

### 4. 提交更改
```bash
# 添加更改
git add .

# 提交更改（遵循提交信息规范）
git commit -m "feat: 添加新功能描述"
```

### 5. 推送和创建PR
```bash
# 推送到您的fork
git push origin feature/your-feature-name

# 在GitHub上创建Pull Request
```

## 📝 编码规范

请遵循项目的[开发规范](DEVELOPMENT_RULES.md)：

### 代码风格
- 使用4个空格缩进
- 行长度不超过120字符
- 使用有意义的变量和方法名
- 添加适当的注释

### 提交信息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建工具

示例：
```
feat(twitter): 添加Twitter实时监控功能

- 实现Twitter API集成
- 添加推文实时监控
- 支持多用户监控配置

Closes #123
```

## 🧪 测试要求

### 单元测试
- 新功能必须包含单元测试
- 测试覆盖率应保持在80%以上
- 使用JUnit 5和Mockito

### 集成测试
- 重要功能需要集成测试
- 测试真实的API调用和数据库操作

### 测试命名
```java
@Test
@DisplayName("应该在用户发布新推文时发送通知")
void shouldSendNotificationWhenUserPostsNewTweet() {
    // 测试实现
}
```

## 🔍 代码审查

### PR要求
- PR标题应清晰描述更改内容
- 包含详细的PR描述
- 关联相关的Issues
- 确保CI检查通过

### 审查流程
1. 自动化检查（CI/CD）
2. 代码审查（至少一个维护者）
3. 测试验证
4. 合并到main分支

## 🏷️ 版本发布

项目使用语义化版本：
- `MAJOR.MINOR.PATCH`
- 主版本：不兼容的API更改
- 次版本：向后兼容的功能添加
- 补丁版本：向后兼容的bug修复

## 📞 获取帮助

如果您在贡献过程中遇到问题：

1. 查看[开发规范](DEVELOPMENT_RULES.md)
2. 搜索现有的Issues
3. 创建新的Issue描述问题
4. 加入讨论区交流

## 🎯 优先级

当前项目重点关注：

### 高优先级
- Twitter监控功能完善
- 美股数据采集模块
- 技术分析指标实现
- Web界面开发

### 中优先级
- 性能优化
- 错误处理改进
- 文档完善
- 测试覆盖率提升

### 低优先级
- 代码重构
- 新功能探索
- 国际化支持

## 🏆 贡献者认可

我们会在以下方式认可贡献者：
- 在README中列出贡献者
- 在发布说明中感谢贡献者
- 为重要贡献者提供项目徽章

## 📜 许可证

通过贡献代码，您同意您的贡献将在与项目相同的[MIT许可证](LICENSE)下获得许可。

---

再次感谢您的贡献！让我们一起构建更好的股票量化分析系统。 🚀
