package com.cnblogs.yjmyzz.langchain4j.study.config;

import com.cnblogs.yjmyzz.langchain4j.study.service.OrderProcessingAiService;
import com.cnblogs.yjmyzz.langchain4j.study.tools.OrderTools;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.skills.ClassPathSkillLoader;
import dev.langchain4j.skills.FileSystemSkill;
import dev.langchain4j.skills.Skill;
import dev.langchain4j.skills.Skills;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class LangChain4jConfig {

    private final OrderTools orderTools; // 自动注入 OrderTools

    // 注入 OrderTools，用于技能作用域的工具
    public LangChain4jConfig(OrderTools orderTools) {
        this.orderTools = orderTools;
    }

    @Bean
    public Skills skills() {
        // 从类路径下的 'skills' 目录加载所有技能
        // 假设你的 SKILL.md 文件位于 src/main/resources/skills/ 下
        List<FileSystemSkill> loadedSkills = ClassPathSkillLoader.loadSkills("skills");

        // 为 "process-order" 技能附加 OrderTools 作为技能作用域的工具
        // 这意味着 OrderTools 中的方法只有在显式激活 "process-order" 技能后，
        // 才会对 LLM 可见
        List<Skill> configuredSkills = loadedSkills.stream()
                .map(fsSkill -> {
                    if ("process-order".equals(fsSkill.name())) {
                        return Skill.builder()
                                .name(fsSkill.name())
                                .description(fsSkill.description())
                                .content(fsSkill.content())
                                .resources(fsSkill.resources())
                                .tools(orderTools) // 附加 OrderTools 作为技能作用域的工具
                                .build();
                    }
                    return fsSkill;
                })
                .collect(Collectors.toList());

        return Skills.from(configuredSkills);
    }

    @Bean
    public ToolProvider skillsToolProvider(Skills skills) {
        // 该 ToolProvider 将处理 'activate_skill'、'read_skill_resource'
        // 并在技能激活时动态暴露技能作用域的工具（例如 OrderTools 中的工具）
        return skills.toolProvider();
    }

    @Bean
    public OrderProcessingAiService orderProcessingAiService(ChatModel chatModel, Skills skills, ToolProvider skillsToolProvider) {
        // 构建 AI 服务，整合聊天模型、聊天记忆以及技能 ToolProvider
        return AiServices.builder(OrderProcessingAiService.class)
                .chatModel(chatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(100)) // 保留对话历史
                .toolProvider(skillsToolProvider) // 注册技能工具提供者
                // 将技能目录注入到系统消息中，以便 LLM 知道它可以激活哪些技能
                .systemMessage("你是一个订单处理助手。你可以使用以下技能：\n"
                        + skills.formatAvailableSkills() // 将技能格式化为 XML 提供给 LLM
                        + "\n当用户请求与这些技能之一相关时，请先使用 `activate_skill` 工具激活该技能，然后再继续处理。")
                .build();
    }
}
