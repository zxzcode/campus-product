package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryQueryDto extends QueryDto {
    /**
     * 类别名
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean isUse;
}
