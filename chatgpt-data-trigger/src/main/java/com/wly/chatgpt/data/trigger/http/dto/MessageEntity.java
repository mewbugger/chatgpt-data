package com.wly.chatgpt.data.trigger.http.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MessageEntity {

    // 角色
    private String role;
    // 内容
    private String content;
    // 名字
    private String name;

}
