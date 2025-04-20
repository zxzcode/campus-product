package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.mapper.MessageMapper;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.mapper.UserMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.InteractionQueryDto;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.em.InteractionEnum;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.entity.Message;
import cn.kmbeast.pojo.entity.User;
import cn.kmbeast.pojo.vo.InteractionVO;
import cn.kmbeast.pojo.vo.ProductVO;
import cn.kmbeast.service.InteractionService;
import cn.kmbeast.service.ProductService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
@Service
public class InteractionServiceImpl implements InteractionService {
    @Autowired
    private InteractionMapper interactionMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        if(CollectionUtils.isEmpty(ids)){
            return ApiResult.error("删除内容为空");
        }
        interactionMapper.batchDelete(ids);
        return ApiResult.success("互动行为删除成功");
    }

    @Override
    public Result<List<InteractionVO>> query(InteractionQueryDto interactionQueryDto) {
        if(ObjectUtils.isEmpty(interactionQueryDto)){
            return ApiResult.error("查询参数为空");
        }
        int totalCount = interactionMapper.queryCount(interactionQueryDto);
        List<InteractionVO> interactionList = interactionMapper.query(interactionQueryDto);
        return ApiResult.success(interactionList,totalCount);
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
        List<InteractionVO> interactionList = interactionMapper.query(interactionQueryDto);
        if(interactionList.isEmpty()){//收藏
            Interaction interaction = Interaction.builder()
                    .userId(LocalThreadHolder.getUserId())
                    .productId(productId)
                    .type(InteractionEnum.SAVE.getType())
                    .createTime(LocalDateTime.now())
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
        interaction.setCreateTime(LocalDateTime.now());
        interactionMapper.save(interaction);
        return ApiResult.success("保存成功");
    }

    @Override
    @Transactional
    public Result<String> likeProduct(Integer productId) {
        if(ObjectUtils.isEmpty(productId)){
            return ApiResult.error("未获取到商品ID");
        }
        //用户不能重复想要一个商品
        InteractionQueryDto interactionQueryDto =  new InteractionQueryDto();
        interactionQueryDto.setUserId(LocalThreadHolder.getUserId());
        interactionQueryDto.setProductId(productId);
        interactionQueryDto.setType(InteractionEnum.LOVE.getType());
        int count = interactionMapper.queryCount(interactionQueryDto);
        if(count != 0){
            return ApiResult.error("您已经想要过该商品");
        }
        // 如果商品信息不存在，直接返回
        ProductQueryDto productQueryDto = new ProductQueryDto();
        productQueryDto.setId(productId);
        List<ProductVO> product = productMapper.query(productQueryDto);
        if(CollectionUtils.isEmpty(product)){
            return ApiResult.error("商品信息不存在");
        }
        // 用户自己感兴趣自己的商品，明显不合理，所以如果商品是自己的商品，不做处理了
        if(Objects.equals(product.get(0).getUserId(), LocalThreadHolder.getUserId())){
            return ApiResult.error("不能自己想要自己的商品");
        }
        Interaction interaction = Interaction.builder()
                .userId(LocalThreadHolder.getUserId())
                .productId(productId)
                .type(InteractionEnum.LOVE.getType())
                .createTime(LocalDateTime.now())
                .build();
        interactionMapper.save(interaction);
        Integer publishUserId = product.get(0).getUserId() ;
        Integer userId = LocalThreadHolder.getUserId();
        User user = new User();
        user.setId(userId);
        User operator = userMapper.getByActive(user);
        Message message = Message.builder()
                .userId(publishUserId)
                .content("用户【" + operator.getUserName() + "】对你的【" + product.get(0).getName() + "】感兴趣!")
                .isRead(false)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.save(message);

        return ApiResult.success("卖家已感受到你的热情，快下单吧!");
    }

    /**
     * 查询用户自己收藏的商品
     *
     * @return Result<List < Interaction>> 响应结果
     */
    @Override
    public Result<List<ProductVO>> queryUser() {
        InteractionQueryDto interactionQueryDto = new InteractionQueryDto();
        interactionQueryDto.setUserId(LocalThreadHolder.getUserId());
        interactionQueryDto.setType(InteractionEnum.SAVE.getType());
        List<InteractionVO> interactionList = interactionMapper.query(interactionQueryDto);
        List<Integer> productIds = interactionList.stream()
                .map(Interaction::getProductId).collect(Collectors.toList());
        // 通过商品的ID列表，查询用户收藏的这些商品返回
        List<ProductVO> productVOS = productMapper.queryProductList(productIds);
        return ApiResult.success(productVOS);
    }

    @Override
    public Result<List<ProductVO>> myView() {
        InteractionQueryDto interactionQueryDto = new InteractionQueryDto();
        interactionQueryDto.setUserId(LocalThreadHolder.getUserId());
        interactionQueryDto.setType(InteractionEnum.VIEW.getType());
        List<InteractionVO> interactionList = interactionMapper.query(interactionQueryDto);
        if(Collections.isEmpty(interactionList)){
            return ApiResult.success(new ArrayList<>());
        }
        List<Integer> productIds = interactionList.stream()
                .map(Interaction::getProductId).collect(Collectors.toList());
        List<ProductVO> productVOS = productMapper.queryProductList(productIds);
        return ApiResult.success(productVOS);
    }

    @Override
    public Result<String> batchDeleteInteraction() {
        InteractionQueryDto interactionQueryDto = new InteractionQueryDto();
        interactionQueryDto.setUserId(LocalThreadHolder.getUserId());
        interactionQueryDto.setType(InteractionEnum.VIEW.getType());
        List<InteractionVO> interactionList = interactionMapper.query(interactionQueryDto);
        if(Collections.isEmpty(interactionList)){
            return ApiResult.success();
        }
        interactionMapper.batchDelete(interactionList.stream().map(Interaction::getId).collect(Collectors.toList()));
        return ApiResult.success();
    }

    @Override
    public Result<String> view(String productId) {
        if(StringUtils.isEmpty(productId)){
            return ApiResult.error("未获取到商品ID");
        }
        Interaction interaction =new Interaction();
        interaction.setUserId(LocalThreadHolder.getUserId());
        interaction.setProductId(Integer.parseInt(productId));
        interaction.setType(InteractionEnum.VIEW.getType());
        interaction.setCreateTime(LocalDateTime.now());
        interactionMapper.save(interaction);
        return ApiResult.success("浏览记录成功");
    }
}
