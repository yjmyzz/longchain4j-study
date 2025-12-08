# langchain4j Study - 结构化数据提取示例

这是一个用于学习langchain4j的Spring Boot项目，集成了本地Ollama服务，演示了如何使用AI从文本中提取结构化数据。

**Package**: `com.cnblogs.yjmyzz.langchain4j.study`

## 🚀 项目特性

- **Java 25**: 使用最新的Java版本
- **Spring Boot 4.0.0**: 现代化的Spring Boot框架
- **LangChain4j 1.8.0**: 强大的Java AI框架
- **Ollama集成**: 支持本地大语言模型（默认使用deepseek-v3.1:671b-cloud）
- **结构化数据提取**: 使用AI从非结构化文本中提取结构化信息
- **Prompt模板**: 支持使用PromptTemplate进行提示词工程
- **AiServices**: 使用AiServices实现类型安全的数据提取接口
- **RESTful API**: 提供数据提取功能API接口

## 📋 前置要求

1. **Java 25**: 确保已安装JDK 25
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
# 下载聊天模型（默认模型）
ollama pull deepseek-v3.1:671b-cloud

# 或者下载其他模型
ollama pull qwen3:0.6b
ollama pull llama2
ollama pull llama2:7b
ollama pull llama2:13b
```

### 4. 克隆项目

```bash
git clone https://github.com/yjmyzz/langchain4j-study.git
cd langchain4j-study
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

#### 结构化数据提取功能演示

##### 1. 使用PromptTemplate提取数据（JSON格式）

```bash
# 从文本中提取结构化数据，返回JSON格式字符串
curl "http://localhost:8080/api/extract"
```

**功能说明**：
- 使用 `PromptTemplate` 构建提示词
- 从人物生平介绍中提取基本信息（姓名、年龄、出生日期、是否健在、死亡日期、最高学历）
- 返回JSON格式的结构化数据
- 示例数据：金庸的生平介绍

**返回示例**：
```json
{
  "name": "金庸",
  "age": 94,
  "birthDay": "1924-03-10",
  "isAlive": false,
  "deathDate": "2018-10-30",
  "degree": "哲学博士"
}
```

##### 2. 使用AiServices提取数据（类型安全）

```bash
# 使用AiServices从文本中提取结构化数据，返回Java对象
curl "http://localhost:8080/api/extract2"
```

**功能说明**：
- 使用 `AiServices` 创建类型安全的数据提取接口
- 定义 `PersonExtractor` 接口，使用 `@SystemMessage` 指定系统提示词
- 返回强类型的 `Person` 对象（Java Record）
- 自动进行JSON序列化和反序列化
- 更类型安全，更易于维护

**返回示例**：
```json
{
  "name": "金庸",
  "age": 94,
  "birthDay": "1924-03-10T00:00:00.000+00:00",
  "isAlive": false,
  "deathDate": "2018-10-30T00:00:00.000+00:00",
  "degree": "哲学博士"
}
```

## ⚙️ 配置说明

项目配置文件位于 `src/main/resources/application.yml`：

```yaml
# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /

# Spring应用配置
spring:
  application:
    name: langchain4j-study
  
  # 日志配置
  logging:
    level:
      com.example.langchain4jstudy: DEBUG
      dev.langchain4j: DEBUG
    pattern:
      console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Ollama配置
ollama:
  base-url: http://localhost:11434          # Ollama服务地址
  model: deepseek-v3.1:671b-cloud           # 聊天模型名称
  timeout: 60                               # 请求超时时间（秒）

# 应用信息
info:
  app:
    name: langchain4j Study
    version: 1.0.0
    description: langchain4j学习项目 - 结构化数据输出
```

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/cnblogs/yjmyzz/langchain4j/study/
│   │   ├── LongChain4jStudyApplication.java    # 主启动类
│   │   ├── config/
│   │   │   └── OllamaConfig.java              # Ollama配置类
│   │   └── controller/
│   │       └── ExtractDataController.java     # 数据提取功能控制器
│   └── resources/
│       └── application.yml                     # 应用配置
└── test/
    └── java/com/cnblogs/yjmyzz/langchain4j/study/
        └── LangChain4jStudyApplicationTests.java  # 应用测试
