package com.bookverse.service;

import com.bookverse.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests cho CoverImageServiceImpl.
 */
@DisplayName("CoverImageService Unit Tests")
class CoverImageServiceTest {

    private CoverImageServiceImpl coverImageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        coverImageService = new CoverImageServiceImpl();
        ReflectionTestUtils.setField(coverImageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(coverImageService, "thumbnailSize", 200);
        ReflectionTestUtils.setField(coverImageService, "mediumSize", 500);
        ReflectionTestUtils.setField(coverImageService, "largeSize", 1200);
    }

    @Test
    @DisplayName("Validate file ảnh hợp lệ - JPEG")
    void validateImageFile_ValidJpeg() {
        MockMultipartFile file = new MockMultipartFile(
                "cover", "test.jpg", "image/jpeg", "fake-image".getBytes());

        assertThatNoException().isThrownBy(() -> coverImageService.validateImageFile(file));
    }

    @Test
    @DisplayName("Validate file ảnh hợp lệ - PNG")
    void validateImageFile_ValidPng() {
        MockMultipartFile file = new MockMultipartFile(
                "cover", "test.png", "image/png", "fake-image".getBytes());

        assertThatNoException().isThrownBy(() -> coverImageService.validateImageFile(file));
    }

    @Test
    @DisplayName("Validate file ảnh không hợp lệ - GIF")
    void validateImageFile_InvalidType() {
        MockMultipartFile file = new MockMultipartFile(
                "cover", "test.gif", "image/gif", "fake-image".getBytes());

        assertThatThrownBy(() -> coverImageService.validateImageFile(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPG, PNG, WebP");
    }

    @Test
    @DisplayName("Validate file rỗng → ném exception")
    void validateImageFile_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "cover", "test.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> coverImageService.validateImageFile(file))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Upload và xử lý ảnh bìa thành công")
    void processAndSaveCover_Success() throws IOException {
        // Tạo ảnh giả 800x600
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);

        MockMultipartFile file = new MockMultipartFile(
                "cover", "test.png", "image/png", baos.toByteArray());

        String coverPath = coverImageService.processAndSaveCover(1L, file);

        assertThat(coverPath).isNotNull();
        assertThat(coverPath).startsWith("covers/");
        assertThat(coverPath).contains("/1");
    }

    @Test
    @DisplayName("Load ảnh bìa - coverPath null → ném exception")
    void loadCoverImage_NullPath() {
        assertThatThrownBy(() -> coverImageService.loadCoverImage(null, "large"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Load ảnh bìa - size không hợp lệ → ném exception")
    void loadCoverImage_InvalidSize() {
        assertThatThrownBy(() -> coverImageService.loadCoverImage("covers/2026/08/1", "huge"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("thumbnail, medium, large");
    }

    @Test
    @DisplayName("Xóa ảnh bìa - coverPath null → không ném exception")
    void deleteCoverImages_NullPath() {
        assertThatNoException().isThrownBy(() -> coverImageService.deleteCoverImages(null));
    }
}
