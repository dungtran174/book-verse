package com.bookverse.service;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.dto.PageResponseDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interface định nghĩa các nghiệp vụ quản lý sách.
 */
public interface BookService {

    /**
     * Lấy danh sách sách có phân trang, hỗ trợ lọc theo thể loại và năm.
     *
     * @param page     số trang (bắt đầu từ 0)
     * @param size     kích thước mỗi trang
     * @param sort     trường sắp xếp (title, year, rating)
     * @param category thể loại (tùy chọn)
     * @param year     năm xuất bản (tùy chọn)
     * @return danh sách sách phân trang
     */
    PageResponseDto<BookResponseDto> getAllBooks(int page, int size, String sort,
                                                 String category, Integer year);

    /**
     * Lấy chi tiết một cuốn sách theo ID.
     *
     * @param id ID của sách
     * @return thông tin chi tiết sách
     */
    BookResponseDto getBookById(Long id);

    /**
     * Tạo mới một cuốn sách, kèm ảnh bìa (tùy chọn).
     *
     * @param requestDto thông tin sách
     * @param coverImage file ảnh bìa (có thể null)
     * @return sách vừa tạo
     */
    BookResponseDto createBook(BookRequestDto requestDto, MultipartFile coverImage);

    /**
     * Cập nhật thông tin sách theo ID, có thể cập nhật cả ảnh bìa.
     *
     * @param id         ID của sách cần cập nhật
     * @param requestDto thông tin sách mới
     * @param coverImage file ảnh bìa mới (có thể null)
     * @return sách sau khi cập nhật
     */
    BookResponseDto updateBook(Long id, BookRequestDto requestDto, MultipartFile coverImage);

    /**
     * Xóa sách theo ID, bao gồm cả ảnh bìa liên quan.
     *
     * @param id ID của sách cần xóa
     */
    void deleteBook(Long id);

    /**
     * Tìm kiếm sách theo từ khóa (tên sách, tác giả) và thể loại.
     *
     * @param query    từ khóa tìm kiếm
     * @param category thể loại (tùy chọn)
     * @param page     số trang
     * @param size     kích thước trang
     * @param sort     trường sắp xếp
     * @return kết quả tìm kiếm phân trang
     */
    PageResponseDto<BookResponseDto> searchBooks(String query, String category,
                                                  int page, int size, String sort);
}
