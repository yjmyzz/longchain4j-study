# LongChain4j Study - Ollama聊天示例

这是一个用于学习LongChain4j的Spring Boot项目，集成了本地Ollama服务，提供聊天功能示例。

**Package**: `com.cnblogs.yjmyzz.longchain4j.study`

## 🚀 项目特性

- **Java 21**: 使用最新的Java LTS版本
- **Spring Boot 3.2.0**: 现代化的Spring Boot框架
- **LongChain4j 1.1.0**: 强大的Java AI框架
- **Ollama集成**: 支持本地大语言模型
- **RESTful API**: 提供完整的聊天API接口
- **流式响应**: 支持Server-Sent Events (SSE)流式聊天
- **Lombok**: 减少样板代码
- **完整测试**: 包含单元测试和集成测试

## 📋 前置要求

1. **Java 21**: 确保已安装JDK 21
2. **Maven 3.6+**: 确保已安装Maven
3. **Ollama**: 确保已安装并启动Ollama服务

## 🛠️ 安装和配置

### 1. 安装Ollama

访问 [Ollama官网](https://ollama.ai/) 下载并安装Ollama。

### 2. 启动Ollama服务

```bash
# 启动Ollama服务
ollama serve
```

### 3. 下载模型

```bash
# 下载qwen3:0.6b模型（默认模型）
ollama pull qwen3:0.6b

# 或者下载其他模型
ollama pull llama2
ollama pull llama2:7b
ollama pull llama2:13b
```

### 4. 克隆项目

```bash
git clone https://github.com/yjmyzz/longchain4j-study.git
cd longchain4j-study
```

### 5. 编译项目

```bash
mvn clean compile
```

### 6. 运行项目

```bash
mvn spring-boot:run
```

## 🌐 使用方式

### API接口

#### 发送聊天消息

```bash
curl "http://localhost:8080/api/chat?prompt=你好，请介绍一下Java编程语言"
```

#### 流式聊天消息

```bash
curl "http://localhost:8080/api/chat/stream?prompt=你好，请介绍一下Java编程语言"
```

**注意**: 流式API返回HTML格式的SSE数据，适合在浏览器中直接测试。

#### 健康检查

```bash
curl http://localhost:8080/api/health
```

## ⚙️ 配置说明

项目配置文件位于 `src/main/resources/application.yml`：

```yaml
# 服务器配置
server:
  port: 8080

# Spring应用配置
spring:
  application:
    name: longchain4j-study
  
  # 日志配置
  logging:
    level:
      com.cnblogs.yjmyzz.longchain4j.study: DEBUG
      dev.langchain4j: DEBUG

# Ollama配置
ollama:
  base-url: http://localhost:11434  # Ollama服务地址
  model: qwen3:0.6b                 # 使用的模型名称
  timeout: 60                       # 请求超时时间（秒）
```

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/cnblogs/yjmyzz/longchain4j/study/
│   │   ├── LongChain4jStudyApplication.java    # 主启动类
│   │   ├── config/
│   │   │   └── OllamaConfig.java              # Ollama配置类
│   │   └── controller/
│   │       └── ChatController.java            # 聊天控制器
│   └── resources/
│       └── application.yml                     # 应用配置
└── test/
    └── java/com/cnblogs/yjmyzz/longchain4j/study/
        └── LongChain4jStudyApplicationTests.java  # 应用测试
```

## 📦 Package结构

项目使用标准的Maven package命名规范：
- **GroupId**: `com.yjmyzz`
- **Package**: `com.cnblogs.yjmyzz.longchain4j.study`
- **主类**: `LongChain4jStudyApplication`

## 🔧 核心组件说明

### 1. OllamaConfig.java
- 配置Ollama聊天模型和流式聊天模型
- 支持自定义模型名称、服务地址和超时时间
- 启用请求和响应日志记录

### 2. ChatController.java
- 提供RESTful API接口
- 支持普通聊天和流式聊天
- 实现Server-Sent Events (SSE)流式响应
- 包含健康检查端点
- 支持CORS跨域请求

### 3. 主要依赖
- **Spring Boot Web**: Web应用支持
- **Spring WebFlux**: 响应式编程支持
- **LongChain4j**: AI框架核心
- **LongChain4j Ollama**: Ollama集成
- **Lombok**: 代码简化工具

## 🧪 测试

### 运行所有测试

```bash
mvn test
```

### 运行特定测试

```bash
mvn test -Dtest=com.cnblogs.yjmyzz.longchain4j.study.LongChain4jStudyApplicationTests
```

## 🔧 开发指南

### 添加新的模型支持

1. 在 `application.yml` 中修改 `ollama.model` 配置
2. 确保对应的模型已在Ollama中下载

### 扩展聊天功能

1. 在 `ChatController` 中添加新的业务逻辑
2. 添加新的API端点
3. 实现自定义的响应处理器

### 自定义配置

可以通过修改 `application.yml` 来调整：
- Ollama服务地址
- 使用的模型
- 超时时间
- 日志级别

**注意**: 日志配置中的package路径为 `com.cnblogs.yjmyzz.longchain4j.study`

## 🐛 故障排除

### 常见问题

1. **Ollama连接失败**
   - 确保Ollama服务已启动：`ollama serve`
   - 检查端口11434是否被占用
   - 验证模型是否已下载：`ollama list`

2. **模型响应缓慢**
   - 检查硬件资源（CPU、内存）
   - 考虑使用更小的模型
   - 调整超时配置

3. **内存不足**
   - 增加JVM堆内存：`-Xmx4g`
   - 使用更小的模型
   - 优化批处理大小

4. **流式响应问题**
   - 确保浏览器支持SSE
   - 检查网络连接稳定性
   - 查看应用日志排查问题

## 📝 许可证

本项目采用 MIT 许可证。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目！

## 📞 联系方式

如有问题，请通过以下方式联系：
- 提交GitHub Issue: https://github.com/yjmyzz/longchain4j-study/issues
- 作者博客: http://yjmyzz.cnblogs.com
- 作者: 菩提树下的杨过

## 🙏 致谢

感谢 [LangChain4j](https://github.com/langchain4j/langchain4j) 开源项目提供的强大支持！

特别感谢以下官方文档资源：
- [LangChain4j 中文文档](https://docs.langchain4j.info/) - 为Java应用赋能大模型能力的官方中文指南
- [LangChain4j 英文文档](https://docs.langchain4j.dev/) - 官方英文文档，提供完整的技术参考

---

**注意**: 请确保在使用前已正确安装和配置Ollama服务。
