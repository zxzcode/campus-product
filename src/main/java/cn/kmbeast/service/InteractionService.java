package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;

import java.util.List;

public interface InteractionService {
    Result<String> batchDelete(List<Integer> ids);

    Result<List<Interaction>> query(InteractionQueryDto interactionQueryDto);

    Result<Boolean> saveOperation(Integer productId);
    Result<String> save(Interaction interaction);
}
