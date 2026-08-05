package com.bookverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Lớp khởi chạy chính của ứng dụng BookVerse.
 * Hệ thống quản lý sách điện tử với REST API và quản lý ảnh bìa.
 */
@SpringBootApplication
@EnableCaching
public class BookVerseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookVerseApplication.class, args);
    }
}
