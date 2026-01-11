# langchain4j Study - AiServices 完整示例

这是一个用于学习langchain4j的Spring Boot项目，集成了本地Ollama服务，演示了如何使用LangChain4j的AiServices功能。项目从Chain链式调用迁移到AiServices，展示了多种AiServices使用场景，包括基本用法、结构化返回、流式返回和监听器机制。

**Package**: `com.cnblogs.yjmyzz.langchain4j.study`

## 🚀 项目特性

- **Java 25**: 使用最新的Java版本
- **Spring Boot 4.0.0**: 现代化的Spring Boot框架
- **LangChain4j 1.8.0**: 强大的Java AI框架
- **Ollama集成**: 支持本地大语言模型和嵌入模型
  - 聊天模型：默认使用 `deepseek-v3.1:671b-cloud`
  - 嵌入模型：默认使用 `nomic-embed-text:latest`
- **AiServices**: 使用接口定义AI服务，简化AI应用开发
  - **基本用法**: 通过接口定义AI服务，自动生成实现
  - **结构化返回**: 支持返回结构化对象（POJO）
  - **流式返回**: 支持流式响应，实时返回AI生成内容
  - **监听器机制**: 提供完整的监听器支持，追踪AI服务执行过程
    - **AiService监听器**: 监听AI服务的开始和完成事件
    - **ChatModel监听器**: 监听聊天模型的请求、响应和错误事件
- **对话记忆**: 使用 `MessageWindowChatMemory` 管理对话上下文
- **RESTful API**: 提供4个API接口，演示不同的AiServices使用场景

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

项目提供了4个API接口，演示不同的AiServices使用场景：

#### 1. 基本AiService用法

```bash
# 基本AiService对话接口
curl "http://localhost:8080/api/aiservice/1?query=请问李清照最广为流传的词是哪一首,请给出这首词全文？"
```

**功能说明**：
- 使用 `AiServices.builder()` 创建AI服务接口实例
- 定义 `ChineseTeacher` 接口，使用 `@SystemMessage` 和 `@UserMessage` 注解
- 支持多轮对话，使用 `MessageWindowChatMemory` 管理对话记忆（最多保留10条消息）
- 返回类型：`String`（JSON格式）

**返回示例**：
```json
"李清照最广为流传的词是《声声慢·寻寻觅觅》..."
```

#### 2. AiService + 结构化返回

```bash
# 结构化返回接口（返回Poem对象）
curl "http://localhost:8080/api/aiservice/2?query=请问李清照最广为流传的词是哪一首,请给出这首词全文（以json格式输出，类似{\"author\":\"...\",\"title\":\"...\",\"content\":\"...\"}）？"
```

**功能说明**：
- 演示如何返回结构化对象（POJO）
- 使用 `PoemExtractor` 接口提取结构化数据
- 使用 `@Description` 注解描述字段，帮助AI理解返回结构
- 返回类型：`Poem` 对象（包含title、author、content字段）

**返回示例**：
```json
{
  "title": "声声慢·寻寻觅觅",
  "author": "李清照",
  "content": "寻寻觅觅，冷冷清清，凄凄惨惨戚戚..."
}
```

#### 3. AiService + 流式返回

```bash
# 流式返回接口（实时返回AI生成内容）
curl "http://localhost:8080/api/aiservice/3?query=请问李清照最广为流传的词是哪一首,请给出这首词全文？"
```

**功能说明**：
- 使用 `StreamingChatModel` 实现流式响应
- 定义 `ChineseStreamTeacher` 接口，返回类型为 `TokenStream`
- 使用 `Flux` 实现响应式流式输出
- 适合需要实时显示AI生成内容的场景
- 返回类型：`text/html`（流式HTML内容）

**使用场景**：
- 实时聊天界面
- 长文本生成场景
- 提升用户体验

#### 4. AiService + 自定义监听器

