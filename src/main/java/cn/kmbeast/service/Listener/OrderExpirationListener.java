package cn.kmbeast.service.Listener;

import cn.kmbeast.service.OrderCancelService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class OrderExpirationListener {

    @Autowired
    private OrderCancelService orderCancelService;

    @RabbitListener(queues = "order.dlx.queue")
    public void handleExpiredOrder(String orderId) {
        try {
            // 处理过期订单
            orderCancelService.cancelOrder(orderId);
        } catch (Exception e) {
            // 记录错误日志
            System.err.println("处理过期订单失败: " + orderId);
            e.printStackTrace();

            // 可以加入重试机制或人工干预队列
        }
    }
}
