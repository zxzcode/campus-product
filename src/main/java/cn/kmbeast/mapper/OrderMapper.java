package cn.kmbeast.mapper;

import cn.kmbeast.pojo.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface OrderMapper {
    <T> Optional<T> findById(@Param("orderId") String orderId);

    void save(Orders order);
}
