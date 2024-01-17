package com.wly.chatgpt.data.types.sdk.weixin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    private String title;
    private String description;
    private String picUrl;
    private String url;
}
