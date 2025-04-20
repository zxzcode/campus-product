package cn.kmbeast.pojo.vo;

import cn.kmbeast.pojo.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersVO extends Orders {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 商品标题
     */
    private String productTitle;
    /**
     * 商品图
     */
    private String productCover;
}
