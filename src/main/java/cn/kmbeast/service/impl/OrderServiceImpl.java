package cn.kmbeast.service.impl;

import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.service.OrderCancelService;
import cn.kmbeast.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderCancelService orderCancelService;

    @Transactional
    public void createOrder(Orders order) {
        // 1. 保存订单到数据库
        saveOrderToDB(order);

        // 2. 发送延迟消息到RabbitMQ
        sendOrderExpirationMessage(order);
    }

    private void saveOrderToDB(Orders order) {
        // 实际项目中这里应该是数据库操作
        System.out.println("保存订单到数据库: " + order.getId());
    }

    private void sendOrderExpirationMessage(Orders order) {
        // 设置消息属性，TTL为30分钟（单位毫秒）
        MessageProperties properties = new MessageProperties();
        properties.setExpiration("1800000"); // 30分钟

        // 创建消息
        Message message = new Message(order.getCode().getBytes(), properties);

        // 发送到订单队列
        rabbitTemplate.convertAndSend("order.exchange", "order.create", message);

        System.out.println("发送订单过期消息: " + order.getId());
    }

}
