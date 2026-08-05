package com.bookverse.controller;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.dto.PageResponseDto;
import com.bookverse.service.BookService;
import com.bookverse.service.BulkImportService;
import com.bookverse.service.CoverImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller quản lý sách - cung cấp đầy đủ CRUD, tìm kiếm,
 * upload ảnh bìa và import hàng loạt.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book Management", description = "API quản lý sách điện tử BookVerse")
public class BookController {

    private final BookService bookService;
    private final BulkImportService bulkImportService;
    private final CoverImageService coverImageService;

    // ==================== CRUD APIs ====================

    /**
     * GET /api/books - Lấy danh sách sách với phân trang, lọc, sắp xếp.
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách sách",
            description = "Phân trang, lọc theo thể loại/năm, sắp xếp theo tên/năm/rating")
    @ApiResponse(responseCode = "200", description = "Thành công")
    public ResponseEntity<PageResponseDto<BookResponseDto>> getAllBooks(
            @Parameter(description = "Số trang (bắt đầu từ 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sắp xếp: field,direction (vd: title,asc)") @RequestParam(required = false) String sort,
            @Parameter(description = "Lọc theo thể loại") @RequestParam(required = false) String category,
            @Parameter(description = "Lọc theo năm xuất bản") @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, sort, category, year));
    }

    /**
     * GET /api/books/{id} - Lấy chi tiết một cuốn sách.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết sách theo ID")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy sách")
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "ID của sách") @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    /**
     * POST /api/books - Tạo sách mới, hỗ trợ upload ảnh bìa.
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Tạo sách mới", description = "Tạo sách kèm upload ảnh bìa (tùy chọn)")
    @ApiResponse(responseCode = "201", description = "Tạo thành công")
    @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ")
    public ResponseEntity<BookResponseDto> createBook(
            @Valid @RequestPart("book") BookRequestDto bookRequest,
            @Parameter(description = "File ảnh bìa (JPG, PNG, WebP)")
            @RequestPart(value = "cover", required = false) MultipartFile coverImage) {
        BookResponseDto created = bookService.createBook(bookRequest, coverImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/books/{id} - Cập nhật sách.
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Cập nhật sách", description = "Cập nhật thông tin và/hoặc ảnh bìa")
    @ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy sách")
    public ResponseEntity<BookResponseDto> updateBook(
            @PathVariable Long id,
            @Valid @RequestPart("book") BookRequestDto bookRequest,
            @RequestPart(value = "cover", required = false) MultipartFile coverImage) {
        return ResponseEntity.ok(bookService.updateBook(id, bookRequest, coverImage));
    }

    /**
     * DELETE /api/books/{id} - Xóa sách.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sách theo ID")
    @ApiResponse(responseCode = "204", description = "Xóa thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy sách")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Search API ====================

    /**
     * GET /api/books/search - Tìm kiếm full-text theo tên sách, tác giả.
     */
    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm sách", description = "Full-text search theo tên sách và tác giả")
    public ResponseEntity<PageResponseDto<BookResponseDto>> searchBooks(
            @Parameter(description = "Từ khóa tìm kiếm") @RequestParam(required = false) String q,
            @Parameter(description = "Lọc theo thể loại") @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(bookService.searchBooks(q, category, page, size, sort));
    }

    // ==================== Cover Image API ====================

    /**
     * GET /api/books/{id}/cover - Trả về file ảnh bìa theo kích thước.
     */
    @GetMapping("/{id}/cover")
    @Operation(summary = "Lấy ảnh bìa sách", description = "Trả về file ảnh theo kích thước: thumbnail, medium, large")
    @ApiResponse(responseCode = "200", description = "Trả về file ảnh")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy ảnh")
    public ResponseEntity<Resource> getCoverImage(
            @PathVariable Long id,
            @Parameter(description = "Kích thước: thumbnail, medium, large")
            @RequestParam(defaultValue = "large") String size) {
        BookResponseDto book = bookService.getBookById(id);
        Resource resource = coverImageService.loadCoverImage(book.getCoverPath(), size);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // WebP fallback to PNG
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .body(resource);
    }

    // ==================== Bulk Import API ====================

    /**
     * POST /api/books/bulk - Import sách hàng loạt từ Excel/CSV.
     */
    @PostMapping(value = "/bulk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import sách hàng loạt",
            description = "Upload file Excel/CSV kèm ảnh bìa (tùy chọn)")
    @ApiResponse(responseCode = "201", description = "Import thành công")
    public ResponseEntity<List<BookResponseDto>> bulkImport(
            @Parameter(description = "File Excel (.xlsx) hoặc CSV (.csv)")
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Danh sách ảnh bìa (khớp theo thứ tự dòng)")
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        List<BookResponseDto> result = bulkImportService.importBooks(file, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
