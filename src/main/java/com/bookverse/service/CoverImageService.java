package com.bookverse.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface cho nghiệp vụ xử lý ảnh bìa sách.
 * Bao gồm upload, resize, chuyển đổi WebP và phục vụ ảnh.
 */
public interface CoverImageService {

    /**
     * Xử lý upload ảnh bìa: validate, resize 3 kích thước, chuyển WebP, lưu file.
     *
     * @param bookId ID của sách (dùng để đặt tên file)
     * @param file   file ảnh upload
     * @return đường dẫn gốc (base path) của ảnh đã lưu (không bao gồm -size.webp)
     */
    String processAndSaveCover(Long bookId, MultipartFile file);

    /**
     * Lấy file ảnh bìa theo kích thước.
     *
     * @param coverPath đường dẫn gốc (base path) của ảnh
     * @param size      kích thước cần lấy: "thumbnail", "medium", "large"
     * @return Resource trỏ tới file ảnh
     */
    Resource loadCoverImage(String coverPath, String size);

    /**
     * Xóa tất cả file ảnh bìa liên quan tới một cuốn sách.
     *
     * @param coverPath đường dẫn gốc của ảnh bìa
     */
    void deleteCoverImages(String coverPath);

    /**
     * Validate file upload: kiểm tra loại file, kích thước.
     *
     * @param file file cần validate
     */
    void validateImageFile(MultipartFile file);
}
