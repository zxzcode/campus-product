package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.controller.ProductController;
import cn.kmbeast.mapper.ProductMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ProductQueryDto;
import cn.kmbeast.pojo.entity.Product;
import cn.kmbeast.pojo.vo.ProductVO;
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
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Override
    public Result<String> save(Product product) {
        if(StringUtils.isEmpty(product.getName())){
            return ApiResult.error("商品名不能为空");
        }else{
            product.setName(sensitiveFilter.filiter(product.getName()));
        }
        if(!StringUtils.isEmpty(product.getDetail())){
            product.setDetail(sensitiveFilter.filiter(product.getDetail()));
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
        List<ProductVO> product = productMapper.query(productQueryDto);
        return ApiResult.success();
    }

    @Override
    public SseEmitter polish(String detail) {
        logger.info("开始处理润色请求，内容长度: {}", detail != null ? detail.length() : 0);
        // 设置更长的超时时间，避免长时间处理时连接断开
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
                            "下面是输入的用户的内容:" + detail)
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

}
