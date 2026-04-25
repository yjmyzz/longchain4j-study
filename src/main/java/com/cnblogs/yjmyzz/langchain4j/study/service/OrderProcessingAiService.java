package com.cnblogs.yjmyzz.langchain4j.study.service;

import dev.langchain4j.service.UserMessage;

public interface OrderProcessingAiService {

    // 系统消息由 Spring 配置中的 AiServices.builder(...).systemMessage(...) 提供
    String chat(@UserMessage String userMessage);
}