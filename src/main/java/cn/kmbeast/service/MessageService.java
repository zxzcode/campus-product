package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.vo.MessageVO;

import java.util.List;

public interface MessageService {
    Result<List<MessageVO>> query(MessageQueryDto messageQueryDto);

    Result<String> save(Message message);

    Result<String> batchDelete(List<Integer> ids);

    Result<String> setRead(Integer userId);

    Result<List<MessageVO>> queryUser();
}
