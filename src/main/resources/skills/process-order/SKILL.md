---
name: process-order
description: 处理客户订单
---

处理订单的步骤：

1. 调用 validateOrder(orderId) 检查订单是否有效。
2. 调用 reserveInventory(orderId) 预留所需库存。
3. 仅当预留成功时，调用 chargePayment(orderId) 进行扣款。
4. 最后，调用 sendConfirmationEmail(orderId) 发送确认邮件。

如果任何步骤失败，在报告错误之前调用 rollbackOrder(orderId) 回滚订单。