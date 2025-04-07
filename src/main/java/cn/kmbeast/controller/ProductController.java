package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.CategoryQueryDto;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Category;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ProductVO;
import cn.kmbeast.service.CategoryService;
import cn.kmbeast.service.ProductService;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 商品分类接口1926
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public Result<String> save(@RequestBody Product product){
        return productService.save(product);
    }
    @PutMapping("/update")
    public Result<String> update(@RequestBody Product product){
        return productService.update(product);
    }
    @Protector(role="管理员")//管理员操作
    @PostMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids){
        return productService.batchDelete(ids);
    }
    @Pager
    @PostMapping("/query")
    public Result<List<ProductVO>> query(@RequestBody ProductQueryDto productQueryDto){
        return productService.query(productQueryDto);
    }
    @PostMapping ("/polish")
   
    public SseEmitter polish(@RequestBody String detail){
        return productService.polish(detail);
    }

}
