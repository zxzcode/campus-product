package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;
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
    public Result<List<Interaction>> query(@RequestBody InteractionQueryDto interactionQueryDto) {
        return interactionService.query(interactionQueryDto);
    }
    @PostMapping("/saveOperation/{productId}")
    public Result<Boolean> saveOperation(@PathVariable Integer productId){
        return interactionService.saveOperation(productId);
    }
    @PostMapping("/likeProduct/{productId}")
    public Result<String> likeProduct(@PathVariable Integer productId){
        return interactionService.likeProduct(productId);
    }
}
