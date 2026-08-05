package com.bookverse.repository;

import com.bookverse.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository cho Book entity.
 * Kế thừa JpaSpecificationExecutor để hỗ trợ dynamic query (lọc, tìm kiếm).
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /**
     * Tìm kiếm full-text theo tiêu đề và tác giả (case-insensitive).
     * Hỗ trợ cả H2 và PostgreSQL.
     *
     * @param keyword từ khóa tìm kiếm
     * @param pageable thông tin phân trang
     * @return danh sách sách khớp với từ khóa
     */
    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tìm kiếm kết hợp từ khóa và thể loại.
     *
     * @param keyword  từ khóa tìm kiếm (tên sách hoặc tác giả)
     * @param category thể loại sách
     * @param pageable thông tin phân trang
     * @return danh sách sách khớp điều kiện
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND LOWER(b.category) = LOWER(:category)")
    Page<Book> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable);

    /**
     * Lọc sách theo thể loại.
     *
     * @param category thể loại sách
     * @param pageable thông tin phân trang
     * @return danh sách sách thuộc thể loại
     */
    Page<Book> findByCategoryIgnoreCase(String category, Pageable pageable);

    /**
     * Lọc sách theo năm xuất bản.
     *
     * @param year     năm xuất bản
     * @param pageable thông tin phân trang
     * @return danh sách sách theo năm
     */
    Page<Book> findByYear(Integer year, Pageable pageable);

    /**
     * Lọc sách theo thể loại và năm xuất bản.
     *
     * @param category thể loại sách
     * @param year     năm xuất bản
     * @param pageable thông tin phân trang
     * @return danh sách sách theo thể loại và năm
     */
    Page<Book> findByCategoryIgnoreCaseAndYear(String category, Integer year, Pageable pageable);

    /**
     * Kiểm tra ISBN đã tồn tại chưa.
     *
     * @param isbn mã ISBN
     * @return true nếu ISBN đã tồn tại
     */
    boolean existsByIsbn(String isbn);
}
