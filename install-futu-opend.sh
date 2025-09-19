#!/bin/bash

# 富途OpenD安装脚本
# 适用于macOS系统

echo "🚀 富途OpenD安装助手"
echo "===================="

# 检查系统
echo "🔍 检查系统环境..."
echo "操作系统: $(uname -s)"
echo "架构: $(uname -m)"
echo "macOS版本: $(sw_vers -productVersion)"

# 创建安装目录
INSTALL_DIR="$HOME/futu-opend"
echo ""
echo "📁 创建安装目录: $INSTALL_DIR"
mkdir -p "$INSTALL_DIR"
cd "$INSTALL_DIR"

# 检查是否已安装
if [ -d "/Applications/FutuOpenD.app" ]; then
    echo "✅ 检测到已安装的富途OpenD"
    echo "📍 安装位置: /Applications/FutuOpenD.app"
    
    # 检查是否运行
    if pgrep -f "FutuOpenD" > /dev/null; then
        echo "🟢 OpenD正在运行"
    else
        echo "🔴 OpenD未运行"
        echo "💡 启动命令: open /Applications/FutuOpenD.app"
    fi
else
    echo "❌ 未检测到富途OpenD安装"
    echo ""
    echo "📥 请手动下载富途OpenD:"
    echo "1. 访问: https://www.futunn.com/download/OpenAPI"
    echo "2. 选择macOS版本下载"
    echo "3. 安装到Applications目录"
    echo ""
    echo "🔗 或者使用以下命令下载:"
    echo "curl -L -o 'FutuOpenD.dmg' 'https://download.futunn.com/OpenAPI/FutuOpenD_Mac.dmg'"
fi

echo ""
echo "⚙️ 系统配置检查..."

# 检查Java环境
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo "✅ Java环境: $JAVA_VERSION"
else
    echo "❌ 未检测到Java环境"
fi

# 检查Python环境
if command -v python3 &> /dev/null; then
    PYTHON_VERSION=$(python3 --version)
    echo "✅ Python环境: $PYTHON_VERSION"
else
    echo "❌ 未检测到Python环境"
fi

# 检查端口占用
echo ""
echo "🔌 检查端口状态..."
if lsof -i :11111 &> /dev/null; then
    echo "⚠️  端口11111已被占用:"
    lsof -i :11111
else
    echo "✅ 端口11111可用"
fi

echo ""
echo "🎯 下一步操作:"
echo "1. 如果未安装OpenD，请手动下载并安装"
echo "2. 启动OpenD: open /Applications/FutuOpenD.app"
echo "3. 验证连接: curl http://localhost:11111"
echo "4. 测试项目集成: curl http://localhost:8080/api/analysis/futu/connect"

echo ""
echo "📚 参考文档:"
echo "- 安装指南: ./FUTU_SETUP.md"
echo "- 项目配置: ./src/main/resources/application.yml"
echo "- 富途官方文档: https://openapi.futunn.com/"

# 创建便捷启动脚本
cat > start-opend.sh << 'EOF'
#!/bin/bash
echo "🚀 启动富途OpenD..."
if [ -d "/Applications/FutuOpenD.app" ]; then
    open /Applications/FutuOpenD.app
    echo "✅ OpenD启动命令已执行"
    echo "🌐 Web控制台: http://localhost:11111"
    sleep 3
    echo "🔍 检查运行状态..."
    if lsof -i :11111 &> /dev/null; then
        echo "✅ OpenD正在运行"
    else
        echo "⚠️  OpenD可能正在启动中，请稍等..."
    fi
else
    echo "❌ 未找到FutuOpenD.app，请先安装"
fi
EOF

chmod +x start-opend.sh
echo ""
echo "✅ 创建了启动脚本: $PWD/start-opend.sh"