```

## 📦 Package结构

项目使用标准的Maven package命名规范：
- **GroupId**: `com.yjmyzz`
- **ArtifactId**: `langchain4j-study`
- **Version**: `1.0.0`
- **Package**: `com.cnblogs.yjmyzz.langchain4j.study`
- **主类**: `LongChain4jStudyApplication`

## 🔧 核心组件说明

### 1. 配置类

#### OllamaConfig.java
- 配置Ollama聊天模型
- 支持自定义模型名称、服务地址和超时时间
- 启用请求和响应日志记录
- 使用 `@Bean` 注解注册为Spring Bean，支持依赖注入
- Bean名称：
  - `ollamaChatModel` - 聊天模型

### 2. 控制器

#### ExtractDataController.java
- 提供结构化数据提取功能演示
- 演示两种数据提取方式：
  - **方式1**：使用 `PromptTemplate` 构建提示词，返回JSON字符串
  - **方式2**：使用 `AiServices` 创建类型安全的提取接口，返回Java对象
- 提供两个API接口：
  - `/api/extract` - 使用PromptTemplate提取数据（返回JSON字符串）
  - `/api/extract2` - 使用AiServices提取数据（返回Java对象）
- 使用 `@SystemMessage` 指定系统提示词
- 定义 `Person` Record类型用于结构化数据
- 定义 `PersonExtractor` 接口用于类型安全的数据提取
- 支持CORS跨域请求
- 示例数据：从金庸的生平介绍中提取人物基本信息

### 3. 主要依赖
- **Spring Boot Web**: Web应用支持
- **Spring Boot Validation**: 数据验证支持
- **Spring WebFlux**: 响应式编程支持
- **LangChain4j**: AI框架核心（版本 1.8.0）
- **LangChain4j Ollama**: Ollama集成（包含聊天模型支持）
- **Lombok**: 代码简化工具（可选依赖）

## 🧪 测试

### 运行所有测试

```bash
mvn test
```

### 运行特定测试

```bash
mvn test -Dtest=com.cnblogs.yjmyzz.langchain4j.study.LangChain4jStudyApplicationTests
```

## 🔧 开发指南

### 添加新的数据提取功能

#### 方式1：使用PromptTemplate

1. 在 `ExtractDataController` 中添加新的端点方法
2. 注入 `OllamaChatModel`（已配置为Spring Bean）
3. 使用 `PromptTemplate.from()` 创建提示词模板
4. 使用 `apply()` 方法填充模板变量
5. 调用 `ollamaChatModel.chat()` 获取AI响应
6. 返回JSON字符串

**示例**：
```java
@Autowired
@Qualifier("ollamaChatModel")
OllamaChatModel ollamaChatModel;

@GetMapping("/extract-custom")
public ResponseEntity<String> extractCustom(@RequestParam String text) {
    try {
        Prompt prompt = PromptTemplate.from(
            "请从以下文本中提取信息，以JSON格式输出：{{text}}"
        ).apply(Map.of("text", text));
        
        String result = ollamaChatModel.chat(prompt.toUserMessage())
            .aiMessage().text();
        
        return ResponseEntity.ok(result);
    } catch (Exception e) {
        return ResponseEntity.ok("{\"error\":\"" + e.getMessage() + "\"}");
    }
}
```

#### 方式2：使用AiServices（推荐）

1. 定义数据模型（使用Record或Class）
2. 定义提取接口，使用 `@SystemMessage` 指定系统提示词
3. 使用 `AiServices.create()` 创建提取器实例
4. 调用提取方法获取结构化数据
5. 返回强类型的Java对象

**示例**：
```java
// 定义数据模型
record Product(String name, double price, String category) {}

// 定义提取接口
interface ProductExtractor {
    @SystemMessage("从商品描述中提取商品信息：name[名称], price[价格], category[类别]")
    Product extractProduct(String description);
}

