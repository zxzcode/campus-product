package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;

public class ProductQueryDto extends QueryDto {
    /**
     * 商品名
     */
    private String name;
    /**
     * 所属商品类别ID
     */
    private Integer categoryId;
    /**
     * 用户ID
     */
    private Integer userId;
    /**
     * 是否支持砍价
     */
    private Boolean isBargain;
}
