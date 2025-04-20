package cn.kmbeast.controller;

import cn.kmbeast.aop.Log;
import cn.kmbeast.aop.Pager;
import cn.kmbeast.aop.Protector;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.dto.update.OrdersDTO;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.ProductVO;
import cn.kmbeast.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 商品分类接口1926
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;
    /**
     * 商品下单
     *
     * @param ordersDTO 参数
     * @return Result<String> 响应结果
     */
    @Log(detail = "商品下单")
    @PostMapping(value = "/buyProduct")
    @ResponseBody
    public Result<String> buyProduct(@RequestBody OrdersDTO ordersDTO) {
        return productService.buyProduct(ordersDTO);
    }

    /**
     * 商品上架
     * @param product
     * @return
     */
    @Log(detail = "商品上架")
    @PostMapping("/save")
    public Result<String> save(@RequestBody Product product){
        return productService.save(product);
    }

    @Log(detail = "商品修改")
    @PutMapping("/update")
    public Result<String> update(@RequestBody Product product){
        return productService.update(product);
    }
    @Protector(role="管理员")//管理员操作
    @PostMapping("/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids){
        return productService.batchDelete(ids);
    }
    /**
     * 查询用户商品列表
     *
     * @param productQueryDto 查询参数
     * @return Result<List < ProductVO>> 响应结果
     */
    @Pager
    @PostMapping("/query")
    public Result<List<ProductVO>> query(@RequestBody ProductQueryDto productQueryDto){
        return productService.query(productQueryDto);
    }

    /**
     * 润色商品内容
     * @param detail
     * @return
     */
    @GetMapping ("/polish")
    public SseEmitter polish(@RequestParam("detail") String detail){
        return productService.polish(detail);
    }
    @PostMapping("/queryUser")
    public Result<List<ProductVO>> queryUser(@RequestBody ProductQueryDto productQueryDto){
        productQueryDto.setUserId(LocalThreadHolder.getUserId());
        return productService.query(productQueryDto);
    }

    /**
     * 商品下单
     *
     * @param ordersId 订单ID
     * @return Result<String> 响应结果
     */
    @PostMapping(value = "/placeAnOrder/{ordersId}")
    @ResponseBody
    public Result<String> placeAnOrder(@PathVariable Integer ordersId) {
        return productService.placeAnOrder(ordersId);
    }

    /**
     * 申请退款
     *
     * @param ordersId 订单ID
     * @return Result<String> 响应结果
     */
    @Log(detail = "商品申请退款")
    @PostMapping(value = "/refund/{ordersId}")
    @ResponseBody
    public Result<String> refund(@PathVariable Integer ordersId) {
        return productService.refund(ordersId);
    }
    /**
     * 查询用户商品指标情况
     *
     * @param productQueryDto 查询参数
     * @return Result<List < ChartVO>> 响应结果
     */
    @PostMapping(value = "/queryProductInfo")
    @ResponseBody
    public Result<List<ChartVO>> queryProductInfo(@RequestBody ProductQueryDto productQueryDto) {
        productQueryDto.setUserId(LocalThreadHolder.getUserId());
        return productService.queryProductInfo(productQueryDto);
    }




}
