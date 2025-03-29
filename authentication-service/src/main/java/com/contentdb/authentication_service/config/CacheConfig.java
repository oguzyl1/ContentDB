package com.contentdb.authentication_service.config;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        Cache tokenCache = new ConcurrentMapCache("tokenCache",
                CacheBuilder.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(1, TimeUnit.HOURS)
                        .build().asMap(),
                false);

        Cache tokenValidationCache = new ConcurrentMapCache("tokenValidationCache",
                CacheBuilder.newBuilder()
                        .maximumSize(2000)
                        .expireAfterWrite(10, TimeUnit.MINUTES)
                        .build().asMap(),
                false);

        cacheManager.setCaches(Arrays.asList(tokenCache, tokenValidationCache));
        return cacheManager;

    }

}
