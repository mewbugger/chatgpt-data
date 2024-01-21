package com.wly.chatgpt.data.domain.openai.service;

import com.wly.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {

    /**
     * 调用chatGPT-sdk提供的流式问答的接口
     * @param emitter 用于给前端响应流式回答的content
     * @param chatProcess
     * @return
     */
    ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess);


}
