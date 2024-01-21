package com.wly.chatgpt.data.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GoogleGuavaCodeCacheConfig {

    @Bean(name = "codeCache")
    public Cache<String, String> codeCache() {
        return CacheBuilder.newBuilder() // 使用CacheBuilder来构建一个新的缓存实例
                .expireAfterWrite(3, TimeUnit.MINUTES) // 设置缓存项写入后3分钟后过期
                .build(); // 构建并返回Cache实例
    }
}
