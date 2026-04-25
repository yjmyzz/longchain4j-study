package com.cnblogs.yjmyzz.langchain4j.study.tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

/**
 * 一组用于处理客户订单的工具。
 * 当 'process-order' SKILL激活时，LLM 将调用这些方法。
 */
@Component
public class OrderTools {

    @Tool("验证订单ID是否有效")
    public String validateOrder(String orderId) {
        System.out.println("工具：正在验证订单：" + orderId);
        // 模拟验证逻辑
        if (orderId != null && orderId.startsWith("ORD")) {
            System.out.println("订单 " + orderId + " 有效。");
            return "订单 " + orderId + " 有效。";
        } else {
            System.out.println("订单 " + orderId + " 无效。");
            return "订单 " + orderId + " 无效。";
        }
    }

    @Tool("为指定订单ID预留库存。返回成功或失败信息。")
    public String reserveInventory(String orderId) {
        System.out.println("工具：正在为订单预留库存：" + orderId);
        // 模拟库存预留
        if ("ORD001".equals(orderId)) { // 模拟成功示例
            System.out.println("订单 " + orderId + " 的库存已预留。");
            return "订单 " + orderId + " 的库存已预留。";
        } else if ("ORD002".equals(orderId)) { // 模拟失败示例
            System.out.println("为订单 " + orderId + " 预留库存失败。库存不足。");
            return "为订单 " + orderId + " 预留库存失败。库存不足。";
        } else {
            System.out.println("订单 " + orderId + " 的库存预留状态未知。");
            return "订单 " + orderId + " 的库存预留状态未知。";
        }
    }

    @Tool("为指定订单ID扣款。返回支付状态。")
    public String chargePayment(String orderId) {
        System.out.println("工具：正在为订单扣款：" + orderId);
        // 模拟支付处理
        return "订单 " + orderId + " 扣款成功。";
    }

    @Tool("向客户发送指定订单ID的确认邮件。返回确认状态。")
    public String sendConfirmationEmail(String orderId) {
        System.out.println("工具：正在为订单发送确认邮件：" + orderId);
        // 模拟邮件发送
        return "订单 " + orderId + " 的确认邮件已发送。";
    }

    @Tool("因处理出错回滚订单。返回回滚状态。")
    public String rollbackOrder(String orderId) {
        System.out.println("工具：因失败正在回滚订单：" + orderId);
        // 模拟回滚
        return "订单 " + orderId + " 已成功回滚。";
    }
}