```bash
# 带监听器追踪的AiService接口
curl "http://localhost:8080/api/aiservice/4?query=请问李清照最广为流传的词是哪一首,请给出这首词全文？"
```

**功能说明**：
- 注册自定义监听器追踪AI服务执行过程
- `CustomAiServiceStartedListener`: 监听AI服务开始事件
- `CustomAiServiceCompletedListener`: 监听AI服务完成事件
- 适合需要追踪和监控AI服务执行的场景
- 返回类型：`String`（JSON格式）

**监听器输出示例**：
```
AiServiceStartedEvent: invocationId=xxx, aiServiceInterfaceName=ChineseTeacher, aiServiceMethodName=chat, ...
AiServiceCompletedListener: invocationId=xxx, result=李清照最广为流传的词是...
```

### 监听器功能说明

项目提供了三种类型的监听器：

**AiService监听器**：
- `CustomAiServiceStartedListener`: 监听AI服务开始执行
  - 获取调用上下文信息（invocationId、接口名、方法名等）
  - 获取系统消息和用户消息
  - 记录事件时间戳
- `CustomAiServiceCompletedListener`: 监听AI服务执行完成
  - 获取调用上下文信息
  - 获取执行结果
  - 记录完成时间

**ChatModel监听器**：
- `CustomChatModelListener`: 监听聊天模型的请求、响应和错误
  - `onRequest`: 监听请求发送，可以查看消息列表和参数
  - `onResponse`: 监听响应接收，可以查看AI消息和Token使用情况
  - `onError`: 监听错误发生，可以查看错误详情

**监听器配置**：
- ChatModel监听器在 `OllamaConfig` 中配置（当前已注释），应用于所有聊天模型调用
- AiService监听器在创建AiService时注册，仅应用于该服务实例

### 多轮对话示例

所有支持记忆的接口（如 `/api/aiservice/1` 和 `/api/aiservice/4`）都支持多轮对话：

```bash
# 第一轮对话
curl "http://localhost:8080/api/aiservice/1?query=我的名字是李四"
# 返回：了解，李四，很高兴认识你...

# 第二轮对话（会记住之前的对话）
curl "http://localhost:8080/api/aiservice/1?query=我刚才说我的名字是什么？"
# 返回：你刚才说你的名字是李四...
```

**注意**：当前实现每次请求都创建新的AiService实例，记忆不会跨请求保持。如需跨请求记忆，需要实现共享记忆机制或使用单例AiService。

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
      com.cnblogs.yjmyzz.langchain4j.study: DEBUG
      dev.langchain4j: DEBUG
    pattern:
      console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"

# Ollama配置
ollama:
  base-url: http://localhost:11434          # Ollama服务地址
  model: deepseek-v3.1:671b-cloud           # 聊天模型名称（用于AiServices）
  embedding-model: nomic-embed-text:latest  # 嵌入模型名称（用于EmbeddingModel）
  timeout: 60                               # 请求超时时间（秒）

# 应用信息
info:
  app:
    name: langchain4j Study
    version: 1.0.0
    description: langchain4j学习项目 - 跟踪Trace示例
