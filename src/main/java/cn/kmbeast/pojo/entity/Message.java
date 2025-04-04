package cn.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    /**
     * 消息id
     */
    private Integer id;
    /**
     * 发消息用户Id
     */
    private Integer userId;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否已读
     */
    private Boolean isRead;
    /**
     * 消息时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
