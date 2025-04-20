package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.vo.InteractionVO;
import cn.kmbeast.pojo.vo.ProductVO;

import java.util.List;

public interface InteractionService {
    Result<String> batchDelete(List<Integer> ids);

    Result<List<InteractionVO>> query(InteractionQueryDto interactionQueryDto);

    Result<Boolean> saveOperation(Integer productId);
    Result<String> save(Interaction interaction);

    Result<String> likeProduct(Integer productId);

    Result<List<ProductVO>> queryUser();

    Result<List<ProductVO>> myView();

    Result<String> batchDeleteInteraction();

    Result<String> view(String productId);
}
