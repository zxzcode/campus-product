package cn.kmbeast.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    /**
     * id
     * 主键
     */
    private Integer id;
    /**
     * name
     * 类别名
     */
    private String name;
    /**
     * is_use
     * 是否启用
     */
    private Boolean isUse;
}
