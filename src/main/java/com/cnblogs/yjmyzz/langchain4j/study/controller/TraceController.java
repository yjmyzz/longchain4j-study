package com.cnblogs.yjmyzz.langchain4j.study.controller;

import com.cnblogs.yjmyzz.langchain4j.study.listener.CustomAiServiceCompletedListener;
import com.cnblogs.yjmyzz.langchain4j.study.listener.CustomAiServiceStartedListener;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TraceController {

    private static final Logger log = LoggerFactory.getLogger(TraceController.class);

    @Autowired
    @Qualifier("ollamaChatModel")
    @Lazy
    OllamaChatModel ollamaChatModel;


    interface ChineseTeacher {

        @SystemMessage("你是一名小学语文老师")
        @UserMessage("请用中文回答我的问题：{{it}}")
        String chat(String query);
    }

    @GetMapping(value = "/chat/trace", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> chatChain(@RequestParam String query) {
        try {
            ChineseTeacher teacher = AiServices.builder(ChineseTeacher.class)
                    .chatModel(ollamaChatModel)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .registerListeners(List.of(new CustomAiServiceStartedListener(), new CustomAiServiceCompletedListener()))
                    .build();

            return ResponseEntity.ok(teacher.chat(query));
        } catch (Exception e) {
            log.error("chatChain", e);
            return ResponseEntity.ok("{\"error\":\"chatChain error: " + e.getMessage() + "\"}");
        }
    }

}
