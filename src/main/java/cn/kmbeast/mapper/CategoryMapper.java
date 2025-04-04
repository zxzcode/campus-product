package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 分类持久层接口
 */
@Mapper
public interface CategoryMapper {
    int save(Category category);

    int update(Category category);

    void batchDelete(@Param(value="ids")List<Integer> ids);

    int queryCount(CategoryQueryDto categoryQueryDto);
    List<Category> query(CategoryQueryDto categoryQueryDto);
}
