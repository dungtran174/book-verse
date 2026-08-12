#  BookVerse - Hệ Thống Quản Lý Sách Điện Tử

Hệ thống Web API + Web Application quản lý sách điện tử được xây dựng bằng **Spring Boot 3**, hỗ trợ CRUD sách, upload/quản lý ảnh bìa, tìm kiếm full-text, và import hàng loạt.

##  Mục Lục

- [Công Nghệ](#-công-nghệ)
- [Cấu Trúc Thư Mục](#-cấu-trúc-thư-mục)
- [Hướng Dẫn Chạy Trên IntelliJ](#-hướng-dẫn-chạy-trên-intellij-idea)
- [API Endpoints](#-api-endpoints)
- [Chạy Bằng Docker](#-chạy-bằng-docker)
- [Chạy Unit Test](#-chạy-unit-test)

---

##  Công Nghệ

| Thành phần | Công nghệ |
|---|---|
| Backend | Spring Boot 3.2.5, Java 17 |
| Database | PostgreSQL 16 (prod) / H2 (dev) |
| ORM | Spring Data JPA |
| Mapping | MapStruct 1.5.5 |
| Validation | Jakarta Bean Validation |
| Cache | Caffeine |
| API Docs | SpringDoc OpenAPI 3 (Swagger) |
| Image | Thumbnailator + Scrimage |
| Import | Apache POI (Excel) + OpenCSV |
| Container | Docker + Docker Compose |
| Build | Maven |

---

##  Cấu Trúc Thư Mục

```
book-verse/
 .env                          # Biến môi trường
 .gitignore
 pom.xml                       # Maven dependencies
 Dockerfile                    # Multi-stage Docker build
 docker-compose.yml            # Docker Compose (app + PostgreSQL)
 README.md
 docs/
    architecture.md           # Kiến trúc hệ thống chi tiết
 frontend/
    index.html                # Giao diện web
    style.css                 # Styles
    app.js                    # JavaScript logic
 src/
     main/
        java/com/bookverse/
           BookVerseApplication.java    # Main class
           config/
              CacheConfig.java         # Caffeine cache
              OpenApiConfig.java       # Swagger
              WebConfig.java           # CORS
           controller/
              BookController.java      # REST API endpoints
           dto/
              BookRequestDto.java      # Input DTO + validation
              BookResponseDto.java     # Output DTO
              PageResponseDto.java     # Pagination wrapper
           entity/
              Book.java                # JPA entity
           exception/
              ErrorResponse.java       # Error DTO
              FileProcessingException.java
              GlobalExceptionHandler.java
              ResourceNotFoundException.java
           mapper/
              BookMapper.java          # MapStruct mapper
           repository/
              BookRepository.java      # JPA Repository
           service/
               BookService.java         # Interface
               BookServiceImpl.java     # CRUD + Search logic
               BulkImportService.java   # Interface
               BulkImportServiceImpl.java # Excel/CSV import
               CoverImageService.java   # Interface
               CoverImageServiceImpl.java # Image resize/save
        resources/
            application.yml              # Config chung
            application-dev.yml          # H2 database
            application-prod.yml         # PostgreSQL
            data.sql                     # Dữ liệu mẫu
     test/java/com/bookverse/
         BookVerseApplicationTests.java
         service/
             BookServiceTest.java         # Unit test CRUD
             CoverImageServiceTest.java   # Unit test image
```

---

##  Hướng Dẫn Chạy Trên IntelliJ IDEA

### Yêu cầu hệ thống
- **Java JDK 17** trở lên
- **IntelliJ IDEA** (Community hoặc Ultimate)
- **Maven** (IntelliJ tích hợp sẵn)

### Bước 1: Mở Project

1. Mở IntelliJ IDEA
2. Chọn **File → Open**
3. Chọn thư mục `book-verse` → Click **OK**
4. IntelliJ sẽ tự nhận diện Maven project và hiển thị popup **"Maven project detected"** → Click **Load**
5. Đợi IntelliJ tải xong dependencies (xem thanh progress bar ở góc dưới phải)

### Bước 2: Cấu hình Environment Variables

1. Copy file `.env` mẫu (đã có sẵn trong project)
2. Mở file `.env` và chỉnh sửa nếu cần:
   ```
   SPRING_PROFILES_ACTIVE=dev    # Dùng H2 database (không cần cài DB)
   SERVER_PORT=8080
   ```

### Bước 3: Cài đặt Lombok Plugin (nếu chưa có)

1. Vào **File → Settings → Plugins**
2. Tìm **Lombok** → Click **Install**
3. Restart IntelliJ
4. Vào **File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors**
5. Tick  **Enable annotation processing**

### Bước 4: Cấu hình Run Configuration

1. Mở file `BookVerseApplication.java`
2. Click biểu tượng ️ (Run) bên cạnh `public static void main`
3. Chọn **Modify Run Configuration...**
4. Trong tab **Environment variables**, thêm:
   ```
   SPRING_PROFILES_ACTIVE=dev
   ```
   Hoặc đơn giản hơn: trong tab **Active profiles** (nếu dùng Ultimate), nhập: `dev`
5. Click **Apply → OK**

### Bước 5: Chạy Ứng Dụng

1. Click ️ **Run** hoặc nhấn `Shift + F10`
2. Đợi console hiển thị:
   ```
   Started BookVerseApplication in X.XX seconds
   ```
3. Ứng dụng đã sẵn sàng!

### Bước 6: Truy Cập

| URL | Mô tả |
|---|---|
| http://localhost:8080/swagger-ui.html | **Swagger UI** - test API |
| http://localhost:8080/h2-console | **H2 Console** (JDBC URL: `jdbc:h2:mem:bookverse`) |
| http://localhost:8080/api/books | API lấy danh sách sách |
| Mở file `frontend/index.html` | **Frontend** giao diện web |

### Lưu ý khi dùng H2 Console
- **JDBC URL:** `jdbc:h2:mem:bookverse`
- **Username:** `sa`
- **Password:** *(để trống)*

---

##  API Endpoints

| Method | Endpoint | Mô tả |
|---|---|---|
| GET | `/api/books?page=&size=&sort=` | Danh sách sách (phân trang) |
| GET | `/api/books/{id}` | Chi tiết sách |
| POST | `/api/books` | Tạo sách mới (multipart) |
| PUT | `/api/books/{id}` | Cập nhật sách |
| DELETE | `/api/books/{id}` | Xóa sách |
| GET | `/api/books/search?q=&category=` | Tìm kiếm full-text |
| GET | `/api/books/{id}/cover?size=large` | Lấy ảnh bìa |
| POST | `/api/books/bulk` | Import từ Excel/CSV |

### Ví dụ tạo sách bằng cURL

```bash
curl -X POST http://localhost:8080/api/books \
  -F 'book={"title":"Clean Code","author":"Robert Martin","isbn":"9780132350884","year":2008,"category":"Công nghệ","rating":4.6};type=application/json' \
  -F 'cover=@/path/to/cover.jpg'
```

### Ví dụ tìm kiếm

```bash
curl "http://localhost:8080/api/books/search?q=Clean&category=Công+nghệ&page=0&size=10&sort=rating,desc"
```

---

##  Chạy Bằng Docker

```bash
# Build và chạy tất cả services
docker-compose up -d

# Xem logs
docker-compose logs -f app

# Dừng
docker-compose down
```

Ứng dụng sẽ chạy ở `http://localhost:8080` với PostgreSQL database.

---

## 🧪 Chạy Unit Test

### Trên IntelliJ
1. Click chuột phải vào thư mục `src/test`
2. Chọn **Run 'All Tests'**

### Trên Terminal
```bash
mvn test
```

---

##  Tài Liệu Bổ Sung

- [Kiến trúc hệ thống chi tiết](docs/architecture.md) - Phân tích bài toán, luồng hoạt động, API specs
- [Swagger UI](http://localhost:8080/swagger-ui.html) - Interactive API documentation
