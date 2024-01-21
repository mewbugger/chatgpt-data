package com.wly.chatgpt.data.domain.weixin.service;

import com.wly.chatgpt.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

/**
 * 受理用户行为接口
 */
public interface IWeiXinBehaviorService {

    /**
     * 受理用户行为
     * @param userBehaviorMessageEntity 用户行为消息实体类
     * @return
     */
    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);
}
