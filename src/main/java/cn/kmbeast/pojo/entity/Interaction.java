package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 互动实体类
 */
@Data //getter 、setter、覆盖默认toString、重写hashCode和equals
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Interaction {
    /**
     * id 主键
     */
    private Integer id ;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * 商品id
     */
    private Integer productId;
    /**
     * 行为类型（1：收藏；2：浏览；3：想要）
     */
    private Integer type;
    /**
     * 互动时间
     */
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
