package com.bookverse.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cấu hình Caffeine Cache cho ứng dụng.
 * Định nghĩa các cache riêng biệt với TTL khác nhau:
 * - books: cache chi tiết sách (mặc định 5 phút)
 * - searchResults: cache kết quả tìm kiếm (mặc định 2 phút)
 * - coverImages: cache ảnh bìa (mặc định 30 phút)
 */
@Configuration
public class CacheConfig {

    @Value("${app.cache.book-ttl:300}")
    private long bookTtl;

    @Value("${app.cache.search-ttl:120}")
    private long searchTtl;

    @Value("${app.cache.cover-ttl:1800}")
    private long coverTtl;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(
                buildCache("books", bookTtl, 500),
                buildCache("searchResults", searchTtl, 200),
                buildCache("coverImages", coverTtl, 1000)
        ));
        return cacheManager;
    }

    /**
     * Tạo một Caffeine cache với TTL và max size chỉ định.
     */
    private CaffeineCache buildCache(String name, long ttlSeconds, long maxSize) {
        return new CaffeineCache(name,
                Caffeine.newBuilder()
                        .expireAfterWrite(ttlSeconds, TimeUnit.SECONDS)
                        .maximumSize(maxSize)
                        .recordStats()
                        .build());
    }
}
