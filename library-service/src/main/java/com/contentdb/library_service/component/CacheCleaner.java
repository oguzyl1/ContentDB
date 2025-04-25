package com.contentdb.library_service.component;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheCleaner {

    private final CacheManager cacheManager;

    public CacheCleaner(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void clearAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            System.out.println("Cleaning cache: " + name);
            cacheManager.getCache(name).clear();
        });
    }
}
