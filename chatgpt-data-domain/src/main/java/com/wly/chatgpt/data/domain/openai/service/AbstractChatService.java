package com.wly.chatgpt.data.domain.openai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wly.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.wly.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;
import com.wly.chatgpt.data.domain.openai.model.valobj.LogicCheckTypeVO;
import com.wly.chatgpt.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.wly.chatgpt.data.types.common.Constants;
import com.wly.chatgpt.data.types.exception.ChatGPTException;
import com.wly.chatgptsdk.session.OpenAiSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
public abstract class AbstractChatService implements IChatService{

    // openaiAiApi的会话接口
    @Resource
    protected OpenAiSession openAiSession;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter,ChatProcessAggregate chatProcess) throws Exception {
        // 1. 请求应答
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });

        emitter.onError(throwable -> log.error("流式问答请求疫情，使用模型：{}", chatProcess.getModel(), throwable));

        // 2. 规则过滤
        RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess,
                DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode());

        if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
            try {
                emitter.send(ruleLogicEntity.getInfo());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            emitter.complete();
            return emitter;
        }


        // 3. 应答处理
        try {
            this.doMessageResponse(chatProcess, emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 4. 返回结果
        return emitter;
    }

    protected abstract void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter responseBodyEmitter) throws JsonProcessingException;

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcessAggregate, String... logics) throws Exception;
}
