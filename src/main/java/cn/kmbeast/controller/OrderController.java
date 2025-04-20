package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.OrdersQueryDto;
import cn.kmbeast.pojo.dto.update.OrdersDTO;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.vo.OrdersVO;
import cn.kmbeast.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrdersService ordersService;



    /**
     * 新增
     *
     * @param orders 参数
     * @return Result<String> 响应结果
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public Result<String> save(@RequestBody Orders orders) {
        return ordersService.save(orders);
    }

    /**
     * 修改
     *
     * @param orders 参数
     * @return Result<String> 响应结果
     */
    @PutMapping(value = "/update")
    @ResponseBody
    public Result<String> update(@RequestBody Orders orders) {
        return ordersService.update(orders);
    }

    /**
     * 批量删除
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public Result<String> batchDelete(@RequestBody List<Integer> ids) {
        return ordersService.batchDelete(ids);
    }

    /**
     * 查询
     *
     * @param ordersQueryDto 查询参数
     * @return Result<List < OrdersVO>> 响应结果
     */
    @Pager
    @PostMapping(value = "/query")
    @ResponseBody
    public Result<List<OrdersVO>> query(@RequestBody OrdersQueryDto ordersQueryDto) {
        return ordersService.query(ordersQueryDto);
    }
    /**
     * 查询用户自己名下的订单数据
     *
     * @param ordersQueryDto 查询参数
     * @return Result<List < OrdersVO>> 响应结果
     */
    @Pager
    @PostMapping(value = "/queryUser")
    @ResponseBody
    public Result<List<OrdersVO>> queryUser(@RequestBody OrdersQueryDto ordersQueryDto) {
        ordersQueryDto.setUserId(LocalThreadHolder.getUserId());
        return ordersService.query(ordersQueryDto);
    }

    /**
     * 查询用户发布的商品产生的订单数据
     *
     * @param ordersQueryDto 查询参数
     * @return Result<List < OrdersVO>> 响应结果
     */
    @PostMapping(value = "/queryOrdersList")
    @ResponseBody
    public Result<List<OrdersVO>> queryOrdersList(@RequestBody OrdersQueryDto ordersQueryDto) {
        return ordersService.queryOrdersList(ordersQueryDto);
    }

    /**
     * 订单确定退款
     *
     * @param ordersId 订单ID
     * @return Result<String> 响应结果
     */
    @PostMapping(value = "/returnMoney/{ordersId}")
    @ResponseBody
    public Result<String> returnMoney(@PathVariable Integer ordersId) {
        return ordersService.returnMoney(ordersId);
    }

}
