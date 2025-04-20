package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.vo.InteractionVO;
import cn.kmbeast.pojo.vo.ProductVO;
import cn.kmbeast.service.InteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/interaction")
public class InteractionController {
    @Autowired
    private InteractionService interactionService;
    /**
     * 批量删除
     */
    @PostMapping(value = "/batchDelete")
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return interactionService.batchDelete(ids);
    }

    /**
     * 查询
     *
     * @param interactionQueryDto 查询参数
     * @return Result<List < Interaction>> 响应结果
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<InteractionVO>> query(@RequestBody InteractionQueryDto interactionQueryDto) {
        return interactionService.query(interactionQueryDto);
    }
    /**
     * 收藏操作 （取消收藏与收藏是一组对立的操作）
     *
     * @param productId 商品ID
     * @return Result<Boolean> 后台通用返回封装类
     */
    @PostMapping("/saveOperation/{productId}")
    public Result<Boolean> saveOperation(@PathVariable Integer productId){
        return interactionService.saveOperation(productId);
    }
    /**
     * "我想要"操作
     *
     * @param productId 商品ID
     * @return Result<Boolean> 后台通用返回封装类
     */
    @PostMapping("/likeProduct/{productId}")
    public Result<String> likeProduct(@PathVariable Integer productId){
        return interactionService.likeProduct(productId);
    }
    /**
     * 查询用户自己收藏的商品
     *
     * @return Result<List < Interaction>> 响应结果
     */
    @PostMapping(value = "/queryUser")
    @ResponseBody
    public Result<List<ProductVO>> queryUser() {
        return interactionService.queryUser();
    }
    /**
     * 查询用户自己浏览过的商品
     *
     * @return Result<List < ProductVO>> 响应结果
     */
    @PostMapping(value = "/myView")
    @ResponseBody
    public Result<List<ProductVO>> myView() {
        return interactionService.myView();
    }
    /**
     * 用户删除自己的浏览记录
     */
    @PostMapping(value = "/batchDeleteView")
    @ResponseBody
    public Result<String> batchDeleteInteraction() {
        return interactionService.batchDeleteInteraction();
    }

    /**
     * 记录浏览
     * @param productId
     * @return
     */
    @PostMapping(value = "/view/{productId}")
    public Result<String> view(@PathVariable(value = "productId") String productId) {
        return interactionService.view(productId);
    }
}
