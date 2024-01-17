package com.wly.chatgpt.data.domain.weixin.service;

import com.wly.chatgpt.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

/**
 * 受理用户行为接口
 */
public interface IWeiXinBehaviorService {

    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);
}
