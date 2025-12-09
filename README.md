# langchain4j Study - Chain链式调用示例

这是一个用于学习langchain4j的Spring Boot项目，集成了本地Ollama服务，演示了如何使用LangChain4j的Chain链式调用功能。项目提供了两种Chain使用方式：对话链（ConversationalChain）和检索增强生成链（ConversationalRetrievalChain）。

**Package**: `com.cnblogs.yjmyzz.langchain4j.study`

## 🚀 项目特性

- **Java 25**: 使用最新的Java版本
- **Spring Boot 4.0.0**: 现代化的Spring Boot框架
- **LangChain4j 1.8.0**: 强大的Java AI框架
- **Ollama集成**: 支持本地大语言模型和嵌入模型
  - 聊天模型：默认使用 `deepseek-v3.1:671b-cloud`
  - 嵌入模型：默认使用 `nomic-embed-text:latest`
- **Chain链式调用**: 演示LangChain4j的链式调用功能
- **两种Chain方式**:
  - **ConversationalChain**: 带记忆的对话链，支持多轮对话
  - **ConversationalRetrievalChain**: RAG检索增强生成链，基于文档知识库回答问题
- **文档处理**: 支持文档加载、分割和嵌入存储
- **RESTful API**: 提供Chain功能演示API接口

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

#### Chain链式调用功能演示

项目提供了两种Chain链式调用方式，用于演示LangChain4j的链式调用功能。

##### 1. ConversationalChain - 对话链

```bash
# 使用对话链进行多轮对话
curl "http://localhost:8080/api/chat/chain?query=你好，我是张三"
```

**功能说明**：
- 使用 `ConversationalChain` 创建带记忆的对话链
- 支持多轮对话，自动维护对话上下文
- 使用 `MessageWindowChatMemory` 管理对话记忆（最多保留10条消息）
- 适合需要上下文理解的对话场景

**返回示例**：
```json
"你好，张三！很高兴认识你。有什么我可以帮助你的吗？"
```

**多轮对话示例**：
```bash
# 第一轮对话
curl "http://localhost:8080/api/chat/chain?query=我的名字是李四"
# 返回：了解，李四，很高兴认识你...

# 第二轮对话（会记住之前的对话）
curl "http://localhost:8080/api/chat/chain?query=我刚才说我的名字是什么？"
# 返回：你刚才说你的名字是李四...
```

##### 2. ConversationalRetrievalChain - RAG检索增强生成链

```bash
# 使用RAG链基于知识库回答问题
curl "http://localhost:8080/api/rag/chain?query=萧寒星是谁？"
```

**功能说明**：
- 使用 `ConversationalRetrievalChain` 创建RAG检索增强生成链
- 基于文档知识库（`data.txt`）进行检索和回答
- 自动加载文档、分割文本、生成嵌入向量并存储
- 使用 `EmbeddingStoreContentRetriever` 检索相关内容
- 结合检索到的内容和对话历史生成回答
- 适合基于知识库的问答场景

**返回示例**：
```json
"萧寒星，号'孤影剑客'，是一位江湖传奇人物。他幼时家族蒙难，唯他幸免，在荒废古墓中偶得前朝遗卷《星陨诀》，自此以残剑独修。他的成名绝技是「寂夜星河」，剑势如流星破空..."
```

**知识库问答示例**：
```bash
# 询问知识库中的内容
curl "http://localhost:8080/api/rag/chain?query=萧寒星的绝技是什么？"
# 返回：萧寒星的成名绝技是「寂夜星河」...

curl "http://localhost:8080/api/rag/chain?query=萧寒星做过什么大事？"
# 返回：他曾为救被邪派掳走的医谷圣女苏挽晴，单剑独闯「幽冥教」总坛...
```

**两种Chain对比**：

