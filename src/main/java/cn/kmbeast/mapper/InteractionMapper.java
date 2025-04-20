package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.vo.InteractionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper

public interface InteractionMapper {

    int batchDelete(@Param("ids") List<Integer> ids);

    List<InteractionVO> query(InteractionQueryDto interactionQueryDto);

    int queryCount(InteractionQueryDto interactionQueryDto);

    int save(Interaction interaction);

    int likeProduct(Integer productId);
    /**
     * 查询商品指标数据
     *
     * @param ids 商品ID列表
     * @return List<Interaction>
     */
    List<Interaction> queryByProductIds(@Param(value = "ids")List<Integer> ids);
}
