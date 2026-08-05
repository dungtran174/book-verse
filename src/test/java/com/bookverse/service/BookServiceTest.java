package com.bookverse.service;

import com.bookverse.dto.BookRequestDto;
import com.bookverse.dto.BookResponseDto;
import com.bookverse.dto.PageResponseDto;
import com.bookverse.entity.Book;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.mapper.BookMapper;
import com.bookverse.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests cho BookServiceImpl.
 * Sử dụng Mockito để mock các dependency.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Unit Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private CoverImageService coverImageService;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book sampleBook;
    private BookRequestDto sampleRequest;
    private BookResponseDto sampleResponse;

    @BeforeEach
    void setUp() {
        sampleBook = Book.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .year(2008)
                .category("Công nghệ")
                .rating(4.6)
                .description("Hướng dẫn viết code sạch")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequest = BookRequestDto.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .year(2008)
                .category("Công nghệ")
                .rating(4.6)
                .description("Hướng dẫn viết code sạch")
                .build();

        sampleResponse = BookResponseDto.builder()
                .id(1L)
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("9780132350884")
                .year(2008)
                .category("Công nghệ")
                .rating(4.6)
                .description("Hướng dẫn viết code sạch")
                .build();
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("Lấy sách theo ID thành công")
    void getBookById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookMapper.toResponseDto(sampleBook)).thenReturn(sampleResponse);

        BookResponseDto result = bookService.getBookById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getAuthor()).isEqualTo("Robert C. Martin");
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Lấy sách theo ID - không tồn tại → ném exception")
    void getBookById_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("Tạo sách mới thành công (không có ảnh)")
    void createBook_Success_NoImage() {
        when(bookMapper.toEntity(sampleRequest)).thenReturn(sampleBook);
        when(bookRepository.save(sampleBook)).thenReturn(sampleBook);
        when(bookMapper.toResponseDto(sampleBook)).thenReturn(sampleResponse);
        when(bookRepository.existsByIsbn(anyString())).thenReturn(false);

        BookResponseDto result = bookService.createBook(sampleRequest, null);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(bookRepository).save(sampleBook);
    }

    @Test
    @DisplayName("Tạo sách với ISBN trùng → ném exception")
    void createBook_DuplicateIsbn() {
        when(bookRepository.existsByIsbn("9780132350884")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(sampleRequest, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ISBN");
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("Cập nhật sách thành công")
    void updateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(sampleBook)).thenReturn(sampleBook);
        when(bookMapper.toResponseDto(sampleBook)).thenReturn(sampleResponse);

        BookResponseDto result = bookService.updateBook(1L, sampleRequest, null);

        assertThat(result).isNotNull();
        verify(bookMapper).updateEntityFromDto(sampleRequest, sampleBook);
        verify(bookRepository).save(sampleBook);
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("Xóa sách thành công")
    void deleteBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        bookService.deleteBook(1L);

        verify(bookRepository).delete(sampleBook);
    }

    @Test
    @DisplayName("Xóa sách không tồn tại → ném exception")
    void deleteBook_NotFound() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("Lấy danh sách sách có phân trang")
    void getAllBooks_WithPagination() {
        Page<Book> bookPage = new PageImpl<>(List.of(sampleBook));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toResponseDto(sampleBook)).thenReturn(sampleResponse);

        PageResponseDto<BookResponseDto> result = bookService.getAllBooks(0, 10, null, null, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // ==================== SEARCH ====================

    @Test
    @DisplayName("Tìm kiếm sách theo từ khóa")
    void searchBooks_ByKeyword() {
        Page<Book> bookPage = new PageImpl<>(List.of(sampleBook));
        when(bookRepository.searchByKeyword(eq("Clean"), any(Pageable.class))).thenReturn(bookPage);
        when(bookMapper.toResponseDto(sampleBook)).thenReturn(sampleResponse);

        PageResponseDto<BookResponseDto> result = bookService.searchBooks("Clean", null, 0, 10, null);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Clean Code");
    }
}
