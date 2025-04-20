package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.controller.ProductController;
import cn.kmbeast.mapper.InteractionMapper;
import cn.kmbeast.mapper.OrdersMapper;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.dto.update.OrdersDTO;
import cn.kmbeast.pojo.em.InteractionEnum;
import cn.kmbeast.pojo.entity.Interaction;
import cn.kmbeast.pojo.entity.Orders;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ChartVO;
import cn.kmbeast.pojo.vo.ProductVO;
import cn.kmbeast.service.InteractionService;
import cn.kmbeast.service.ProductService;
import cn.kmbeast.utils.SensitiveFilter;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private InteractionMapper interactionMapper;
    @Override
    public Result<String> save(Product product) {
        if(StringUtils.isEmpty(product.getName())){
            return ApiResult.error("商品名不能为空");
        }
        product.setUserId(LocalThreadHolder.getUserId());
        product.setCreateTime(LocalDateTime.now());
        productMapper.save(product);
        return ApiResult.success();
    }

    @Override
    public Result<String> update(Product product) {
        productMapper.update(product);
        return ApiResult.success();
    }

    @Override
    public Result<String> batchDelete(List<Integer> ids) {
        productMapper.batchDelete(ids);
        return ApiResult.success();
    }

    @Override
    public Result<List<ProductVO>> query(ProductQueryDto productQueryDto) {
        int totalCount = productMapper.queryCount(productQueryDto);
        List<ProductVO> productVOList = productMapper.query(productQueryDto);
        //记录浏览历史
//        if (productVOList.size() == 1) {
//            Interaction interaction = new Interaction();
//            interaction.setUserId(productVOList.get(0).getUserId());
//            interaction.setType(InteractionEnum.VIEW.getType());
//            interaction.setProductId(productVOList.get(0).getId());
//            interaction.setCreateTime(LocalDateTime.now());
//            interactionMapper.save(interaction);
//        }
        return ApiResult.success(productVOList,totalCount);
    }

    @Override
    public SseEmitter polish(String detail) {
        logger.info("开始处理润色请求，内容长度: {}", detail != null ? detail.length() : 0);
        // 设置合理的超时时间，避免长时间处理时连接断开
        SseEmitter emitter = new SseEmitter(60000L);

        if (detail == null || detail.length() < 10) {
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("内容长度必须大于10个字"));
                // 发送结束标志
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("处理完成"));
                emitter.complete();
            } catch (IOException e) {
                logger.error("发送错误消息时发生异常: {}", e.getMessage(), e);
                emitter.completeWithError(e);
            }
            return emitter;
        }

        try {
            // 从配置文件或环境变量获取API密钥，而不是硬编码
            String apiKey = System.getenv("DASHSCOPE_API_KEY");
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = "sk-72506993cd1648f09f52a3f95dc580e4"; // 仅作为后备
            }
            
            // 确保API密钥格式正确
            if (!apiKey.startsWith("sk-")) {
                apiKey = "sk-" + apiKey;
            }
            
            // 设置全局API密钥
            Constants.apiKey = apiKey;
            logger.info("使用API密钥: {}", apiKey.substring(0, 5) + "***");

            // 构建应用参数
            ApplicationParam param = ApplicationParam.builder()
                    .apiKey(apiKey)
                    .appId("7e2fb2f747654d6abf5382205442ae6d") // 替换为实际的应用ID
                    .prompt("你是一位专业的二手商品文案优化专家，具备5年电商文案策划经验。请根据以下规则处理商品描述300字以内：\n\n" +
                            "### 核心原则\n" +
                            "1. **信息保真**：保持商品核心参数（型号/尺寸/成色等）绝对准确\n" +
                            "2. **情感共鸣**：运用FAB法则（特性→优势→利益）突出卖点\n" +
                            "3. **场景化表达**：创建使用场景如\"周末露营时...\"\n" +
                            "4. **信任构建**：通过细节描述增强可信度\n\n" +

                            "### 优化维度\n" +
                            "1. **结构优化**：采用「商品亮点+参数详情+使用场景」三段式结构\n" +
                            "2. **词汇升级**：\n" +
                            "   - 将\"很新\" → \"98新，仅拆封体验\"\n" +
                            "   - 将\"便宜卖\" → \"超高性价比，专柜同款1/3价格\"\n" +
                            "3. **痛点解决**：预判买家顾虑并主动解答\n\n" +

                            "### 风格要求\n" +
                            "1. 口语化但保持专业度（避免过度网络用语）\n" +
                            "2. 适当使用emoji（每段不超过2个）\n" +
                            "3. 重要信息采用「」符号突出\n" +
                            "4. 成色描述使用标准分级：\n" +
                            "   99新→仅拆封 / 95新→轻微使用痕迹 / 9成新→正常使用痕迹\n\n" +

                            "### 禁用行为\n" +
                            "❌ 虚构商品没有的特性\n" +
                            "❌ 使用夸张修辞如\"史上最低价\"\n" +
                            "❌ 出现联系方式或外链\n\n" +
                            "下面是输入的用户的内容:" + detail+",直接返回优化后的内容，不要有'以下是优化后','优化完成'相关词语")
                    .incrementalOutput(true)
                    .build();

            // 创建应用实例
            Application application = new Application();
            logger.info("已创建Application实例");

            // 发送初始ping事件
            emitter.send(SseEmitter.event().id("0").name("ping").data(""));
            logger.info("已发送初始ping事件");

            // 获取流式响应并处理
            logger.info("开始调用streamCall方法");
            Flowable<ApplicationResult> resultFlowable = application.streamCall(param);
            logger.info("已获取Flowable<ApplicationResult>");

            // 设置完成回调，清理资源
            emitter.onCompletion(() -> {
                logger.info("SSE连接已完成，正在关闭");
            });
            
            // 设置超时回调
            emitter.onTimeout(() -> {
                logger.info("SSE连接超时，正在关闭");
                emitter.complete();
            });
            
            // 设置错误回调
            emitter.onError((ex) -> {
                logger.error("SSE连接发生错误: {}", ex.getMessage(), ex);
            });

            // 在新线程中处理流式响应
            new Thread(() -> {
                try {
                    resultFlowable.subscribe(
                            result -> {
                                try {
                                    if (result != null && result.getOutput() != null) {
                                        String text = result.getOutput().getText();
                                        if (text != null && !text.isEmpty()) {
                                            // 使用JSON格式发送数据，确保客户端能正确解析
                                            emitter.send(SseEmitter.event()
                                                    .data("{\"content\": \"" + text + "\"}"));
                                        }
                                    }
                                } catch (Exception e) {
                                    logger.error("发送数据失败: " + e.getMessage(), e);
                                }
                            },
                            error -> {
                                logger.error("订阅过程中出现错误: " + error.getMessage(), error);
                                try {
                                    emitter.send(SseEmitter.event()
                                            .name("error")
                                            .data("处理请求时发生错误: " + error.getMessage()));
                                    // 发送结束标志
                                    emitter.send(SseEmitter.event()
                                            .name("complete")
                                            .data("处理完成"));
                                    emitter.complete();
                                } catch (IOException e) {
                                    emitter.completeWithError(e);
                                }
                            },
                            () -> {
                                logger.info("流式响应已完成");
                                try {
                                    // 发送结束标志
                                    emitter.send(SseEmitter.event()
                                            .name("complete")
                                            .data("处理完成"));
                                    emitter.complete();
                                } catch (Exception e) {
                                    logger.error("完成SSE连接时发生错误: " + e.getMessage(), e);
                                }
                            }
                    );
                } catch (Exception e) {
                    logger.error("处理流式响应时发生错误: " + e.getMessage(), e);
                    try {
                        emitter.send(SseEmitter.event()
                                .name("error")
                                .data("处理流式响应时发生错误: " + e.getMessage()));
                        // 发送结束标志
                        emitter.send(SseEmitter.event()
                                .name("complete")
                                .data("处理完成"));
                        emitter.complete();
                    } catch (IOException ex) {
                        emitter.completeWithError(ex);
                    }
                }
            }).start();

            return emitter;

        } catch (Exception e) {
            logger.error("处理润色请求时发生错误: " + e.getMessage(), e);
            try {
                emitter.send(SseEmitter.event()
                        .name("error")
                        .data("处理请求时发生错误: " + e.getMessage()));
                // 发送结束标志
                emitter.send(SseEmitter.event()
                        .name("complete")
                        .data("处理完成"));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
            return emitter;
        }
    }

    @Override
    public Result<List<ProductVO>> queryUser(ProductQueryDto productQueryDto) {
        if(ObjectUtils.isEmpty(productQueryDto)){
            return  ApiResult.error("1");
        }
        productMapper.queryUser(productQueryDto);
        return null;
    }

    @Override
    public Result<String> buyProduct(OrdersDTO ordersDTO) {
        if (ordersDTO.getProductId() == null) {
            return ApiResult.error("商品ID不为空");
        }
        ProductQueryDto productQueryDto = new ProductQueryDto();
        productQueryDto.setId(ordersDTO.getProductId());
        List<ProductVO> productVOS = productMapper.query(productQueryDto);
        if (productVOS.isEmpty()) {
            return ApiResult.error("商品信息异常");
        }
        // 有且仅有一条商品信息
        ProductVO productVO = productVOS.get(0);
        // 判断库存情况
        if (productVO.getInventory() <= 0
                || (productVO.getInventory() - ordersDTO.getBuyNumber()) < 0) {
            return ApiResult.error("商品库存不足");
        }
        createOrders(ordersDTO, productVO);
        ordersMapper.save(ordersDTO);
        // 扣库存
        Product product = new Product();
        product.setId(productVO.getId());
        product.setInventory(productVO.getInventory() - ordersDTO.getBuyNumber());
        productMapper.update(product);
        return ApiResult.success("下单成功");
    }

    /**
     * 商品下单
     * @param ordersId
     * @return Result<String> 通用返回封装类
     */
    @Override
    public Result<String> placeAnOrder(Integer ordersId) {
        if(ordersId == null){
            return ApiResult.error("订单ID不为空");
        }
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setTradeStatus(true);
        orders.setTradeTime(LocalDateTime.now());
        ordersMapper.update(orders);
        return ApiResult.success("下单成功");
    }

    /**
     * 申请退款
     * @param ordersId
     * @return Result<String>
     */
    @Override
    public Result<String> refund(Integer ordersId) {
        Orders orders = new Orders();
        orders.setId(ordersId);
        orders.setRefundStatus(false);
        ordersMapper.update(orders);
        return ApiResult.success("申请退款成功，请等待卖家审核");
    }
    /**
     * 查询用户商品指标情况
     *
     * @param productQueryDto 查询参数
     * @return Result<List < ChartVO>> 响应结果
     */
    @Override
    public Result<List<ChartVO>> queryProductInfo(ProductQueryDto productQueryDto) {
        List<Integer> productIds = productMapper.queryProductIds(productQueryDto.getUserId());
        List<Interaction> interactionList = interactionMapper.queryByProductIds(productIds);
        // 浏览、收藏、想要
        long viewCount = getProductCount(interactionList, InteractionEnum.VIEW.getType());
        long saveCount = getProductCount(interactionList, InteractionEnum.SAVE.getType());
        long loveCount = getProductCount(interactionList, InteractionEnum.LOVE.getType());
        List<ChartVO> chartVOList = new ArrayList<>();
        ChartVO chartVOView = new ChartVO("商品被浏览",(int)viewCount);
        ChartVO chartVOSave = new ChartVO("商品被收藏",(int)saveCount);
        ChartVO chartVOLove = new ChartVO("商品被想要",(int)loveCount);
        chartVOList.add(chartVOView);
        chartVOList.add(chartVOSave);
        chartVOList.add(chartVOLove);
        return ApiResult.success(chartVOList);
    }
    /**
     * 过滤指定的商品指标数据
     *
     * @param interactionList 互动数据源
     * @param type            互动类型
     * @return long
     */
    private long getProductCount(List<Interaction> interactionList, Integer type) {
        return interactionList.stream()
                .filter(interaction -> Objects.equals(type, interaction.getType()))
                .count();
    }

    /**
     * 设置订单所需参数
     *
     * @param orders    订单
     * @param productVO 商品信息
     */
    private void createOrders(Orders orders, ProductVO productVO) {
        orders.setCode(createOrdersCode());
        orders.setUserId(LocalThreadHolder.getUserId());
        orders.setTradeStatus(false); // 初始时，未交易成功
        orders.setBuyPrice(productVO.getPrice());
        orders.setCreateTime(LocalDateTime.now());
    }

    /**
     * 生成订单号
     *
     * @return String
     */
    private String createOrdersCode() {
        // UUID
        //String ordersCode = UUID.randomUUID().toString().toLowerCase();
        // 时间戳
        long timeMillis = System.currentTimeMillis();
        return String.valueOf(timeMillis);
    }

}
