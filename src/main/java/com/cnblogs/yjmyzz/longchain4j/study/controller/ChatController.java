package com.example.longchain4jstudy.controller;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * 聊天控制器
 * 提供与Ollama模型交互的REST API
 *
 * @author 菩提树下的杨过
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private StreamingChatModel streamingChatModel;

    /**
     * 发送聊天消息（GET方式）
     *
     * @param prompt 用户输入的消息
     * @return 聊天响应
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> chat(@RequestParam String prompt) {
        log.info("收到聊天请求: {}", prompt);

        try {
            String aiResponse = chatModel.chat(prompt);
            return ResponseEntity.ok(aiResponse);

        } catch (Exception e) {
            log.error("与Ollama通信时发生错误", e);
            String errorResponse = "抱歉，处理您的请求时发生了错误: " + e.getMessage();
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * 流式聊天消息（GET方式）
     *
     * @param prompt 用户输入的消息
     * @return 流式聊天响应
     */
    @GetMapping(value = "/chat/stream", produces = "text/html;charset=utf-8")
    public Flux<String> chatStream(@RequestParam String prompt) {
        log.info("收到流式聊天请求: {}", prompt);

        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

        streamingChatModel.chat(prompt, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                log.info("收到部分响应: {}",s);
                // 发送SSE格式的数据
                sink.tryEmitNext(escapeToHtml(s));
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                log.info("流式响应完成");
                sink.tryEmitComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("流式响应发生错误", throwable);
                sink.tryEmitError(throwable);
            }
        });

        return sink.asFlux();
    }


    /**
     * 健康检查端点
     *
     * @return 服务状态
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("LongChain4j Study服务运行正常");
    }


    public String escapeToHtml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("\n", "<br/>")
                .replace("<think>", "&lt;think&gt;")
                .replace("</think>", "&lt;/think&gt;");
    }
}