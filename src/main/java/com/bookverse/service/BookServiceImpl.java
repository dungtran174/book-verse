package com.bookverse.service;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.dto.PageResponseDto;
import com.bookverse.entity.Book;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation của BookService.
 * Xử lý toàn bộ nghiệp vụ CRUD, tìm kiếm, phân trang cho sách.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CoverImageService coverImageService;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<BookResponseDto> getAllBooks(int page, int size, String sort,
                                                        String category, Integer year) {
        Pageable pageable = createPageable(page, size, sort);

        // Lọc theo category và/hoặc year
        Page<Book> bookPage;
        if (category != null && year != null) {
            bookPage = bookRepository.findByCategoryIgnoreCaseAndYear(category, year, pageable);
        } else if (category != null) {
            bookPage = bookRepository.findByCategoryIgnoreCase(category, pageable);
        } else if (year != null) {
            bookPage = bookRepository.findByYear(year, pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        List<BookResponseDto> content = bookPage.getContent().stream()
                .map(this::toResponseWithCoverUrls)
                .toList();

        return buildPageResponse(content, bookPage);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "books", key = "#id")
    public BookResponseDto getBookById(Long id) {
        log.info("Lấy thông tin sách ID: {}", id);
        Book book = findBookOrThrow(id);
        return toResponseWithCoverUrls(book);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "books", allEntries = true),
            @CacheEvict(value = "searchResults", allEntries = true)
    })
    public BookResponseDto createBook(BookRequestDto requestDto, MultipartFile coverImage) {
        log.info("Tạo sách mới: {}", requestDto.getTitle());

        // Kiểm tra ISBN trùng
        if (requestDto.getIsbn() != null && bookRepository.existsByIsbn(requestDto.getIsbn())) {
            throw new IllegalArgumentException("ISBN '" + requestDto.getIsbn() + "' đã tồn tại trong hệ thống");
        }

        // Tạo entity từ DTO
        Book book = bookMapper.toEntity(requestDto);
        Book savedBook = bookRepository.save(book);

        // Xử lý ảnh bìa nếu có
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageService.validateImageFile(coverImage);
            String coverPath = coverImageService.processAndSaveCover(savedBook.getId(), coverImage);
            savedBook.setCoverPath(coverPath);
            savedBook = bookRepository.save(savedBook);
        }

        log.info("Tạo sách thành công, ID: {}", savedBook.getId());
        return toResponseWithCoverUrls(savedBook);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "books", key = "#id"),
            @CacheEvict(value = "searchResults", allEntries = true)
    })
    public BookResponseDto updateBook(Long id, BookRequestDto requestDto, MultipartFile coverImage) {
        log.info("Cập nhật sách ID: {}", id);
        Book book = findBookOrThrow(id);

        // Cập nhật các trường từ DTO (bỏ qua null)
        bookMapper.updateEntityFromDto(requestDto, book);

        // Xử lý ảnh bìa mới nếu có
        if (coverImage != null && !coverImage.isEmpty()) {
            coverImageService.validateImageFile(coverImage);

            // Xóa ảnh cũ nếu có
            if (book.getCoverPath() != null) {
                coverImageService.deleteCoverImages(book.getCoverPath());
            }

            String coverPath = coverImageService.processAndSaveCover(book.getId(), coverImage);
            book.setCoverPath(coverPath);
        }

        Book updatedBook = bookRepository.save(book);
        log.info("Cập nhật sách thành công, ID: {}", updatedBook.getId());
        return toResponseWithCoverUrls(updatedBook);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "books", key = "#id"),
            @CacheEvict(value = "searchResults", allEntries = true)
    })
    public void deleteBook(Long id) {
        log.info("Xóa sách ID: {}", id);
        Book book = findBookOrThrow(id);

        // Xóa ảnh bìa nếu có
        if (book.getCoverPath() != null) {
            coverImageService.deleteCoverImages(book.getCoverPath());
        }

        bookRepository.delete(book);
        log.info("Xóa sách thành công, ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "searchResults", key = "#query + '_' + #category + '_' + #page + '_' + #size + '_' + #sort")
    public PageResponseDto<BookResponseDto> searchBooks(String query, String category,
                                                         int page, int size, String sort) {
        log.info("Tìm kiếm sách: query='{}', category='{}'", query, category);
        Pageable pageable = createPageable(page, size, sort);

        Page<Book> bookPage;
        if (query != null && !query.isBlank() && category != null && !category.isBlank()) {
            bookPage = bookRepository.searchByKeywordAndCategory(query.trim(), category.trim(), pageable);
        } else if (query != null && !query.isBlank()) {
            bookPage = bookRepository.searchByKeyword(query.trim(), pageable);
        } else if (category != null && !category.isBlank()) {
            bookPage = bookRepository.findByCategoryIgnoreCase(category.trim(), pageable);
        } else {
            bookPage = bookRepository.findAll(pageable);
        }

        List<BookResponseDto> content = bookPage.getContent().stream()
                .map(this::toResponseWithCoverUrls)
                .toList();

        return buildPageResponse(content, bookPage);
    }

    // ==================== Helper Methods ====================

    /**
     * Tìm sách theo ID hoặc ném ResourceNotFoundException.
     */
    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sách", id));
    }

    /**
     * Chuyển Book entity sang ResponseDto kèm cover URLs.
     */
    private BookResponseDto toResponseWithCoverUrls(Book book) {
        BookResponseDto dto = bookMapper.toResponseDto(book);
        if (book.getCoverPath() != null) {
            Map<String, String> coverUrls = new HashMap<>();
            coverUrls.put("thumbnail", "/api/books/" + book.getId() + "/cover?size=thumbnail");
            coverUrls.put("medium", "/api/books/" + book.getId() + "/cover?size=medium");
            coverUrls.put("large", "/api/books/" + book.getId() + "/cover?size=large");
            dto.setCoverUrls(coverUrls);
        }
        return dto;
    }

    /**
     * Tạo Pageable từ tham số phân trang.
     * Hỗ trợ sort format: "field,direction" (ví dụ: "title,asc").
     */
    private Pageable createPageable(int page, int size, String sort) {
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, field));
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
    }

    /**
     * Xây dựng PageResponseDto từ Page object.
     */
    private <T> PageResponseDto<T> buildPageResponse(List<T> content, Page<?> pageData) {
        return PageResponseDto.<T>builder()
                .content(content)
                .page(pageData.getNumber())
                .size(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .first(pageData.isFirst())
                .last(pageData.isLast())
                .build();
    }
}
