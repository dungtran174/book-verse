# BookVerse - Kiến Trúc Hệ Thống

## 1. Tổng Quan

**BookVerse** là hệ thống Web API + Web Application quản lý sách điện tử, được xây dựng trên nền tảng **Spring Boot 3** với kiến trúc **Layered Architecture**.

### Công nghệ sử dụng

| Thành phần | Công nghệ |
|---|---|
| Framework | Spring Boot 3.2.5 |
| Database | PostgreSQL 16 (prod) / H2 (dev) |
| ORM | Spring Data JPA + Hibernate |
| Mapping | MapStruct 1.5.5 |
| Cache | Caffeine Cache |
| API Docs | SpringDoc OpenAPI 3.0 (Swagger) |
| Image Processing | Thumbnailator + Scrimage |
| Excel/CSV | Apache POI + OpenCSV |
| Build | Maven |
| Container | Docker + Docker Compose |

---

## 2. Kiến Trúc Layered Architecture

```
┌─────────────────────────────────────────────────┐
│                   CLIENT                        │
│         (Frontend / Postman / Swagger)           │
└───────────────────┬─────────────────────────────┘
                    │ HTTP Request
                    ▼
┌─────────────────────────────────────────────────┐
│              CONTROLLER LAYER                   │
│         BookController.java                     │
│  - Nhận request, validate, trả response         │
│  - Swagger annotations                          │
└───────────────────┬─────────────────────────────┘
                    │ DTO
                    ▼
┌─────────────────────────────────────────────────┐
│               SERVICE LAYER                     │
│  BookServiceImpl / CoverImageServiceImpl        │
│  BulkImportServiceImpl                          │
│  - Xử lý business logic                        │
│  - Caching, transaction management              │
│  - Gọi Mapper để chuyển đổi DTO ↔ Entity       │
└───────────────────┬─────────────────────────────┘
                    │ Entity
                    ▼
┌─────────────────────────────────────────────────┐
│             REPOSITORY LAYER                    │
│         BookRepository.java                     │
│  - Truy vấn database qua Spring Data JPA       │
│  - Custom query cho full-text search            │
└───────────────────┬─────────────────────────────┘
                    │ SQL
                    ▼
┌─────────────────────────────────────────────────┐
│               DATABASE                          │
│     PostgreSQL (prod) / H2 (dev)                │
└─────────────────────────────────────────────────┘
```

---

## 3. Cấu Trúc Database

### Bảng `books`

| Cột | Kiểu | Ràng buộc | Mô tả |
|---|---|---|---|
| id | BIGSERIAL | PK, AUTO | ID tự tăng |
| title | VARCHAR(255) | NOT NULL | Tiêu đề sách |
| author | VARCHAR(255) | NOT NULL | Tên tác giả |
| isbn | VARCHAR(13) | UNIQUE | Mã ISBN |
| publication_year | INTEGER | | Năm xuất bản |
| category | VARCHAR(100) | | Thể loại |
| rating | DOUBLE | DEFAULT 0.0 | Đánh giá (0-5) |
| description | TEXT | | Mô tả chi tiết |
| cover_path | VARCHAR(500) | | Đường dẫn ảnh bìa |
| created_at | TIMESTAMP | AUTO | Ngày tạo |
| updated_at | TIMESTAMP | AUTO | Ngày cập nhật |

### Indexes
- `idx_books_category` → tối ưu lọc theo thể loại
- `idx_books_year` → tối ưu lọc theo năm
- `idx_books_title` → tối ưu tìm kiếm theo tên
- `idx_books_author` → tối ưu tìm kiếm theo tác giả

---

## 4. Chi Tiết API

### 4.1. GET /api/books — Danh sách sách (phân trang)

**Mô tả:** Lấy danh sách sách với phân trang, lọc, sắp xếp.

| Param | Kiểu | Mặc định | Mô tả |
|---|---|---|---|
| page | int | 0 | Số trang |
| size | int | 10 | Kích thước trang |
| sort | string | id,asc | Sắp xếp (title/year/rating,asc/desc) |
| category | string | | Lọc theo thể loại |
| year | int | | Lọc theo năm |

**Response 200:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Clean Code",
      "author": "Robert C. Martin",
      "isbn": "9780132350884",
      "year": 2008,
      "category": "Công nghệ",
      "rating": 4.6,
      "description": "...",
      "coverUrls": {
        "thumbnail": "/api/books/1/cover?size=thumbnail",
        "medium": "/api/books/1/cover?size=medium",
        "large": "/api/books/1/cover?size=large"
      },
      "createdAt": "2026-08-05T09:00:00",
      "updatedAt": "2026-08-05T09:00:00"
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 50,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

**Luồng xử lý:**
1. Controller nhận params → tạo `Pageable`
2. Service gọi Repository theo điều kiện lọc
3. Chuyển Page<Book> → PageResponseDto<BookResponseDto>
4. Gắn coverUrls cho mỗi BookResponseDto
5. Trả về response

---

### 4.2. GET /api/books/{id} — Chi tiết sách

**Response 200:** Trả về BookResponseDto đầy đủ.

