package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ProductVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    int save(Product product);

    int update(Product product);

    int batchDelete(List<Integer> ids);

    int queryCount(ProductQueryDto productQueryDto);

    List<ProductVO> query(ProductQueryDto productQueryDto);
}
