package com.bookverse.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho một cuốn sách trong hệ thống BookVerse.
 * Ánh xạ tới bảng "books" trong database.
 */
@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_books_category", columnList = "category"),
        @Index(name = "idx_books_year", columnList = "publication_year"),
        @Index(name = "idx_books_title", columnList = "title"),
        @Index(name = "idx_books_author", columnList = "author")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Tiêu đề sách - bắt buộc */
    @Column(nullable = false, length = 255)
    private String title;

    /** Tác giả - bắt buộc */
    @Column(nullable = false, length = 255)
    private String author;

    /** Mã ISBN - duy nhất, tối đa 13 ký tự */
    @Column(unique = true, length = 13)
    private String isbn;

    /** Năm xuất bản */
    @Column(name = "publication_year")
    private Integer year;

    /** Thể loại sách */
    @Column(length = 100)
    private String category;

    /** Đánh giá sách (0.0 - 5.0) */
    @Column
    @Builder.Default
    private Double rating = 0.0;

    /** Mô tả chi tiết về cuốn sách */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Đường dẫn gốc tới thư mục chứa ảnh bìa (không bao gồm -size.webp) */
    @Column(name = "cover_path", length = 500)
    private String coverPath;

    /** Thời gian tạo bản ghi */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Thời gian cập nhật gần nhất */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
