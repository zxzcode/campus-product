package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商品信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    /**
     * id
     */
    private Integer id;
    /**
     * 商品名
     */
    private String name;
    /**
     * 描述
     */
    private String detail;
    /**
     * 封面列表
     */
    private String coverList;
    /**
     * 新旧程度
     */
    private Integer oldLevel;
    /**
     * 类别Id
     */
    private Integer categoryId;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 库存
     */
    private Integer inventory;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 是否支持砍价
     */
    private Boolean isBargain;
    /**
     * 商品发布时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