```

## 📁 项目结构

```
src/
├── main/
│   ├── java/com/cnblogs/yjmyzz/langchain4j/study/
│   │   ├── LongChain4jStudyApplication.java    # 主启动类
│   │   ├── config/
│   │   │   └── OllamaConfig.java              # Ollama配置类（包含聊天模型、嵌入模型和流式模型）
│   │   ├── controller/
│   │   │   └── TraceController.java           # AI服务功能演示控制器（4个API接口）
│   │   ├── service/
│   │   │   ├── ChineseTeacher.java            # 基本AI服务接口（中文老师）
│   │   │   ├── ChineseStreamTeacher.java      # 流式AI服务接口（中文老师）
│   │   │   ├── Poem.java                      # 结构化返回数据类（诗歌）
│   │   │   └── PoemExtractor.java             # 结构化提取AI服务接口
│   │   └── listener/
│   │       ├── CustomAiServiceStartedListener.java    # AI服务开始监听器
│   │       ├── CustomAiServiceCompletedListener.java # AI服务完成监听器
│   │       └── CustomChatModelListener.java          # 聊天模型监听器
│   └── resources/
│       ├── application.yml                     # 应用配置
│       └── data.txt                           # 示例文档（可选）
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
- 配置Ollama聊天模型、嵌入模型和流式聊天模型
- 支持自定义模型名称、服务地址和超时时间
- 启用请求和响应日志记录
- 使用 `@Bean` 注解注册为Spring Bean，支持依赖注入
- Bean名称：
  - `ollamaChatModel` - 普通聊天模型（用于基本AiService）
  - `streamingChatModel` - 流式聊天模型（用于流式AiService）
  - `ollamaEmbeddingModel` - 嵌入模型（用于文档嵌入和RAG检索）

### 2. 控制器

#### TraceController.java
- 提供4个AiServices功能演示API接口
- 演示不同的AiServices使用场景：
  - 基本用法、结构化返回、流式返回、监听器追踪
- 提供API接口：
  - `/api/aiservice/1` - 基本AiService用法
  - `/api/aiservice/2` - AiService + 结构化返回
  - `/api/aiservice/3` - AiService + 流式返回
  - `/api/aiservice/4` - AiService + 自定义监听器
- 记忆管理：
  - 使用 `MessageWindowChatMemory` 管理对话记忆
  - 最多保留10条消息历史
- 流式支持：
  - 使用 `StreamingChatModel` 和 `Flux` 实现流式响应
  - 支持实时返回AI生成内容

### 3. 监听器

#### CustomAiServiceStartedListener.java
- 实现 `AiServiceStartedListener` 接口
- 监听AI服务开始执行事件
- 记录调用上下文信息：invocationId、接口名、方法名、参数、时间戳等
- 记录系统消息和用户消息

#### CustomAiServiceCompletedListener.java
- 实现 `AiServiceCompletedListener` 接口
- 监听AI服务执行完成事件
- 记录调用上下文信息和执行结果
- 用于追踪AI服务的执行结果

#### CustomChatModelListener.java
- 实现 `ChatModelListener` 接口
- 监听聊天模型的请求、响应和错误事件
- `onRequest`: 记录请求消息和参数
- `onResponse`: 记录响应消息和Token使用情况
- `onError`: 记录错误信息
- 支持在请求上下文中设置自定义属性

### 4. Service接口

#### ChineseTeacher.java
- 基本AI服务接口定义
- 使用 `@SystemMessage` 定义系统提示词（"你是一名小学语文老师"）
- 使用 `@UserMessage` 定义用户消息模板（支持 `{{it}}` 占位符）
- 返回类型：`String`

#### ChineseStreamTeacher.java
- 流式AI服务接口定义
- 返回类型：`TokenStream`（用于流式响应）
- 适用于需要实时返回AI生成内容的场景

#### Poem.java
- 结构化返回数据类
- 使用 `@Description` 注解描述字段，帮助AI理解返回结构
- 包含字段：`title`（标题）、`author`（作者）、`content`（内容）
- 使用Lombok简化代码（`@Data`、`@AllArgsConstructor`、`@NoArgsConstructor`）

#### PoemExtractor.java
- 结构化提取AI服务接口
- 使用 `@V("query")` 注解指定参数在模板中的变量名
- 返回类型：`Poem`（结构化对象）

### 5. 主要依赖
- **Spring Boot Web**: Web应用支持
- **Spring Boot Validation**: 数据验证支持
- **Spring WebFlux**: 响应式编程支持（用于流式返回）
- **LangChain4j**: AI框架核心（版本 1.8.0）
- **LangChain4j Ollama**: Ollama集成（包含聊天模型、流式聊天模型和嵌入模型支持）
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

