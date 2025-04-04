package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.entity.Category;

import java.util.List;

/**
 * 商品类别业务接口
 */
public interface CategoryService {
    Result<String> save(Category category);

    Result<String> update(Category category);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<Category>> query(CategoryQueryDto categoryQueryDto);
}
