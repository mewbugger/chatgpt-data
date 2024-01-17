package com.wly.chatgpt.data.domain.auth.service;

import com.wly.chatgpt.data.domain.auth.model.entity.AuthStateEntity;

/**
 * 鉴权验证服务接口
 */
public interface IAuthService {

    /**
     * 登录验证
     * @param code 验证码
     * @return
     */
    AuthStateEntity doLogin(String code);

    boolean checkToken(String token);
}
