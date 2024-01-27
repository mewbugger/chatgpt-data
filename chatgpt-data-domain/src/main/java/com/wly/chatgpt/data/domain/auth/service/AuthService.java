package com.wly.chatgpt.data.domain.auth.service;

import com.wly.chatgpt.data.domain.auth.model.entity.AuthStateEntity;
import com.wly.chatgpt.data.domain.auth.model.valobj.AuthTypeVO;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.google.common.cache.Cache;

import javax.annotation.Resource;

/**
 * 鉴权服务
 */
@Slf4j
@Service
public class AuthService extends AbstractAuthService{
    @Resource Cache<String, String> codeCache;
    @Override
    protected AuthStateEntity checkCode(String code) {
        // 获取验证码校验
        String openId = codeCache.getIfPresent(code);
        if (StringUtils.isBlank(openId)) {
            log.info("鉴权，用户收入的验证码不存在 {}", code);
            return AuthStateEntity.builder()
                    .code(AuthTypeVO.A0001.getCode())
                    .info(AuthTypeVO.A0001.getInfo())
                    .build();
        }
        // 移除缓存Key值 因为验证码只能校验一次，逻辑运行到这里就是已经校验成功了，要把cache中的缓存删掉
        codeCache.invalidate(openId);
        codeCache.invalidate(code);
        // 验证码校验成功
        return AuthStateEntity.builder()
                .code(AuthTypeVO.A0000.getCode())
                .info(AuthTypeVO.A0000.getInfo())
                .openId((openId))
                .build();
    }

    @Override
    public boolean checkToken(String token) {
        return isVerify(token);
    }

    @Override
    public String openid(String token) {
        Claims claims = decode(token);
        return claims.get("openId").toString();
    }
}
