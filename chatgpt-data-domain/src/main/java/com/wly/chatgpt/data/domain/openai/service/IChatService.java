package com.wly.chatgpt.data.domain.openai.service;

import com.wly.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface IChatService {

    ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess);


}
