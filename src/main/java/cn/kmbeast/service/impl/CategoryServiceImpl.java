package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.CategoryMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.dto.query.extend.UserQueryDto;
import cn.kmbeast.pojo.dto.update.UserLoginDTO;
import cn.kmbeast.pojo.dto.update.UserRegisterDTO;
import cn.kmbeast.pojo.dto.update.UserUpdateDTO;
import cn.kmbeast.pojo.em.LoginStatusEnum;
import cn.kmbeast.pojo.em.RoleEnum;
import cn.kmbeast.pojo.em.WordStatusEnum;
import cn.kmbeast.pojo.entity.Category;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.UserVO;
import cn.kmbeast.service.CategoryService;
import cn.kmbeast.utils.JwtUtil;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.plaf.ListUI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 分类业务逻辑接口实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增类别
     * @param category
     * @return
     */
    @Override
    public Result<String> save(Category category) {
        if(ObjectUtils.isEmpty(category.getName())){
            return ApiResult.error("商品分类名不能为空");
        }
        categoryMapper.save(category);
        return ApiResult.success("商品类别新增成功");
    }

    @Override
    public Result<String> update(Category category) {
        if(ObjectUtils.isEmpty(category)){
            return ApiResult.error("更新非类信息为空");
        }
        categoryMapper.update(category);
        return ApiResult.success("修改成功");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        if(Collections.isEmpty(ids)){
            return ApiResult.error("删除内容为空");
        }
        categoryMapper.batchDelete(ids);
        return ApiResult.success("删除成功");
    }

    @Override
    public Result<List<Category>> query(CategoryQueryDto categoryQueryDto) {
        int totalCount = categoryMapper.queryCount(categoryQueryDto);
        return null;
    }
}
