# langchain4j Study - 文本分类示例

这是一个用于学习langchain4j的Spring Boot项目，集成了本地Ollama服务，演示了如何使用AI进行文本分类。项目提供了两种分类方式：基于大语言模型的分类和基于嵌入模型的分类。

**Package**: `com.cnblogs.yjmyzz.langchain4j.study`

## 🚀 项目特性

- **Java 25**: 使用最新的Java版本
- **Spring Boot 4.0.0**: 现代化的Spring Boot框架
- **LangChain4j 1.8.0**: 强大的Java AI框架
- **Ollama集成**: 支持本地大语言模型和嵌入模型
  - 聊天模型：默认使用 `deepseek-v3.1:671b-cloud`
  - 嵌入模型：默认使用 `nomic-embed-text:latest`
- **文本分类**: 演示客服问题自动分类功能
- **两种分类方式**:
  - **AiServices分类**: 使用大语言模型进行智能分类
  - **嵌入模型分类**: 使用EmbeddingModelTextClassifier进行基于相似度的分类
- **RESTful API**: 提供文本分类功能API接口

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
# 下载聊天模型（默认模型，用于AiServices分类）
ollama pull deepseek-v3.1:671b-cloud

# 下载嵌入模型（用于EmbeddingModelTextClassifier分类）
ollama pull nomic-embed-text:latest

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

#### 文本分类功能演示

项目提供了两种文本分类方式，用于将客服问题自动分类到不同的类别。

**支持的分类类别**：
- **产品相关** (PRODUCT): 产品使用、保修、质量问题等
- **订单相关** (ORDER): 订单查询、配送、物流等
- **账户相关** (ACCOUNT): 登录、密码、账号管理等
- **会员相关** (MEMBER): 会员等级、积分、优惠券等
- **支付相关** (PAYMENT): 支付方式、发票、退款等
- **其它问题** (OTHERS): 其他未分类问题

##### 1. 使用AiServices进行分类（基于大语言模型）

```bash
# 使用大语言模型进行智能分类
curl "http://localhost:8080/api/classify?query=能给我发个支付99折优惠券吗"
```

**功能说明**：
- 使用 `AiServices` 创建类型安全的分类接口
- 定义 `CustomerServiceCategoryClassifier` 接口，使用 `@UserMessage` 指定提示词
- 基于大语言模型的语义理解能力进行分类
- 返回分类类别的中文描述

**返回示例**：
```
支付相关
```

**请求示例**：
```bash
# 测试不同的问题
curl "http://localhost:8080/api/classify?query=我的订单现在到哪里了？"
# 返回：订单相关

curl "http://localhost:8080/api/classify?query=我的密码过期了？"
# 返回：账户相关

curl "http://localhost:8080/api/classify?query=产品的保修期过了怎么办？"
# 返回：产品相关
```

##### 2. 使用EmbeddingModelTextClassifier进行分类（基于嵌入模型）

```bash
# 使用嵌入模型进行基于相似度的分类
curl "http://localhost:8080/api/classify/embed?query=能给我发个支付99折优惠券吗"
```

**功能说明**：
- 使用 `EmbeddingModelTextClassifier` 进行分类
- 基于文本嵌入向量的相似度计算
- 通过示例数据（few-shot learning）进行分类
- 每个类别都有多个示例问题，通过计算相似度找到最匹配的类别
- 性能更好，适合大规模分类场景

**返回示例**：
```
支付相关
```

**优势对比**：

| 特性 | AiServices分类 | EmbeddingModel分类 |
|------|---------------|-------------------|
| 分类方式 | 基于大语言模型理解 | 基于向量相似度 |
| 准确性 | 高（语义理解强） | 较高（依赖示例质量） |
| 性能 | 较慢（需要完整推理） | 快（向量计算） |
| 成本 | 较高 | 较低 |
| 适用场景 | 复杂语义理解 | 大规模批量分类 |

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
  model: deepseek-v3.1:671b-cloud           # 聊天模型名称（用于AiServices分类）
  embedding-model: nomic-embed-text:latest  # 嵌入模型名称（用于EmbeddingModel分类）
  timeout: 60                               # 请求超时时间（秒）

