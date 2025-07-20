package com.example.longchain4jstudy.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Ollama配置类
 * 用于配置LongChain4j与本地Ollama服务的连接
 * 
 * @author 菩提树下的杨过
 * @version 1.0.0
 */
@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model:qwen3:0.6b}")
    private String ollamaModel;

    @Value("${ollama.timeout:60}")
    private Integer timeoutSeconds;

    /**
     * 配置Ollama聊天模型
     * 
     * @return ChatLanguageModel实例
     */
    @Bean
    public ChatModel chatLanguageModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModel)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }
} 