package com.example.longchain4jstudy.controller;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天控制器
 * 提供与Ollama模型交互的REST API
 * 
 * @author 菩提树下的杨过
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatModel chatModel;

    /**
     * 发送聊天消息（GET方式）
     * 
     * @param prompt 用户输入的消息
     * @return 聊天响应
     */
    @GetMapping
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
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<String> chatStream(@RequestParam String prompt) {
        log.info("收到流式聊天请求: {}", prompt);
        
        StringBuilder responseBuilder = new StringBuilder();
        
        try {
            // 发送开始标记
            responseBuilder.append("data: {\"type\":\"start\",\"message\":\"开始生成回复...\"}\n\n");
            
            // 获取AI回复
            String aiResponse = chatModel.chat(prompt);
            
            // 模拟流式输出：按字符分割并逐个发送
            for (int i = 0; i < aiResponse.length(); i++) {
                String token = String.valueOf(aiResponse.charAt(i));
                responseBuilder.append("data: {\"type\":\"content\",\"message\":\"").append(escapeJson(token)).append("\"}\n\n");
            }
            
            // 发送结束标记
            responseBuilder.append("data: {\"type\":\"end\",\"message\":\"生成完成\"}\n\n");
            
        } catch (Exception e) {
            log.error("流式聊天发生错误", e);
            responseBuilder.append("data: {\"type\":\"error\",\"message\":\"抱歉，处理您的请求时发生了错误: ").append(escapeJson(e.getMessage())).append("\"}\n\n");
        }
        
        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .body(responseBuilder.toString());
    }
    
    /**
     * 转义JSON字符串
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
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
} 