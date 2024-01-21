package com.wly.chatgpt.data.types.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    // 响应码
    private String code;
    // 响应信息
    private String info;
    // 响应数据
    private T data;
}
