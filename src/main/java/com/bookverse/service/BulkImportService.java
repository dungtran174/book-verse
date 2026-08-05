package com.bookverse.service;

import com.bookverse.dto.BookResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Interface cho nghiệp vụ import sách hàng loạt từ Excel/CSV.
 */
public interface BulkImportService {

    /**
     * Import danh sách sách từ file Excel (.xlsx) hoặc CSV (.csv).
     * Mỗi dòng trong file tương ứng với một cuốn sách.
     *
     * Các cột bắt buộc: title, author
     * Các cột tùy chọn: isbn, year, category, rating, description
     *
     * @param file   file Excel hoặc CSV
     * @param images danh sách ảnh bìa (tùy chọn, khớp theo thứ tự dòng)
     * @return danh sách sách đã import thành công
     */
    List<BookResponseDto> importBooks(MultipartFile file, List<MultipartFile> images);
}
