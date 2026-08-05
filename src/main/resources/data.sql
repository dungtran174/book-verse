-- ============================================
-- BookVerse - Dữ liệu mẫu (chỉ dùng cho dev profile)
-- ============================================

INSERT INTO books (title, author, isbn, publication_year, category, rating, description, created_at, updated_at)
VALUES
    ('Nhà Giả Kim', 'Paulo Coelho', '9780062315007', 1988, 'Tiểu thuyết', 4.5,
     'Câu chuyện về chàng chăn cừu Santiago trong hành trình tìm kiếm kho báu và khám phá bản thân.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Đắc Nhân Tâm', 'Dale Carnegie', '9780671027032', 1936, 'Phát triển bản thân', 4.7,
     'Cuốn sách kinh điển về nghệ thuật giao tiếp và cách đối nhân xử thế.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Clean Code', 'Robert C. Martin', '9780132350884', 2008, 'Công nghệ', 4.6,
     'Hướng dẫn viết code sạch, dễ đọc và dễ bảo trì cho lập trình viên chuyên nghiệp.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Design Patterns', 'Gang of Four', '9780201633610', 1994, 'Công nghệ', 4.3,
     'Cuốn sách kinh điển về các mẫu thiết kế phần mềm hướng đối tượng.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', '9780062316097', 2011, 'Khoa học', 4.8,
     'Khám phá lịch sử phát triển của loài người từ thời tiền sử đến hiện đại.',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