| 特性 | ConversationalChain | ConversationalRetrievalChain |
|------|-------------------|----------------------------|
| 功能 | 多轮对话 | RAG检索增强生成 |
| 记忆 | 支持对话记忆 | 支持对话记忆 + 知识库检索 |
| 数据源 | 仅依赖模型知识 | 基于文档知识库 |
| 适用场景 | 通用对话、聊天 | 知识问答、文档检索 |
| 性能 | 较快 | 较慢（需要检索和嵌入计算） |

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
│   │       └── ChainController.java           # Chain链式调用功能控制器
│   └── resources/
│       ├── application.yml                     # 应用配置
│       └── data.txt                           # RAG知识库文档
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
  - `ollamaChatModel` - 聊天模型（用于ConversationalChain和ConversationalRetrievalChain）
  - `ollamaEmbeddingModel` - 嵌入模型（用于文档嵌入和RAG检索）

### 2. 控制器

#### ChainController.java
- 提供Chain链式调用功能演示
- 演示两种Chain使用方式：
  - **ConversationalChain**: 带记忆的对话链，支持多轮对话
  - **ConversationalRetrievalChain**: RAG检索增强生成链，基于文档知识库回答问题
- 提供两个API接口：
  - `/api/chat/chain` - 使用ConversationalChain进行对话
  - `/api/rag/chain` - 使用ConversationalRetrievalChain进行RAG问答
- 文档处理功能：
  - 使用 `FileSystemDocumentLoader` 加载文档
  - 使用 `DocumentByLineSplitter` 按行分割文档
  - 使用 `InMemoryEmbeddingStore` 存储嵌入向量
  - 自动为文档生成嵌入向量并建立索引
- 记忆管理：
  - 使用 `MessageWindowChatMemory` 管理对话记忆
  - 最多保留10条消息历史

### 3. 知识库文档

#### data.txt
- RAG知识库文档，包含示例内容
- 用于演示文档加载、分割和检索功能
- 可以替换为任何文本文件作为知识库

### 4. 主要依赖
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

### 添加新的Chain功能

#### 方式1：使用ConversationalChain（推荐用于多轮对话）

1. 注入 `OllamaChatModel`
2. 创建 `MessageWindowChatMemory` 管理对话记忆
3. 使用 `ConversationalChain.builder()` 构建链
4. 调用 `execute()` 方法执行对话

**示例**：
```java
@Autowired
@Qualifier("ollamaChatModel")
OllamaChatModel ollamaChatModel;

@GetMapping("/chat")
public ResponseEntity<String> chat(@RequestParam String query) {
    try {
        String response = ConversationalChain.builder()
                .chatModel(ollamaChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build()
                .execute(query);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.ok("错误: " + e.getMessage());
    }
}
```

#### 方式2：使用ConversationalRetrievalChain（推荐用于RAG问答）

1. 准备文档和嵌入存储
2. 创建 `EmbeddingStoreContentRetriever`
3. 使用 `ConversationalRetrievalChain.builder()` 构建RAG链
4. 调用 `execute()` 方法执行问答

