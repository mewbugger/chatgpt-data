package com.wly.chatgpt.data.types.sdk.weixin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 校验签名工具类
 */
public class SignUtil {

    private static String token = "bcwly";

    /**
     * 校验签名
     * @param signature 签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return 布尔值
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String checkText = null;
        if (null != signature) {
            // 对Token，timestamp,nonce 按字典排序
            String[] paramArr = new String[]{token, timestamp, nonce};
            Arrays.sort(paramArr);
            // 将排序后的结果拼成一个字符串
            String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                // 对接后的字符串进行sha1加密
                byte[] digest = md.digest(content.toString().getBytes());
                checkText = byteToStr(digest);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        // 将加密后的字符串与signature进行对比
        return checkText != null ? checkText.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转化为16进制字符串
     * @param byteArrays
     * @return
     */
    private static String byteToStr(byte[] byteArrays) {
        String str = "";
        for (int i = 0; i < byteArrays.length; i++) {
            str += byteToHeStr(byteArrays[i]);
        }
        return str;
    }

    /**
     * 将字节转化为十六进制字符串
     * @param myByte
     * @return
     */
    private static String byteToHeStr(byte myByte) {
        char[] Digit = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] tampArr = new char[2];
        // myByte是字节型变量，有8为，无符号右移四位是将高位的4位移动到低位的4为，并与0X0F（0000 1111）相与，即获得byte的高四位
        tampArr[0] = Digit[(myByte >>> 4) & 0X0F];
        // 获得byte的低四位
        tampArr[1] = Digit[myByte & 0X0F];
        String str = new String(tampArr);
        return str;
    }
}
