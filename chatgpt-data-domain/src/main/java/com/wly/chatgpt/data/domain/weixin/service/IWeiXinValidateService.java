package com.wly.chatgpt.data.domain.weixin.service;



/**
 * 验签接口
 */
public interface IWeiXinValidateService {

    /**
     * 校验签名
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    boolean checkSign(String signature, String timestamp, String nonce);
}