**示例**：
```java
@Autowired
@Qualifier("ollamaChatModel")
OllamaChatModel ollamaChatModel;

@Autowired
@Qualifier("ollamaEmbeddingModel")
OllamaEmbeddingModel ollamaEmbeddingModel;

// 创建嵌入存储
EmbeddingStore<TextSegment> createEmbeddingStore(String filePath) {
    EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
    Document document = FileSystemDocumentLoader.loadDocument(filePath);
    DocumentByLineSplitter splitter = new DocumentByLineSplitter(100, 0);
    List<TextSegment> segments = splitter.split(document);
    for (TextSegment segment : segments) {
        Embedding embedding = ollamaEmbeddingModel.embed(segment).content();
        embeddingStore.add(embedding, segment);
    }
    return embeddingStore;
}

@GetMapping("/rag")
public ResponseEntity<String> rag(@RequestParam String query) {
    try {
        EmbeddingStore<TextSegment> embeddingStore = createEmbeddingStore("path/to/document.txt");
        String answer = ConversationalRetrievalChain.builder()
                .chatModel(ollamaChatModel)
                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                        .embeddingModel(ollamaEmbeddingModel)
                        .embeddingStore(embeddingStore)
                        .maxResults(3)
                        .build())
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build()
                .execute(query);
        return ResponseEntity.ok(answer);
    } catch (Exception e) {
        return ResponseEntity.ok("错误: " + e.getMessage());
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

2. **RAG检索结果不准确**
   - 检查知识库文档（`data.txt`）的内容是否完整
   - 尝试调整文档分割策略（修改 `DocumentByLineSplitter` 的参数）
   - 增加 `maxResults` 参数以检索更多相关内容
   - 优化文档内容，确保信息清晰明确

3. **对话记忆丢失**
   - 检查 `MessageWindowChatMemory` 的配置
   - 确保每次请求使用同一个Chain实例（或共享记忆）
   - 注意：当前实现每次请求都创建新的Chain，记忆不会跨请求保持

4. **模型响应缓慢**
    - 检查硬件资源（CPU、内存）
    - 考虑使用更小的模型
    - 调整超时配置（`ollama.timeout`）
    - 对于本地模型，考虑使用GPU加速
    - RAG链需要额外的检索和嵌入计算，响应时间会更长

5. **内存不足**
    - 增加JVM堆内存：`-Xmx4g`
    - 使用更小的模型
    - 减少文档大小或分割粒度
    - 考虑使用持久化嵌入存储替代内存存储

6. **文档加载失败**
    - 确保 `data.txt` 文件存在于 `src/main/resources/` 目录
    - 检查文件路径和权限
    - 验证文件编码为UTF-8

7. **嵌入模型加载失败**
    - 确保已下载嵌入模型：`ollama pull nomic-embed-text:latest`
    - 检查嵌入模型名称配置是否正确
    - 验证Ollama服务是否支持嵌入模型API

8. **Java 25 兼容性**
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

### Chain链式调用功能说明

项目演示了如何使用 LangChain4j 的Chain链式调用功能：

1. **ConversationalChain**: 带记忆的对话链
   - 使用 `MessageWindowChatMemory` 管理对话记忆
   - 支持多轮对话，自动维护上下文
   - 适合需要上下文理解的对话场景
   - 简单易用，性能较好

2. **ConversationalRetrievalChain**: RAG检索增强生成链
   - 基于文档知识库进行检索和回答
   - 使用 `EmbeddingStoreContentRetriever` 检索相关内容
   - 结合检索内容和对话历史生成回答
   - 适合基于知识库的问答场景
   - 需要文档加载、分割和嵌入计算

3. **文档处理流程**:
   - 使用 `FileSystemDocumentLoader` 加载文档
   - 使用 `DocumentByLineSplitter` 分割文档为文本段
   - 使用 `OllamaEmbeddingModel` 生成嵌入向量
   - 使用 `InMemoryEmbeddingStore` 存储嵌入向量和文本段
   - 检索时计算查询与文档的相似度，返回最相关的内容

4. **应用场景**:
   - 智能客服对话系统
   - 知识库问答系统
   - 文档检索和问答
   - 多轮对话应用
   - 企业知识管理
   - 教育培训问答

5. **优势**:
   - 链式调用：简化AI应用开发流程
   - 记忆管理：自动维护对话上下文
   - RAG能力：结合知识库提供准确回答
   - 易于扩展：可以添加更多Chain组件
   - 灵活配置：支持自定义记忆、检索器等组件

### 技术架构

- **Spring Boot**: 提供Web服务和依赖注入
- **LangChain4j**: 提供AI集成能力
  - `ConversationalChain`: 对话链，支持多轮对话
  - `ConversationalRetrievalChain`: RAG检索增强生成链
  - `MessageWindowChatMemory`: 对话记忆管理
  - `EmbeddingStoreContentRetriever`: 嵌入存储内容检索器
  - `OllamaChatModel`: 聊天模型接口
  - `OllamaEmbeddingModel`: 嵌入模型接口
  - `FileSystemDocumentLoader`: 文件系统文档加载器
  - `DocumentByLineSplitter`: 按行分割文档
  - `InMemoryEmbeddingStore`: 内存嵌入存储
- **Ollama**: 提供本地大语言模型和嵌入模型服务

---

**注意**: 
- 请确保在使用前已正确安装和配置Ollama服务，并下载所需的模型
- RAG功能需要知识库文档（`data.txt`），可以替换为任何文本文件
- 当前实现每次请求都创建新的Chain实例，对话记忆不会跨请求保持（如需跨请求记忆，需要实现共享记忆机制）