### 添加新的AiService功能

#### 使用AiServices创建AI服务（推荐方式）

1. 定义AI服务接口，使用 `@SystemMessage` 和 `@UserMessage` 注解
2. 注入 `OllamaChatModel` 或 `StreamingChatModel`
3. 使用 `AiServices.builder()` 创建服务实例
4. 配置聊天模型、记忆管理和监听器
5. 调用接口方法执行对话

**基本用法示例**：
```java
@Autowired
@Qualifier("ollamaChatModel")
OllamaChatModel ollamaChatModel;

// 定义AI服务接口
interface MyAiService {
    @SystemMessage("你是一个专业的助手")
    @UserMessage("请回答：{{it}}")
    String chat(String query);
}

@GetMapping("/chat")
public ResponseEntity<String> chat(@RequestParam String query) {
    try {
        // 创建AI服务实例
        MyAiService service = AiServices.builder(MyAiService.class)
                .chatModel(ollamaChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        
        // 调用服务方法
        String response = service.chat(query);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        return ResponseEntity.ok("错误: " + e.getMessage());
    }
}
```

**结构化返回示例**：
```java
// 定义数据类
@Data
@AllArgsConstructor
@NoArgsConstructor
class MyData {
    @Description("字段描述")
    private String field1;
    @Description("字段描述")
    private String field2;
}

// 定义提取接口
interface MyExtractor {
    @UserMessage("请从以下内容中提取：{{query}}")
    MyData extract(@V("query") String query);
}

// 使用
MyExtractor extractor = AiServices.builder(MyExtractor.class)
        .chatModel(ollamaChatModel)
        .build();
MyData data = extractor.extract("内容...");
```

**流式返回示例**：
```java
@Autowired
@Qualifier("streamingChatModel")
StreamingChatModel streamingChatModel;

interface MyStreamService {
    @SystemMessage("你是一个专业的助手")
    @UserMessage("请回答：{{it}}")
    TokenStream chat(String query);
}

@GetMapping(value = "/stream", produces = "text/html;charset=utf-8")
public Flux<String> stream(@RequestParam String query) {
    MyStreamService service = AiServices.builder(MyStreamService.class)
            .streamingChatModel(streamingChatModel)
            .build();
    
    Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
    service.chat(query)
            .onPartialResponse((String s) -> sink.tryEmitNext(s))
            .onCompleteResponse((ChatResponse response) -> sink.tryEmitComplete())
            .onError(sink::tryEmitError)
            .start();
    return sink.asFlux();
}
```

### 添加自定义监听器

#### 创建AiService监听器

**示例**：
```java
public class MyAiServiceStartedListener implements AiServiceStartedListener {
    @Override
    public void onEvent(AiServiceStartedEvent event) {
        InvocationContext context = event.invocationContext();
        UUID invocationId = context.invocationId();
        String methodName = context.methodName();
        // 处理事件...
    }
}
```

#### 创建ChatModel监听器

**示例**：
```java
public class MyChatModelListener implements ChatModelListener {
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        ChatRequest request = requestContext.chatRequest();
        // 处理请求...
    }
    
    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        ChatResponse response = responseContext.chatResponse();
        TokenUsage tokenUsage = response.metadata().tokenUsage();
        // 处理响应...
    }
    
    @Override
    public void onError(ChatModelErrorContext errorContext) {
        Throwable error = errorContext.error();
        // 处理错误...
    }
}
```

#### 注册监听器

**ChatModel监听器**（在配置类中注册）：
```java
@Bean("ollamaChatModel")
public ChatModel chatModel() {
    return OllamaChatModel.builder()
            .baseUrl(ollamaBaseUrl)
            .modelName(ollamaModel)
            .listeners(List.of(new CustomChatModelListener()))
            .build();
}
```

