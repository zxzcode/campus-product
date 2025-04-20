package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.OrdersMapper;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.vo.OrdersVO;
import cn.kmbeast.service.OrderCancelService;
import cn.kmbeast.service.OrdersService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderCancelService orderCancelService;

    @Override
    public Result<String> save(Orders orders) {
        if(ObjectUtils.isEmpty(orders)){
            return ApiResult.error("无法保存空值");
        }
        ordersMapper.save(orders);
        return ApiResult.success("订单保存成功");
    }
    @Override
    public Result<String> update(Orders orders) {
        if(ObjectUtils.isEmpty(orders)){
            return ApiResult.error("无法更新空值");
        }
        ordersMapper.update(orders);

        return ApiResult.success("订单更新成功");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return ApiResult.error("删除内容为空");
        }
        ordersMapper.batchDelete(ids);
        return ApiResult.success("删除成功");
    }

    @Override
    public Result<List<OrdersVO>> query(OrdersQueryDto ordersQueryDto) {
        int totalCount = ordersMapper.queryCount(ordersQueryDto);
        List<OrdersVO> ordersVOList = ordersMapper.query(ordersQueryDto);
        return ApiResult.success(ordersVOList,totalCount);
    }

    @Override
    public Result<List<OrdersVO>> queryOrdersList(OrdersQueryDto ordersQueryDto) {
        List<Integer> productIds = productMapper.queryProductIds(LocalThreadHolder.getUserId());
        ordersQueryDto.setProductIds(productIds);
        List<OrdersVO> ordersVOList = ordersMapper.queryByProductIds(ordersQueryDto);
        return ApiResult.success(ordersVOList);

    }

    @Override
    public Result<String> returnMoney(Integer ordersId) {
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setRefundStatus(true);
        orders.setIsRefundConfirm(true);
        orders.setRefundTime(LocalDateTime.now());
        ordersMapper.update(orders);
        return ApiResult.success("退款成功");
    }

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
