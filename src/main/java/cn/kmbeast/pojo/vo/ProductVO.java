package cn.kmbeast.pojo.vo;

import cn.kmbeast.pojo.entity.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductVO extends Product {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatar;
    /**
     * 商品类别名
     */
    private String categoryName;
}