**AiService监听器**（在创建服务时注册）：
```java
AiServices.builder(MyAiService.class)
        .chatModel(ollamaChatModel)
        .registerListeners(List.of(
            new CustomAiServiceStartedListener(),
            new CustomAiServiceCompletedListener()
        ))
        .build();
```

### 自定义配置

可以通过修改 `application.yml` 来调整：
- Ollama服务配置
    - 服务地址（`ollama.base-url`，默认：http://localhost:11434）
    - 聊天模型（`ollama.model`，默认：deepseek-v3.1:671b-cloud）
    - 嵌入模型（`ollama.embedding-model`，默认：nomic-embed-text:latest）
    - 超时时间（`ollama.timeout`，单位：秒，默认：60）
- 日志级别和格式
- 服务器端口（默认8080）

**注意**:
- 日志配置中的package路径为 `com.example.langchain4jstudy`（可修改为 `com.cnblogs.yjmyzz.langchain4j.study`）
- 修改配置后需要重启应用才能生效
- 确保使用的模型已在Ollama中下载：
  - 聊天模型：`ollama pull deepseek-v3.1:671b-cloud`
  - 嵌入模型：`ollama pull nomic-embed-text:latest`
- 流式模型和普通聊天模型使用相同的模型配置

## 🐛 故障排除

### 常见问题

1. **Ollama连接失败**
    - 确保Ollama服务已启动：`ollama serve`
    - 检查端口11434是否被占用
    - 验证模型是否已下载：`ollama list`
    - 确认使用的模型名称正确：
      - 聊天模型：`deepseek-v3.1:671b-cloud`
      - 嵌入模型：`nomic-embed-text:latest`

2. **对话记忆丢失**
   - 检查 `MessageWindowChatMemory` 的配置
   - 确保每次请求使用同一个AiService实例（或共享记忆）
   - 注意：当前实现每次请求都创建新的AiService实例，记忆不会跨请求保持
   - 如需跨请求记忆，需要实现共享记忆机制或使用单例AiService
   - 流式接口（`/api/aiservice/3`）不支持记忆管理

3. **监听器未触发**
   - 检查监听器是否正确注册
   - 验证监听器实现是否正确实现了对应的接口
   - 确认监听器在正确的时机注册（ChatModel监听器在配置类中，AiService监听器在创建服务时）

4. **模型响应缓慢**
    - 检查硬件资源（CPU、内存）
    - 考虑使用更小的模型
    - 调整超时配置（`ollama.timeout`）
    - 对于本地模型，考虑使用GPU加速

5. **监听器输出过多**
    - 监听器会输出详细的调试信息
    - 可以在生产环境中移除或禁用监听器
    - 或者修改监听器实现，使用日志框架而不是System.out

6. **AiService接口定义错误**
    - 确保接口方法使用 `@UserMessage` 或 `@SystemMessage` 注解
    - 检查方法参数是否正确
    - 验证方法返回类型是否支持（String、TokenStream、POJO等）
    - 流式接口必须使用 `StreamingChatModel` 并返回 `TokenStream`
    - 结构化返回需要使用 `@Description` 注解描述字段

7. **流式返回问题**
    - 确保使用 `StreamingChatModel` 而不是普通的 `ChatModel`
    - 流式接口返回类型必须是 `TokenStream`
    - 使用 `Flux` 处理响应式流式输出
    - 检查浏览器是否支持Server-Sent Events（SSE）

8. **结构化返回问题**
    - 确保数据类使用 `@Description` 注解描述每个字段
    - 使用 `@V("变量名")` 注解指定参数在模板中的变量名
    - 确保AI返回的JSON格式与数据类结构匹配
    - 可以在提示词中明确要求JSON格式输出

9. **Java 25 兼容性**
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

### AiServices 功能说明

项目演示了如何使用 LangChain4j 的AiServices功能，从Chain链式调用迁移到AiServices：

