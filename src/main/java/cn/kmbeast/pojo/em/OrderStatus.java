package cn.kmbeast.pojo.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    UNPAID(1, "未支付"),
    CANCELLED(2, "取消");

    /**
     * 状态
     */
    private final Integer type;
    /**
     * 状态解释
     */
    private final String detail;
}
