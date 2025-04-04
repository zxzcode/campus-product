package cn.kmbeast.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Orders {
    /**
     * id
     */
    private Integer id;
    /**
     * 订单号
     */
    private String code;

    /**
     * 商品Id
     */
    private Integer productId;
    /**
     * 备注
     */
    private String detail;
    /**
     * 购买价格
     */
    private BigDecimal buyPrice;
    /**
     * 交易状态
     */
    private Boolean tradeStatus;
    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
