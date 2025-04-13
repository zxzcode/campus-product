package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<MessageVO> query(MessageQueryDto messageQueryDto);

    int queryCount(MessageQueryDto messageQueryDto);

    int save(Message message);

    int batchDelete(@Param(value = "ids")List<Integer> ids);

    int setRead(@Param(value="userId")Integer userId);
}
