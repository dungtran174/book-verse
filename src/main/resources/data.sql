-- ============================================
-- BookVerse - Dữ liệu mẫu (chỉ dùng cho dev profile)
-- ============================================

INSERT INTO books (title, author, isbn, publication_year, category, rating, description, created_at, updated_at)
VALUES
    ('Nhà Giả Kim', 'Paulo Coelho', '9780062315007', 1988, 'Tiểu thuyết', 4.5, 'Câu chuyện về chàng chăn cừu Santiago trong hành trình tìm kiếm kho báu và khám phá bản thân.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Đắc Nhân Tâm', 'Dale Carnegie', '9780671027032', 1936, 'Phát triển bản thân', 4.7, 'Cuốn sách kinh điển về nghệ thuật giao tiếp và cách đối nhân xử thế.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Clean Code', 'Robert C. Martin', '9780132350884', 2008, 'Công nghệ', 4.6, 'Hướng dẫn viết code sạch, dễ đọc và dễ bảo trì cho lập trình viên chuyên nghiệp.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Design Patterns', 'Gang of Four', '9780201633610', 1994, 'Công nghệ', 4.3, 'Cuốn sách kinh điển về các mẫu thiết kế phần mềm hướng đối tượng.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Sapiens: Lược Sử Loài Người', 'Yuval Noah Harari', '9780062316097', 2011, 'Khoa học', 4.8, 'Khám phá lịch sử phát triển của loài người từ thời tiền sử đến hiện đại.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Dune (Xứ Cát)', 'Frank Herbert', '9780441172719', 1965, 'Khoa học', 4.9, 'Kiệt tác khoa học viễn tưởng về hành tinh Arrakis và hương dược quý giá.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Bố Già', 'Mario Puzo', '9780451205766', 1969, 'Tiểu thuyết', 4.8, 'Câu chuyện kinh điển về thế giới ngầm mafia tại Mỹ.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Suối Nguồn', 'Ayn Rand', '9780451191151', 1943, 'Tiểu thuyết', 4.5, 'Một tác phẩm đề cao chủ nghĩa cá nhân và sự sáng tạo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling', '9780747532699', 1997, 'Tiểu thuyết', 4.9, 'Tập đầu tiên trong loạt truyện ma thuật kinh điển về cậu bé phù thủy.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Đọc Vị Bất Kỳ Ai', 'David J. Lieberman', '9780978924623', 2007, 'Phát triển bản thân', 4.2, 'Các kỹ thuật tâm lý giúp thấu hiểu hành vi của người khác.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Tội Ác Và Hình Phạt', 'Fyodor Dostoevsky', '9780486415871', 1866, 'Tiểu thuyết', 4.7, 'Kiệt tác tâm lý học về tội ác, sự cắn rứt lương tâm và sự cứu rỗi.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Muôn Kiếp Nhân Sinh', 'Nguyên Phong', '9786043236021', 2020, 'Phát triển bản thân', 4.6, 'Những câu chuyện kỳ lạ về tiền kiếp và luân hồi.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Gatsby Vĩ Đại', 'F. Scott Fitzgerald', '9780743273565', 1925, 'Tiểu thuyết', 4.4, 'Bức tranh về Giấc mơ Mỹ phồn hoa nhưng sụp đổ trong thập niên 1920.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Lược Sử Thời Gian', 'Stephen Hawking', '9780553380163', 1988, 'Khoa học', 4.5, 'Cuốn sách đưa những khái niệm vật lý phức tạp đến gần hơn với đại chúng.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Nghĩ Giàu Làm Giàu', 'Napoleon Hill', '9781585424337', 1937, 'Phát triển bản thân', 4.6, 'Những nguyên tắc vượt thời gian để đạt được thành công và giàu có.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Cây Cam Ngọt Của Tôi', 'José Mauro de Vasconcelos', '9780143135808', 1968, 'Tiểu thuyết', 4.7, 'Câu chuyện cảm động về chú bé Zezé trí tưởng tượng phong phú và cây cam nhỏ.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Tôi Thấy Hoa Vàng Trên Cỏ Xanh', 'Nguyễn Nhật Ánh', '9786045610812', 2010, 'Tiểu thuyết', 4.8, 'Hồi ức tuổi thơ êm đềm và trong trẻo nơi làng quê Việt Nam.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Kafka Bên Bờ Biển', 'Haruki Murakami', '9781400079278', 2002, 'Tiểu thuyết', 4.5, 'Hành trình kỳ ảo của cậu bé Kafka bỏ nhà đi bụi và lão già Nakata.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Kẻ Trộm Sách', 'Markus Zusak', '9780375842207', 2005, 'Tiểu thuyết', 4.8, 'Câu chuyện về chiến tranh và tình yêu qua góc nhìn của Thần Chết.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Mật Mã Da Vinci', 'Dan Brown', '9780307277671', 2003, 'Tiểu thuyết', 4.3, 'Cuộc phiêu lưu giải mã các bí ẩn tôn giáo của giáo sư Langdon.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thiết Kế CSDL Tối Ưu', 'Martin Kleppmann', '9781449373320', 2017, 'Công nghệ', 4.9, 'Designing Data-Intensive Applications - Sách gối đầu giường của kỹ sư Data.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Hai Số Phận', 'Jeffrey Archer', '9781250034250', 1979, 'Tiểu thuyết', 4.7, 'Cuộc đời đối lập và sự kình địch giữa hai người đàn ông sinh cùng một ngày.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Head First Design Patterns', 'Eric Freeman', '9780596007126', 2004, 'Công nghệ', 4.6, 'Học Design Patterns một cách trực quan, vui nhộn và dễ hiểu.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Những Người Khốn Khổ', 'Victor Hugo', '9780451419439', 1862, 'Tiểu thuyết', 4.8, 'Bản thiên anh hùng ca về tình người và khao khát công lý trong xã hội Pháp.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Steve Jobs', 'Walter Isaacson', '9781451648539', 2011, 'Khoa học', 4.6, 'Tiểu sử chân thực và toàn diện về vị CEO huyền thoại của Apple.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Nghệ Thuật Tinh Tế Của Việc Đếch Quan Tâm', 'Mark Manson', '9780062457714', 2016, 'Phát triển bản thân', 4.4, 'Cách tiếp cận khác biệt để có một cuộc sống ý nghĩa, chấp nhận sự không hoàn hảo.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
