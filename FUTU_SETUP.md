# 富途OpenD安装指南

## 📋 概述

富途OpenD是富途OpenAPI的网关程序，负责处理API请求和数据转发。本指南将帮助您在macOS上安装和配置富途OpenD。

## 🔧 安装步骤

### 1. 手动下载富途OpenD

由于富途OpenD需要从官方网站手动下载，请按以下步骤操作：

1. **访问富途官方下载页面**:
   ```
   https://www.futunn.com/download/OpenAPI
   ```

2. **选择macOS版本**:
   - 点击"macOS"选项卡
   - 下载最新版本的OpenD

3. **下载到指定目录**:
   ```bash
   # 建议下载到以下目录
   ~/futu-opend/
   ```

### 2. 安装富途OpenD

```bash
# 1. 进入下载目录
cd ~/futu-opend/

# 2. 如果是DMG文件，双击安装
# 或者使用命令行挂载
hdiutil attach FutuOpenD_Mac.dmg

# 3. 复制应用到Applications目录
cp -R "/Volumes/FutuOpenD/FutuOpenD.app" /Applications/

# 4. 卸载DMG
hdiutil detach "/Volumes/FutuOpenD"
```

### 3. 启动富途OpenD

```bash
# 方法1: 从Applications启动
open /Applications/FutuOpenD.app

# 方法2: 命令行启动
/Applications/FutuOpenD.app/Contents/MacOS/FutuOpenD
```

### 4. 配置富途OpenD

启动后，OpenD会在以下端口运行：
- **默认端口**: 11111
- **Web控制台**: http://localhost:11111

### 5. 验证安装

```bash
# 检查端口是否开放
lsof -i :11111

# 测试连接
curl http://localhost:11111/status
```

## ⚙️ 配置说明

### OpenD配置文件

OpenD的配置文件通常位于：
```
~/Library/Application Support/FutuOpenD/config.ini
```

主要配置项：
```ini
[network]
port = 11111
enable_encrypt = false

[log]
level = INFO
file_path = ~/Library/Logs/FutuOpenD/

[api]
max_connections = 100
timeout = 30
```

### 项目配置

在我们的项目中，富途API配置位于 `application.yml`:

```yaml
futu:
  api:
    enabled: true
    host: 127.0.0.1
    port: 11111
    enable-encrypt: false
    connection-timeout: 3000
    keep-alive-interval: 10
```

## 🧪 测试连接

安装完成后，使用以下命令测试连接：

```bash
# 1. 检查OpenD是否运行
ps aux | grep FutuOpenD

# 2. 测试端口连接
telnet localhost 11111

# 3. 使用项目API测试
curl http://localhost:8080/api/analysis/futu/connect

# 4. 检查连接状态
curl http://localhost:8080/api/analysis/futu/status
```

## 🔐 权限和认证

### 富途账户要求

1. **开通OpenAPI权限**:
   - 登录富途牛牛APP
   - 申请OpenAPI使用权限
   - 获取API密钥和权限

2. **市场数据权限**:
   - 确保有美股行情权限
   - 开通实时数据订阅
   - 配置交易权限（如需要）

### 安全设置

```bash
# 1. 设置OpenD只允许本地连接
# 在config.ini中设置:
# allowed_ips = 127.0.0.1

# 2. 启用加密传输（可选）
# enable_encrypt = true
# cert_file = /path/to/cert.pem
```

## 🚀 启动脚本

创建便捷的启动脚本：

```bash
# 创建启动脚本
cat > ~/futu-opend/start-opend.sh << 'EOF'
#!/bin/bash
echo "🚀 启动富途OpenD..."
/Applications/FutuOpenD.app/Contents/MacOS/FutuOpenD &
echo "✅ OpenD已在后台启动"
echo "🌐 Web控制台: http://localhost:11111"
echo "📊 API端口: 11111"
EOF

chmod +x ~/futu-opend/start-opend.sh
```

## 🔧 故障排除

### 常见问题

1. **端口被占用**:
   ```bash
   # 查看端口占用
   lsof -i :11111
   
   # 杀死占用进程
   kill -9 <PID>
   ```

2. **权限问题**:
   ```bash
   # 给予执行权限
   chmod +x /Applications/FutuOpenD.app/Contents/MacOS/FutuOpenD
   ```

3. **网络连接问题**:
   ```bash
   # 检查防火墙设置
   sudo pfctl -s rules | grep 11111
   ```

### 日志查看

```bash
# OpenD日志位置
tail -f ~/Library/Logs/FutuOpenD/opend.log

# 项目日志
tail -f /tmp/spring-boot.log
```

## 📚 参考资源

- [富途OpenAPI官方文档](https://openapi.futunn.com/)
- [富途OpenD下载页面](https://www.futunn.com/download/OpenAPI)
- [API使用指南](https://openapi.futunn.com/futu-api-doc/)

## ⚠️ 注意事项

1. **合规使用**: 请遵守富途的API使用条款
2. **数据限制**: 注意API调用频率限制
3. **市场时间**: 某些数据只在交易时间可用
4. **网络稳定**: 确保网络连接稳定

---

**安装完成后，您的股票量化分析系统就可以使用真实的富途API数据了！** 🚀
