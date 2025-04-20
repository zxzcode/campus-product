package cn.kmbeast.controller;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.MessageQueryDto;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.vo.MessageVO;
import cn.kmbeast.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @RequestMapping("/query")
    public Result<List<MessageVO>> query(MessageQueryDto messageQueryDto) {
        return messageService.query(messageQueryDto);
    }

    @RequestMapping("/insert")
    public Result<String> save(Message message) {
        return messageService.save(message);
    }

    @RequestMapping("/batchDelete")
    public Result<String> batchDelete(List<Integer> ids) {
        return messageService.batchDelete(ids);
    }
    /**
     * 消息设为已读
     */
    @PostMapping(value = "/setRead")
    @ResponseBody
    public Result<String> setRead() {
        return messageService.setRead(LocalThreadHolder.getUserId());
    }

    @PostMapping("/queryUser")
    public Result<List<MessageVO>> queryUser() {
        return messageService.queryUser();
    }
}
