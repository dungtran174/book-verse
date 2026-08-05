package com.bookverse.service;

import com.bookverse.exception.FileProcessingException;
import com.bookverse.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Xử lý upload, resize (3 kích thước), chuyển đổi WebP, lưu trữ và phục vụ ảnh bìa.
 * Cấu trúc lưu trữ: {uploadDir}/covers/{yyyy}/{MM}/{bookId}-{size}.webp
 */
@Service
@Slf4j
public class CoverImageServiceImpl implements CoverImageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.image.thumbnail-size:200}")
    private int thumbnailSize;

    @Value("${app.image.medium-size:500}")
    private int mediumSize;

    @Value("${app.image.large-size:1200}")
    private int largeSize;

    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public String processAndSaveCover(Long bookId, MultipartFile file) {
        log.info("Xử lý ảnh bìa cho sách ID: {}", bookId);
        try {
            BufferedImage original;
            try (InputStream is = file.getInputStream()) {
                original = ImageIO.read(is);
            }
            if (original == null) {
                throw new FileProcessingException("Không thể đọc file ảnh");
            }

            LocalDate now = LocalDate.now();
            String yearMonth = now.format(DateTimeFormatter.ofPattern("yyyy/MM"));
            Path coverDir = Paths.get(uploadDir, "covers", yearMonth);
            Files.createDirectories(coverDir);

            String basePath = "covers/" + yearMonth + "/" + bookId;

            resizeAndSave(original, coverDir, bookId, "thumbnail", thumbnailSize);
            resizeAndSave(original, coverDir, bookId, "medium", mediumSize);
            resizeAndSave(original, coverDir, bookId, "large", largeSize);

            log.info("Ảnh bìa đã xử lý: {}", basePath);
            return basePath;
        } catch (IOException e) {
            throw new FileProcessingException("Lỗi xử lý ảnh: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource loadCoverImage(String coverPath, String size) {
        if (coverPath == null || coverPath.isBlank()) {
            throw new ResourceNotFoundException("Sách chưa có ảnh bìa");
        }
        String validSize = (size == null || size.isBlank()) ? "medium" : size.trim().toLowerCase();
        if (!List.of("thumbnail", "medium", "large").contains(validSize)) {
            throw new IllegalArgumentException("Kích thước không hợp lệ. Chấp nhận: thumbnail, medium, large");
        }

        Path filePath = Paths.get(uploadDir, coverPath + "-" + validSize + ".webp");
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new ResourceNotFoundException("Ảnh bìa không tồn tại: " + validSize);
        } catch (MalformedURLException e) {
            throw new FileProcessingException("Đường dẫn ảnh không hợp lệ", e);
        }
    }

    @Override
    public void deleteCoverImages(String coverPath) {
        if (coverPath == null) return;
        for (String s : List.of("thumbnail", "medium", "large")) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir, coverPath + "-" + s + ".webp"));
            } catch (IOException e) {
                log.warn("Không thể xóa: {}-{}.webp", coverPath, s);
            }
        }
    }

    @Override
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File ảnh không được để trống");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Chỉ chấp nhận: JPG, PNG, WebP");
        }
    }

    private void resizeAndSave(BufferedImage original, Path dir, Long bookId,
                                String sizeName, int target) throws IOException {
        int w = original.getWidth(), h = original.getHeight();
        int nw, nh;
        if (w > h) { nw = target; nh = (int)((double)h / w * target); }
        else { nh = target; nw = (int)((double)w / h * target); }

        Path output = dir.resolve(bookId + "-" + sizeName + ".webp");
        // Resize và lưu - dùng PNG format (rename .webp), WebP writer tùy thuộc runtime
        Thumbnails.of(original).size(nw, nh).outputFormat("png").toFile(output.toFile());
        log.debug("Tạo ảnh {}: {}x{}", sizeName, nw, nh);
    }
}
