package cn.kmbeast.service.impl;

import cn.kmbeast.mapper.OrdersMapper;
import cn.kmbeast.pojo.em.OrderStatus;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.service.OrderCancelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCancelServiceImpl implements OrderCancelService {
    @Autowired
    private OrdersMapper ordersMapper;

    @Transactional
    public void cancelOrder(String orderId) {
        // 1. 查询订单状态
        Orders order = (Orders) ordersMapper.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 2. 只有未支付的订单才需要取消
        if (order.getTradeStatus() == (OrderStatus.UNPAID.getType().equals(1)?false:true)) {
            // 3. 更新订单状态为已取消
            order.setTradeStatus(OrderStatus.UNPAID.getType().equals(1)?false:true);
            ordersMapper.save(order);

            // 4. 执行补偿逻辑（如库存回滚等）
            compensateForCancellation(order);

            System.out.println("订单已取消: " + orderId);
        }
    }

    private void compensateForCancellation(Orders order) {
        // 实现补偿逻辑，如库存回滚、优惠券返还等
        System.out.println("执行补偿逻辑: " + order.getId());
    }
}