# 应用信息
info:
  app:
    name: langchain4j Study
    version: 1.0.0
    description: langchain4j学习项目 - 分类示例
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
│   │       └── ClassifierController.java      # 文本分类功能控制器
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
- 配置Ollama聊天模型和嵌入模型
- 支持自定义模型名称、服务地址和超时时间
- 启用请求和响应日志记录
- 使用 `@Bean` 注解注册为Spring Bean，支持依赖注入
- Bean名称：
  - `ollamaChatModel` - 聊天模型（用于AiServices分类）
  - `ollamaEmbeddingModel` - 嵌入模型（用于EmbeddingModelTextClassifier分类）

### 2. 控制器

#### ClassifierController.java
- 提供文本分类功能演示
- 演示两种分类方式：
  - **方式1**：使用 `AiServices` 创建类型安全的分类接口，基于大语言模型
  - **方式2**：使用 `EmbeddingModelTextClassifier` 进行基于嵌入向量的分类
- 提供两个API接口：
  - `/api/classify` - 使用AiServices进行分类（基于大语言模型）
  - `/api/classify/embed` - 使用EmbeddingModelTextClassifier进行分类（基于嵌入模型）
- 定义 `CustomerServiceCategory` 枚举类型，包含6个分类类别
- 定义 `CustomerServiceCategoryClassifier` 接口用于类型安全的分类
- 使用 `@UserMessage` 指定用户提示词模板
- 提供 `getExamples()` 方法，为每个类别准备示例问题（few-shot learning）
- 支持客服问题自动分类场景

### 3. 主要依赖
- **Spring Boot Web**: Web应用支持
- **Spring Boot Validation**: 数据验证支持
- **Spring WebFlux**: 响应式编程支持
- **LangChain4j**: AI框架核心（版本 1.8.0）
- **LangChain4j Ollama**: Ollama集成（包含聊天模型和嵌入模型支持）
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

### 添加新的分类功能

#### 方式1：使用AiServices进行分类（推荐用于复杂语义理解）

1. 定义分类枚举类型（使用Enum）
2. 定义分类接口，使用 `@UserMessage` 指定提示词模板
3. 使用 `AiServices.create()` 创建分类器实例
4. 调用分类方法获取分类结果
5. 返回分类枚举值

**示例**：
```java
// 定义分类枚举
enum ArticleCategory {
    TECHNOLOGY("技术"),
    BUSINESS("商业"),
    SPORTS("体育");
    
    @Getter
    private final String desc;
    
    ArticleCategory(String desc) {
        this.desc = desc;
    }
}

// 定义分类接口
interface ArticleClassifier {
    @UserMessage("将文章【{{text}}】归类到以下类别：技术、商业、体育")
    ArticleCategory classify(String text);
}

// 使用分类器
@GetMapping("/classify-article")
public ResponseEntity<String> classifyArticle(@RequestParam String text) {
    try {
        ArticleClassifier classifier = AiServices.create(
            ArticleClassifier.class, 
            ollamaChatModel
        );
        ArticleCategory category = classifier.classify(text);
        return ResponseEntity.ok(category.getDesc());
    } catch (Exception e) {
        return ResponseEntity.ok("分类错误: " + e.getMessage());
    }
}
```

#### 方式2：使用EmbeddingModelTextClassifier进行分类（推荐用于大规模分类）

1. 定义分类枚举类型（使用Enum）
2. 准备每个类别的示例数据（few-shot learning）
3. 使用 `EmbeddingModelTextClassifier` 创建分类器
4. 调用 `classify()` 方法获取分类结果列表
5. 返回最匹配的分类结果

