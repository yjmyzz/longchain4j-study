# langchain4j-study（day10）- Skills + AiServices 订单处理示例

这是一个用于学习 **LangChain4j Skills** 的 Spring Boot 项目，集成本地 **Ollama**，演示如何用 `AiServices` + `ToolProvider` + `Skills` 做一个“**订单处理助手**”：当用户说“处理订单 ORD001”时，模型会先 `activate_skill` 激活 `process-order`，再按技能文档调用 `OrderTools` 中的工具方法完成流程。

**Package**：`com.cnblogs.yjmyzz.langchain4j.study`  
**入口类**：`LongChain4jStudyApplication`

## 项目特性

- **Java**：25（以 `pom.xml` 为准）
- **Spring Boot**：4.0.0
- **LangChain4j（核心）**：1.13.0
- **LangChain4j-Ollama**：1.13.1
- **LangChain4j-Skills**：`${langchain4j.version}`（当前为 `1.13.1-beta23`）
- **技能驱动**：
  - 技能文件：`src/main/resources/skills/process-order/SKILL.md`
  - 技能工具：`src/main/java/.../tools/OrderTools.java`
  - 工具仅在技能激活后对模型可见（技能作用域工具）
- **对话记忆**：`MessageWindowChatMemory.withMaxMessages(100)`

## 前置要求

1. **JDK 25**
2. **Maven 3.6+**
3. **Ollama** 已安装并启动（默认 `http://localhost:11434`）

## 快速开始

### 1) 启动 Ollama

```bash
ollama serve
```

### 2) 拉取模型（示例）

```bash
ollama pull deepseek-v3.1:671b-cloud
ollama pull nomic-embed-text:latest
```

### 3) 运行项目

```bash
mvn spring-boot:run
```

## API 使用

### 订单处理对话接口

- **URL**：`GET /chat/order`
- **示例**：

```bash
curl "http://localhost:8080/chat/order?message=处理订单%20ORD001"
```

你也可以测试闲聊：

```bash
curl "http://localhost:8080/chat/order?message=你叫什么名字？"
```

## 示例用例

### Case 1：处理订单 `ORD001`（成功）

请求：

```bash
curl "http://localhost:8080/chat/order?message=处理订单%20ORD001"
```

工具调用（控制台输出节选）：

```
用户消息：处理订单 ORD001
工具：正在验证订单：ORD001
订单 ORD001 有效。
工具：正在为订单预留库存：ORD001
订单 ORD001 的库存已预留。
工具：正在为订单扣款：ORD001
工具：正在为订单发送确认邮件：ORD001
```

AI 回复示例：

```
订单 ORD001 已成功处理完成！处理过程包括：

1. ✅ 订单验证 - 订单有效
2. ✅ 库存预留 - 库存已预留
3. ✅ 支付扣款 - 扣款成功
4. ✅ 确认邮件 - 邮件已发送

订单处理流程全部顺利完成。客户将收到订单确认邮件。
```

### Case 2：处理订单 `ORD002`（库存不足 -> 回滚）

请求：

```bash
curl "http://localhost:8080/chat/order?message=处理订单%20ORD002"
```

工具调用（控制台输出节选）：

```
用户消息：处理订单 ORD002
工具：正在验证订单：ORD002
订单 ORD002 有效。
工具：正在为订单预留库存：ORD002
为订单 ORD002 预留库存失败。库存不足。
工具：因失败正在回滚订单：ORD002
```

AI 回复示例：

```
订单 ORD002 处理失败。处理过程：

1. ✅ 订单验证 - 订单有效
2. ❌ 库存预留 - 库存不足，预留失败
3. ✅ 订单回滚 - 订单已成功回滚

处理无法继续进行，因为库存不足导致订单无法完成。建议联系客户告知库存情况并提供替代方案。
```

## 关键实现说明

### Skills 加载与技能作用域工具

- `LangChain4jConfig#skills()` 会从类路径加载 `skills/**/SKILL.md`
- 针对 `process-order` 技能，额外挂载 `OrderTools` 作为技能作用域工具：只有在模型调用 `activate_skill` 激活该技能后，`OrderTools` 的 `@Tool` 方法才会暴露给模型

### AiServices 注入 SystemMessage（动态拼接）

`LangChain4jConfig#orderProcessingAiService(...)` 使用：
- `.toolProvider(skills.toolProvider())` 提供 `activate_skill` / `read_skill_resource` 等工具能力
- `.systemMessage(...)` 把 `skills.formatAvailableSkills()` 动态注入系统提示，告知模型当前有哪些技能可以激活

## 项目结构（以当前代码为准）

```
src/main/java/com/cnblogs/yjmyzz/langchain4j/study/
├── LongChain4jStudyApplication.java
├── config/
│   ├── OllamaConfig.java              # Ollama Chat/Embedding/Streaming 模型 Bean
│   └── LangChain4jConfig.java         # Skills + ToolProvider + AiService 装配
├── controller/
│   └── OrderController.java           # GET /chat/order
├── service/
│   └── OrderProcessingAiService.java  # AiServices 接口（仅保留 @UserMessage）
└── tools/
    └── OrderTools.java                # process-order 技能下可用的工具集

src/main/resources/
├── application.yml
└── skills/
    └── process-order/
        └── SKILL.md
```

## 常见问题（Troubleshooting）

### 1) `@SystemMessage's template cannot be empty`

**原因**：在 AiServices 接口方法上声明了 `@SystemMessage("")`（空字符串）。  
**解决**：不要写空模板；如果系统消息要由 `AiServices.builder(...).systemMessage(...)` 动态提供，接口里就不要再放 `@SystemMessage`


## 许可证

MIT

## 📞 联系方式

如有问题，请通过以下方式联系：

- 提交 GitHub Issue：[https://github.com/yjmyzz/langchain4j-study/issues](https://github.com/yjmyzz/langchain4j-study/issues)
- 作者博客：[http://yjmyzz.cnblogs.com](http://yjmyzz.cnblogs.com)
- 作者：菩提树下的杨过

## 🙏 致谢

感谢 [LangChain4j](https://github.com/langchain4j/langchain4j) 开源项目提供的强大支持！

特别感谢以下官方文档资源：

- [LangChain4j 中文文档](https://docs.langchain4j.info/) - 为 Java 应用赋能大模型能力的官方中文指南
- [LangChain4j 英文文档](https://docs.langchain4j.dev/) - 官方英文文档，提供完整的技术参考
- [Ollama 官网](https://ollama.ai/) - 本地大语言模型运行环境