1. **AiServices**: 通过接口定义AI服务
   - 使用 `@SystemMessage` 定义系统提示词
   - 使用 `@UserMessage` 定义用户消息模板（支持 `{{it}}` 或 `{{变量名}}`）
   - 使用 `@V("变量名")` 注解指定参数在模板中的变量名
   - 自动生成AI服务实现，简化开发流程
   - 支持类型安全的接口调用

2. **多种返回类型支持**:
   - **String**: 基本文本返回
   - **TokenStream**: 流式返回，实时返回AI生成内容
   - **POJO**: 结构化返回，自动解析JSON为Java对象
     - 使用 `@Description` 注解描述字段，帮助AI理解返回结构

3. **监听器机制**: 追踪AI服务执行过程
   - **AiService监听器**: 监听服务开始和完成事件
     - `AiServiceStartedListener`: 监听服务开始，获取调用上下文
     - `AiServiceCompletedListener`: 监听服务完成，获取执行结果
   - **ChatModel监听器**: 监听模型请求、响应和错误
     - `onRequest`: 监听请求发送，可查看消息和参数
     - `onResponse`: 监听响应接收，可查看Token使用情况
     - `onError`: 监听错误发生，可查看错误详情

4. **对话记忆管理**:
   - 使用 `MessageWindowChatMemory` 管理对话上下文
   - 自动维护多轮对话历史
   - 可配置记忆窗口大小（默认10条消息）

5. **流式响应支持**:
   - 使用 `StreamingChatModel` 实现流式响应
   - 使用 `TokenStream` 作为返回类型
   - 使用 `Flux` 实现响应式流式输出
   - 适合实时聊天界面和长文本生成场景

6. **应用场景**:
   - 智能客服对话系统
   - 多轮对话应用
   - 实时聊天界面
   - 结构化数据提取
   - AI服务监控和追踪
   - 性能分析和调试
   - 企业AI应用开发

7. **优势**:
   - 接口定义：通过接口定义AI服务，类型安全
   - 简化开发：无需手动处理消息构建和模型调用
   - 多种返回：支持文本、流式和结构化返回
   - 监听追踪：完整的监听器机制，便于监控和调试
   - 灵活配置：支持自定义记忆、监听器等组件
   - 易于测试：接口定义便于单元测试

### 技术架构

- **Spring Boot**: 提供Web服务和依赖注入
- **LangChain4j**: 提供AI集成能力
  - `AiServices`: AI服务接口生成器，通过接口定义AI服务
  - `@SystemMessage`: 系统消息注解，定义系统提示词
  - `@UserMessage`: 用户消息注解，定义用户消息模板
  - `@V`: 参数变量名注解，指定参数在模板中的变量名
  - `@Description`: 字段描述注解，用于结构化返回
  - `MessageWindowChatMemory`: 对话记忆管理
  - `TokenStream`: 流式返回类型
  - `OllamaChatModel`: 普通聊天模型接口
  - `OllamaStreamingChatModel`: 流式聊天模型接口
  - `OllamaEmbeddingModel`: 嵌入模型接口
  - `AiServiceStartedListener`: AI服务开始监听器接口
  - `AiServiceCompletedListener`: AI服务完成监听器接口
  - `ChatModelListener`: 聊天模型监听器接口
- **Spring WebFlux**: 提供响应式编程支持，用于流式返回
- **Ollama**: 提供本地大语言模型和嵌入模型服务

---

**注意**: 
- 请确保在使用前已正确安装和配置Ollama服务，并下载所需的模型
- 当前实现每次请求都创建新的AiService实例，对话记忆不会跨请求保持（如需跨请求记忆，需要实现共享记忆机制或使用单例AiService）
- 流式接口（`/api/aiservice/3`）不支持记忆管理
- 监听器会输出详细的调试信息，生产环境建议使用日志框架替代System.out
- 结构化返回需要AI模型支持JSON格式输出，建议在提示词中明确要求JSON格式
- 流式返回使用Server-Sent Events（SSE），确保浏览器支持或使用专门的SSE客户端
