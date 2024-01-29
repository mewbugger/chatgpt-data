package com.wly.chatgpt.data.domain.openai.repository;

import com.wly.chatgpt.data.domain.openai.model.entity.UserAccountQuotaEntity;

/**
 * 仓储接口
 */
public interface IOpenAiRepository {

    int subAccountQuota(String openai);

    UserAccountQuotaEntity queryUserAccount(String openid);
}