**Response 404:**
```json
{
  "status": 404,
  "message": "Sách với ID 999 không tồn tại",
  "timestamp": "2026-08-05T09:00:00"
}
```

**Luồng:** Controller → Service (kiểm tra cache Caffeine → nếu miss thì query DB) → Mapper → Response.

---

### 4.3. POST /api/books — Tạo sách mới

**Content-Type:** `multipart/form-data`

| Part | Kiểu | Bắt buộc | Mô tả |
|---|---|---|---|
| book | JSON | ✅ | BookRequestDto |
| cover | File | ❌ | Ảnh bìa (JPG/PNG/WebP) |

**Luồng xử lý:**
1. Controller nhận multipart → validate BookRequestDto
2. Service kiểm tra ISBN trùng
3. Mapper chuyển DTO → Entity → lưu DB
4. Nếu có ảnh: validate → resize 3 kích thước → lưu file
5. Cập nhật coverPath vào Entity
6. Evict cache → trả response 201

---

### 4.4. PUT /api/books/{id} — Cập nhật sách

Tương tự POST, nhưng:
- Tìm Entity theo ID (404 nếu không có)
- Dùng MapStruct `updateEntityFromDto` (bỏ qua null)
- Nếu upload ảnh mới → xóa ảnh cũ trước

---

### 4.5. DELETE /api/books/{id} — Xóa sách

**Luồng:** Tìm Entity → xóa ảnh bìa nếu có → xóa Entity → evict cache → 204.

---

### 4.6. GET /api/books/search — Tìm kiếm

| Param | Kiểu | Mô tả |
|---|---|---|
| q | string | Từ khóa (tên sách / tác giả) |
| category | string | Lọc thể loại |
| page, size, sort | | Phân trang (như GET /api/books) |

**Luồng:** JPQL `LIKE` query trên title + author (case-insensitive) → kết hợp category filter → phân trang.

---

### 4.7. GET /api/books/{id}/cover — Ảnh bìa

| Param | Giá trị | Mô tả |
|---|---|---|
| size | thumbnail / medium / large | Kích thước ảnh |

**Luồng:** Lấy coverPath từ Book → xây dựng file path → trả Resource + Cache-Control header.

---

### 4.8. POST /api/books/bulk — Import hàng loạt

**Content-Type:** `multipart/form-data`

| Part | Kiểu | Mô tả |
|---|---|---|
| file | .xlsx / .csv | File dữ liệu sách |
| images | File[] | Ảnh bìa (theo thứ tự dòng) |

**Cột trong file:** title, author, isbn, year, category, rating, description

---

## 5. Xử Lý Ảnh Bìa

```
Upload (JPG/PNG/WebP)
        │
        ▼
  Validate (type, size)
        │
        ▼
  Đọc BufferedImage
        │
        ├──► Resize 200px  → {id}-thumbnail.webp
        ├──► Resize 500px  → {id}-medium.webp
        └──► Resize 1200px → {id}-large.webp
                │
                ▼
    Lưu tại: uploads/covers/yyyy/MM/
                │
                ▼
    coverPath = "covers/yyyy/MM/{id}"
```

**Quy tắc resize:** Giữ nguyên tỷ lệ, lấy cạnh dài nhất = target size.

---

## 6. Chiến Lược Caching

| Cache Name | TTL | Max Size | Mô tả |
|---|---|---|---|
| books | 5 phút | 500 | Chi tiết sách theo ID |
| searchResults | 2 phút | 200 | Kết quả tìm kiếm |
| coverImages | 30 phút | 1000 | Ảnh bìa |

**Eviction:** Tự động evict khi CREATE / UPDATE / DELETE sách.

---

## 7. Xử Lý Lỗi

| Exception | HTTP Status | Mô tả |
|---|---|---|
| ResourceNotFoundException | 404 | Không tìm thấy tài nguyên |
| MethodArgumentNotValidException | 400 | Validation thất bại |
| FileProcessingException | 500 | Lỗi xử lý file |
| MaxUploadSizeExceededException | 413 | File quá lớn |
| IllegalArgumentException | 400 | Tham số không hợp lệ |
| Exception | 500 | Lỗi không xác định |

Response lỗi chuẩn:
```json
{
  "status": 400,
  "message": "Dữ liệu đầu vào không hợp lệ",
  "timestamp": "2026-08-05T09:00:00",
  "errors": {
    "title": "Tiêu đề không được để trống",
    "rating": "Rating tối đa là 5.0"
  }
}
```

---

## 8. Cấu Trúc Thư Mục Upload

```
uploads/
└── covers/
    └── 2026/
        └── 08/
            ├── 1-thumbnail.webp
            ├── 1-medium.webp
            ├── 1-large.webp
            ├── 2-thumbnail.webp
            ├── 2-medium.webp
            └── 2-large.webp
```

---

## 9. Docker Deployment

```
docker-compose up -d
```

```
┌─────────────────┐     ┌─────────────────┐
│  bookverse-app  │────▶│  bookverse-db   │
│  (Spring Boot)  │     │  (PostgreSQL)   │
│  Port: 8080     │     │  Port: 5432     │
└─────────────────┘     └─────────────────┘
        │                       │
   upload_data             postgres_data
   (Volume)                (Volume)
```
