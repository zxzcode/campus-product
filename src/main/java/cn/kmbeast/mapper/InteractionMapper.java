package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper

public interface InteractionMapper {

    int batchDelete(@Param("ids") List<Integer> ids);

    List<Interaction> query(InteractionQueryDto interactionQueryDto);

    int queryCount(InteractionQueryDto interactionQueryDto);

    int save(Interaction interaction);

    int likeProduct(Integer productId);
}
