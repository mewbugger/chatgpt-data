package com.wly.chatgpt.data.domain.openai.service.rule;

import com.wly.chatgpt.data.domain.openai.model.aggregates.ChatProcessAggregate;
import com.wly.chatgpt.data.domain.openai.model.entity.RuleLogicEntity;

/**
 * 规则过滤接口
 */
public interface ILogicFilter {
    RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcessAggregate) throws Exception;
}
