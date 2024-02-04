package com.wly.chatgpt.data.domain.order.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付类型
 */
@Getter
@AllArgsConstructor
public enum PayTypeVO {

    AliPay_NATIVE(0, "阿里沙箱Native支付"),
    ;

    private final Integer code;
    private final String desc;

    public static PayTypeVO get(Integer code){
        switch (code){
            case 0:
                return PayTypeVO.AliPay_NATIVE;
            default:
                return PayTypeVO.AliPay_NATIVE;
        }
    }

}
