package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.vo.MessageVO;
import cn.kmbeast.service.MessageService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public Result<List<MessageVO>> query(MessageQueryDto messageQueryDto) {
        if(ObjectUtils.isEmpty(messageQueryDto)){
            return ApiResult.error("参数为空");
        }
        int totalCount = messageMapper.queryCount(messageQueryDto);
        List<MessageVO> messageVOList = messageMapper.query(messageQueryDto);
        return  ApiResult.success(messageVOList,totalCount);
    }

    @Override
    public Result<String> save(Message message) {
        if(ObjectUtils.isEmpty(message)){
            return ApiResult.error("参数为空");
        }
        messageMapper.save(message);
        return ApiResult.success("保存成功");
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        if(Collections.isEmpty(ids)){
            return ApiResult.error("删除内容为空");
        }
        messageMapper.batchDelete(ids);
        return ApiResult.success("删除成功");
    }

    @Override
    public Result<String> setRead(Integer userId) {
        if(ObjectUtils.isEmpty(userId)){
            return ApiResult.error("用户ID为空");
        }
        messageMapper.setRead(userId);
        return ApiResult.success("已读消息");
    }

    @Override
    public Result<List<MessageVO>> queryUser() {
        MessageQueryDto messageQueryDto = new MessageQueryDto();
        messageQueryDto.setUserId(LocalThreadHolder.getUserId());
        List<MessageVO> messageResultList = messageMapper.query(messageQueryDto);
        return ApiResult.success(messageResultList);

    }
}
