package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.dto.update.OrdersDTO;
import cn.kmbeast.pojo.entity.Category;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.ProductVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ProductService {
    Result<String> save(Product product);

    Result<String> update(Product product);

    Result<String> batchDelete(List<Integer> ids);

    Result<List<ProductVO>> query(ProductQueryDto productQueryDto);

    SseEmitter polish(String detail);

    Result<List<ProductVO>> queryUser(ProductQueryDto productQueryDto);

    Result<String> buyProduct(OrdersDTO ordersDTO);

    Result<String> placeAnOrder(Integer ordersId);

    Result<String> refund(Integer ordersId);

    Result<List<ChartVO>> queryProductInfo(ProductQueryDto productQueryDto);
}