// 使用提取器
@GetMapping("/extract-product")
public ResponseEntity<Product> extractProduct(@RequestParam String description) {
    try {
        ProductExtractor extractor = AiServices.create(
            ProductExtractor.class, 
            ollamaChatModel
        );
        Product product = extractor.extractProduct(description);
        return ResponseEntity.ok(product);
    } catch (Exception e) {
        return ResponseEntity.ok(new Product("", 0.0, ""));
    }
}
```

### 自定义配置

可以通过修改 `application.yml` 来调整：
- Ollama服务配置
    - 服务地址（`ollama.base-url`）
    - 聊天模型（`ollama.model`，默认：deepseek-v3.1:671b-cloud）
    - 超时时间（`ollama.timeout`，单位：秒）
- 日志级别和格式
- 服务器端口（默认8080）

**注意**:
- 日志配置中的package路径为 `com.example.langchain4jstudy`
- 修改配置后需要重启应用才能生效
- 确保使用的聊天模型已在Ollama中下载：`ollama pull deepseek-v3.1:671b-cloud`

## 🐛 故障排除

### 常见问题

1. **Ollama连接失败**
    - 确保Ollama服务已启动：`ollama serve`
    - 检查端口11434是否被占用
    - 验证模型是否已下载：`ollama list`
    - 确认使用的模型名称正确（默认：deepseek-v3.1:671b-cloud）

2. **数据提取结果不准确**
   - 优化系统提示词（`@SystemMessage`）的描述
   - 在PromptTemplate中提供更清晰的示例格式
   - 检查输入文本的质量和完整性
   - 尝试使用不同的模型（如更大的模型）

3. **模型响应缓慢**
    - 检查硬件资源（CPU、内存）
    - 考虑使用更小的模型
    - 调整超时配置
    - 对于本地模型，考虑使用GPU加速

4. **内存不足**
    - 增加JVM堆内存：`-Xmx4g`
    - 使用更小的模型
    - 减少处理的文本长度

5. **JSON解析错误**
    - 检查AI返回的JSON格式是否正确
    - 使用 `AiServices` 方式可以自动处理JSON序列化/反序列化
    - 在PromptTemplate中提供更明确的JSON格式示例

6. **Java 25 兼容性**
    - 项目使用 Java 25，确保已安装 JDK 25
    - Maven编译器插件设置为Java 25
    - Lombok为可选依赖，打包时会被排除

## 📝 许可证

本项目采用 MIT 许可证。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目！

## 📞 联系方式

如有问题，请通过以下方式联系：
- 提交GitHub Issue: https://github.com/yjmyzz/langchain4j-study/issues
- 作者博客: http://yjmyzz.cnblogs.com
- 作者: 菩提树下的杨过

## 🙏 致谢

感谢 [LangChain4j](https://github.com/langchain4j/langchain4j) 开源项目提供的强大支持！

特别感谢以下官方文档资源：
- [LangChain4j 中文文档](https://docs.langchain4j.info/) - 为Java应用赋能大模型能力的官方中文指南
- [LangChain4j 英文文档](https://docs.langchain4j.dev/) - 官方英文文档，提供完整的技术参考
- [Ollama官网](https://ollama.ai/) - 本地大语言模型运行环境
- [MCP协议文档](https://modelcontextprotocol.io/) - Model Context Protocol 官方文档

## ⚠️ 重要说明

### Java 25 兼容性

项目使用 Java 25 和 Spring Boot 4.0.0 进行开发：

- **Java 25**: 确保已安装 JDK 25
- **Maven配置**: 编译器源码和目标版本都设置为25
- **Lombok**: 作为可选依赖，打包时会被排除
- 所有日志记录使用标准的 SLF4J Logger

### 结构化数据提取功能说明

项目演示了如何使用 LangChain4j 从非结构化文本中提取结构化数据：

1. **PromptTemplate方式**: 使用模板构建提示词，灵活控制输出格式
   - 支持变量替换和动态内容填充
   - 可以指定JSON格式示例
   - 返回JSON字符串，需要手动解析

2. **AiServices方式**: 使用类型安全的接口定义，自动处理数据转换
   - 定义Java接口和Record类型
   - 使用 `@SystemMessage` 指定系统提示词
   - 自动进行JSON序列化和反序列化
   - 返回强类型的Java对象，更易于使用

3. **应用场景**:
   - 从简历中提取个人信息
   - 从产品描述中提取商品信息
   - 从新闻文章中提取关键信息
   - 从合同文档中提取条款信息
   - 任何需要将非结构化文本转换为结构化数据的场景

4. **优势**:
   - 类型安全：使用Java类型系统保证数据正确性
   - 易于维护：接口定义清晰，便于扩展
   - 灵活配置：可以自定义提示词和输出格式

### 技术架构

- **Spring Boot**: 提供Web服务和依赖注入
- **LangChain4j**: 提供AI集成能力
  - `PromptTemplate`: 提示词模板引擎
  - `AiServices`: 类型安全的AI服务构建器
- **Ollama**: 提供本地大语言模型服务
- **Java Record**: 用于定义结构化数据模型

---

**注意**: 请确保在使用前已正确安装和配置Ollama服务，并下载所需的模型。
