package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.vo.OrdersVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrdersMapper {
    <T> Optional<T> findById(@Param("orderId") String orderId);

    int save(Orders order);

    int update(Orders orders);

    int batchDelete(@Param(value = "ids") List<Integer> ids);

    int queryCount(OrdersQueryDto ordersQueryDto);

    List<OrdersVO> query(OrdersQueryDto ordersQueryDto);

    List<OrdersVO> queryByProductIds(OrdersQueryDto ordersQueryDto);
}
