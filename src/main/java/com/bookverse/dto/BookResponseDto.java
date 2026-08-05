package com.bookverse.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO trả về thông tin chi tiết sách cho client.
 * Bao gồm các URL ảnh bìa theo từng kích thước.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer year;
    private String category;
    private Double rating;
    private String description;
    private String coverPath;

    /**
     * Map chứa URL ảnh bìa theo kích thước.
     * Ví dụ: {"thumbnail": "/api/books/1/cover?size=thumbnail",
     *         "medium": "/api/books/1/cover?size=medium",
     *         "large": "/api/books/1/cover?size=large"}
     */
    private Map<String, String> coverUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
