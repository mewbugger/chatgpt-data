package com.wly.chatgpt.data.domain.weixin.service;



/**
 * 验签接口
 */
public interface IWeiXinValidateService {

    boolean checkSign(String signature, String timestamp, String nonce);
}
