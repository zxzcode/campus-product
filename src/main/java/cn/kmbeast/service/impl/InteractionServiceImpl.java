package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.em.InteractionEnum;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.service.InteractionService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.stream.Collectors;

public class InteractionServiceImpl implements InteractionService {
    private InteractionMapper interactionMapper;
    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return ApiResult.error("删除内容为空");
        }
        interactionMapper.batchDelete(ids);
        return ApiResult.success("互动行为删除成功");
    }

    @Override
    public Result<List<Interaction>> query(InteractionQueryDto interactionQueryDto) {
        if(ObjectUtils.isEmpty(interactionQueryDto)){
            return ApiResult.error("查询参数为空");
        }
        int totalCount = interactionMapper.queryCount(interactionQueryDto);
        interactionMapper.query(interactionQueryDto);
        return null;
    }

    @Override
    public Result<Boolean> saveOperation(Integer productId) {
        if(productId== null){
            return ApiResult.error("未获取到商品ID");
        }
        InteractionQueryDto interactionQueryDto = new InteractionQueryDto();
        interactionQueryDto.setUserId(LocalThreadHolder.getUserId());
        interactionQueryDto.setProductId(productId);
        interactionQueryDto.setType(InteractionEnum.SAVE.getType());
        List<Interaction> interactionList = interactionMapper.query(interactionQueryDto);
        if(interactionList.isEmpty()){//收藏
            Interaction interaction = Interaction.builder()
                    .userId(LocalThreadHolder.getUserId())
                    .productId(productId)
                    .type(InteractionEnum.SAVE.getType())
                    .build();
            interactionMapper.save(interaction);
        }else{//取消收藏
            List<Integer> ids = interactionList.stream().map(Interaction::getId).collect(Collectors.toList());
            interactionMapper.batchDelete(ids);
        }
        return ApiResult.success(interactionList.isEmpty() ? "收藏成功":"取消收藏成功",interactionList.isEmpty());
    }

    @Override
    public Result<String> save(Interaction interaction) {
        if(ObjectUtils.isEmpty(interaction)){
            return ApiResult.error("无法保存空值");
        }
        interactionMapper.save(interaction);
        return ApiResult.success("保存成功");
    }
}
