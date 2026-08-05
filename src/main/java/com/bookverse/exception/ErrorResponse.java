package com.bookverse.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO chuẩn hóa response lỗi trả về cho client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /** HTTP status code */
    private int status;

    /** Thông điệp lỗi chính */
    private String message;

    /** Thời gian xảy ra lỗi */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Chi tiết lỗi validation cho từng trường (nếu có) */
    private Map<String, String> errors;
}
