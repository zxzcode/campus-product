package cn.kmbeast.controller;

import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.entity.Category;
import cn.kmbeast.service.CategoryService;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类接口11111
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping("/save")
    public Result<String> save(@RequestBody Category category){
        return categoryService.save(category);
    }
    @PutMapping("/update")
    public Result<String> update(@RequestBody Category category){
        return categoryService.update(category);
    }
    @Protector(role="管理员")//管理员操作
    @PostMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids){
        return categoryService.batchDelete(ids);
    }
    public Result<List<Category>> query(@RequestBody CategoryQueryDto categoryQueryDto){
        return categoryService.query(categoryQueryDto);
    }


}
