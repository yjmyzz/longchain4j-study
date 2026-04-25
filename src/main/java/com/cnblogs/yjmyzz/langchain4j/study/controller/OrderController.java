package com.cnblogs.yjmyzz.langchain4j.study.controller;

import com.cnblogs.yjmyzz.langchain4j.study.service.OrderProcessingAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 与使用技能的 LangChain4j AI 服务进行交互的 REST 控制器。
 */
@RestController
public class OrderController {

    private final OrderProcessingAiService orderProcessingAiService;

    // 自动注入 AI 服务
    public OrderController(OrderProcessingAiService orderProcessingAiService) {
        this.orderProcessingAiService = orderProcessingAiService;
    }

    /**
     * 与订单处理 AI 助手进行聊天的端点。
     * 示例：http://localhost:8080/chat/order?message=处理订单 ORD001
     * 示例：http://localhost:8080/chat/order?message=你叫什么名字？
     */
    @GetMapping("/chat/order")
    public String chatWithOrderAssistant(@RequestParam(value = "message", defaultValue = "处理订单 ORD001") String message) {
        System.out.println("用户消息：" + message);
        String aiResponse = orderProcessingAiService.chat(message);
        System.out.println("AI 回复：" + aiResponse);
        return aiResponse;
    }
}