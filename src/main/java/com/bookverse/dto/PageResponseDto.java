package com.bookverse.dto;

import lombok.*;

import java.util.List;

/**
 * DTO generic cho phân trang, bọc dữ liệu trả về kèm thông tin phân trang.
 *
 * @param <T> kiểu dữ liệu của từng phần tử trong danh sách
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDto<T> {

    /** Danh sách dữ liệu của trang hiện tại */
    private List<T> content;

    /** Số trang hiện tại (bắt đầu từ 0) */
    private int page;

    /** Kích thước mỗi trang */
    private int size;

    /** Tổng số phần tử */
    private long totalElements;

    /** Tổng số trang */
    private int totalPages;

    /** Có phải trang đầu tiên không */
    private boolean first;

    /** Có phải trang cuối cùng không */
    private boolean last;
}
