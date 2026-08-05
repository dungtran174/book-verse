package com.bookverse.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO nhận dữ liệu đầu vào khi tạo mới hoặc cập nhật sách.
 * Bao gồm validation cho từng trường dữ liệu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 255, message = "Tên tác giả không được vượt quá 255 ký tự")
    private String author;

    @Size(max = 13, message = "ISBN không được vượt quá 13 ký tự")
    private String isbn;

    @Min(value = 1000, message = "Năm xuất bản phải từ 1000 trở lên")
    @Max(value = 2100, message = "Năm xuất bản không hợp lệ")
    private Integer year;

    @Size(max = 100, message = "Thể loại không được vượt quá 100 ký tự")
    private String category;

    @DecimalMin(value = "0.0", message = "Rating phải từ 0.0")
    @DecimalMax(value = "5.0", message = "Rating tối đa là 5.0")
    private Double rating;

    @Size(max = 5000, message = "Mô tả không được vượt quá 5000 ký tự")
    private String description;
}