**示例**：
```java
// 定义分类枚举
enum Sentiment {
    POSITIVE("正面"),
    NEGATIVE("负面"),
    NEUTRAL("中性");
    
    @Getter
    private final String desc;
    
    Sentiment(String desc) {
        this.desc = desc;
    }
}

// 准备示例数据
Map<Sentiment, List<String>> examples = new HashMap<>();
examples.put(POSITIVE, asList("太好了", "非常满意", "很棒"));
examples.put(NEGATIVE, asList("太差了", "不满意", "糟糕"));
examples.put(NEUTRAL, asList("一般", "还可以", "还行"));

// 使用分类器
@GetMapping("/classify-sentiment")
public ResponseEntity<String> classifySentiment(@RequestParam String text) {
    try {
        TextClassifier<Sentiment> classifier = new EmbeddingModelTextClassifier<>(
            ollamaEmbeddingModel, 
            examples
        );
        List<Sentiment> results = classifier.classify(text);
        if (!results.isEmpty()) {
            return ResponseEntity.ok(results.get(0).getDesc());
        }
        return ResponseEntity.ok("无法分类");
    } catch (Exception e) {
        return ResponseEntity.ok("分类错误: " + e.getMessage());
    }
}
```

### 自定义配置

可以通过修改 `application.yml` 来调整：
- Ollama服务配置
    - 服务地址（`ollama.base-url`）
    - 聊天模型（`ollama.model`，默认：deepseek-v3.1:671b-cloud）
    - 嵌入模型（`ollama.embedding-model`，默认：nomic-embed-text:latest）
    - 超时时间（`ollama.timeout`，单位：秒）
- 日志级别和格式
- 服务器端口（默认8080）

**注意**:
- 日志配置中的package路径为 `com.example.langchain4jstudy`
- 修改配置后需要重启应用才能生效
- 确保使用的模型已在Ollama中下载：
  - 聊天模型：`ollama pull deepseek-v3.1:671b-cloud`
  - 嵌入模型：`ollama pull nomic-embed-text:latest`

## 🐛 故障排除

### 常见问题

1. **Ollama连接失败**
    - 确保Ollama服务已启动：`ollama serve`
    - 检查端口11434是否被占用
    - 验证模型是否已下载：`ollama list`
    - 确认使用的模型名称正确：
      - 聊天模型：`deepseek-v3.1:671b-cloud`
      - 嵌入模型：`nomic-embed-text:latest`

2. **分类结果不准确**
   - 对于AiServices分类：优化提示词（`@UserMessage`）的描述，提供更清晰的分类规则
   - 对于EmbeddingModel分类：增加更多高质量的示例数据，确保示例覆盖各种表达方式
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

5. **嵌入模型加载失败**
    - 确保已下载嵌入模型：`ollama pull nomic-embed-text:latest`
    - 检查嵌入模型名称配置是否正确
    - 验证Ollama服务是否支持嵌入模型API

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

### 文本分类功能说明

项目演示了如何使用 LangChain4j 进行文本分类：

1. **AiServices分类方式**: 使用大语言模型进行智能分类
   - 定义分类枚举类型和分类接口
   - 使用 `@UserMessage` 指定提示词模板
   - 基于大语言模型的语义理解能力
   - 适合复杂语义理解和少量类别分类
   - 返回强类型的枚举值

2. **EmbeddingModelTextClassifier方式**: 使用嵌入模型进行基于相似度的分类
   - 为每个类别准备示例数据（few-shot learning）
   - 基于文本嵌入向量的相似度计算
   - 性能更好，适合大规模分类场景
   - 通过示例数据学习分类模式
   - 返回分类结果列表（按相似度排序）

3. **应用场景**:
   - 客服问题自动分类
   - 邮件分类和路由
   - 内容审核和分类
   - 情感分析
   - 意图识别
   - 任何需要将文本分类到预定义类别的场景

4. **优势**:
   - 类型安全：使用Java枚举类型保证分类正确性
   - 易于维护：接口定义清晰，便于扩展
   - 灵活配置：可以自定义提示词和示例数据
   - 两种方式互补：根据场景选择最适合的分类方式

### 技术架构

- **Spring Boot**: 提供Web服务和依赖注入
- **LangChain4j**: 提供AI集成能力
  - `AiServices`: 类型安全的AI服务构建器，用于大语言模型分类
  - `EmbeddingModelTextClassifier`: 基于嵌入模型的分类器
  - `OllamaChatModel`: 聊天模型接口
  - `OllamaEmbeddingModel`: 嵌入模型接口
- **Ollama**: 提供本地大语言模型和嵌入模型服务
- **Java Enum**: 用于定义分类类别

---

**注意**: 请确保在使用前已正确安装和配置Ollama服务，并下载所需的模型。
