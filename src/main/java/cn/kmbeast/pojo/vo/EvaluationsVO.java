package cn.kmbeast.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评论VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationsVO {
    /**
     * 总数
     */
    private Integer count;
    /**
     * 评论数据
     */
    private List<CommentParentVO> data;
}
