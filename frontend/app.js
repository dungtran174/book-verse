/**
 * BookVerse Frontend - JavaScript Application
 * Tương tác với REST API để quản lý sách.
 */

const API_BASE = 'http://localhost:8080/api/books';
let currentPage = 0;
let currentSize = 12;
let currentSort = 'title,asc';

// ==================== Khởi tạo ====================
document.addEventListener('DOMContentLoaded', () => {
    loadBooks();
});

// ==================== API Calls ====================

/** Tải danh sách sách với phân trang */
async function loadBooks() {
    hideForm();
    const category = document.getElementById('category-filter').value;
    let url = `${API_BASE}?page=${currentPage}&size=${currentSize}&sort=${currentSort}`;
    if (category) url += `&category=${encodeURIComponent(category)}`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        renderBooks(data);
        renderPagination(data);
    } catch (err) {
        showToast('Lỗi kết nối server: ' + err.message, 'error');
    }
}

/** Tìm kiếm sách */
async function searchBooks() {
    const q = document.getElementById('search-input').value.trim();
    const category = document.getElementById('category-filter').value;
    if (!q && !category) { loadBooks(); return; }

    let url = `${API_BASE}/search?page=${currentPage}&size=${currentSize}`;
    if (q) url += `&q=${encodeURIComponent(q)}`;
    if (category) url += `&category=${encodeURIComponent(category)}`;

    try {
        const res = await fetch(url);
        const data = await res.json();
        renderBooks(data);
        renderPagination(data);
    } catch (err) {
        showToast('Lỗi tìm kiếm: ' + err.message, 'error');
    }
}

/** Lấy chi tiết sách */
async function viewBook(id) {
    try {
        const res = await fetch(`${API_BASE}/${id}`);
        const book = await res.json();
        showBookDetail(book);
    } catch (err) {
        showToast('Lỗi tải chi tiết sách', 'error');
    }
}

/** Tạo hoặc cập nhật sách */
async function submitForm(event) {
    event.preventDefault();
    const bookId = document.getElementById('book-id').value;
    const isEdit = !!bookId;

    const bookData = {
        title: document.getElementById('title').value,
        author: document.getElementById('author').value,
        isbn: document.getElementById('isbn').value || null,
        year: document.getElementById('year').value ? parseInt(document.getElementById('year').value) : null,
        category: document.getElementById('category').value || null,
        rating: document.getElementById('rating').value ? parseFloat(document.getElementById('rating').value) : null,
        description: document.getElementById('description').value || null
    };

    const formData = new FormData();
    formData.append('book', new Blob([JSON.stringify(bookData)], { type: 'application/json' }));

    const coverFile = document.getElementById('cover').files[0];
    if (coverFile) {
        formData.append('cover', coverFile);
    }

    try {
        const url = isEdit ? `${API_BASE}/${bookId}` : API_BASE;
        const method = isEdit ? 'PUT' : 'POST';
        const res = await fetch(url, { method, body: formData });

        if (res.ok) {
            showToast(isEdit ? 'Cập nhật thành công!' : 'Thêm sách thành công!', 'success');
            hideForm();
            loadBooks();
        } else {
            const error = await res.json();
            showToast(error.message || 'Có lỗi xảy ra', 'error');
        }
    } catch (err) {
        showToast('Lỗi: ' + err.message, 'error');
    }
}

/** Xóa sách */
async function deleteBook(id) {
    if (!confirm('Bạn có chắc muốn xóa sách này?')) return;

    try {
        const res = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Xóa sách thành công!', 'success');
            closeModal();
            loadBooks();
        } else {
            showToast('Lỗi khi xóa sách', 'error');
        }
    } catch (err) {
        showToast('Lỗi: ' + err.message, 'error');
    }
}

// ==================== Render UI ====================

/** Render danh sách sách dạng grid */
function renderBooks(data) {
    const grid = document.getElementById('book-grid');
    if (!data.content || data.content.length === 0) {
        grid.innerHTML = '<p style="text-align:center;color:var(--text-secondary);grid-column:1/-1;padding:40px;">📭 Không tìm thấy sách nào.</p>';
        return;
    }

    grid.innerHTML = data.content.map(book => `
        <div class="book-card" onclick="viewBook(${book.id})">
            <div class="book-cover">
                ${book.coverUrls
                    ? `<img src="${API_BASE}/${book.id}/cover?size=medium" alt="${book.title}" onerror="this.parentElement.innerHTML='📖'">`
                    : '📖'}
            </div>
            <div class="book-info">
                <h3 title="${book.title}">${book.title}</h3>
                <p class="author">${book.author}</p>
                <div class="meta">
                    ${book.category ? `<span class="category">${book.category}</span>` : '<span></span>'}
                    <span class="rating">⭐ ${book.rating ? book.rating.toFixed(1) : 'N/A'}</span>
                </div>
            </div>
            <div class="book-actions">
                <button class="btn btn-sm btn-primary" onclick="event.stopPropagation(); editBook(${book.id})">✏️ Sửa</button>
                <button class="btn btn-sm btn-danger" onclick="event.stopPropagation(); deleteBook(${book.id})">🗑️ Xóa</button>
            </div>
        </div>
    `).join('');
}

/** Render pagination */
function renderPagination(data) {
    const div = document.getElementById('pagination');
    if (data.totalPages <= 1) { div.innerHTML = ''; return; }

    let html = `<button ${data.first ? 'disabled' : ''} onclick="goToPage(${data.page - 1})">◀ Trước</button>`;
    for (let i = 0; i < data.totalPages; i++) {
        html += `<button class="${i === data.page ? 'active' : ''}" onclick="goToPage(${i})">${i + 1}</button>`;
    }
    html += `<button ${data.last ? 'disabled' : ''} onclick="goToPage(${data.page + 1})">Sau ▶</button>`;
    div.innerHTML = html;
}

/** Hiển thị modal chi tiết sách */
function showBookDetail(book) {
    const body = document.getElementById('modal-body');
    body.innerHTML = `
        ${book.coverUrls
            ? `<img class="detail-cover" src="${API_BASE}/${book.id}/cover?size=large" alt="${book.title}" onerror="this.style.display='none'">`
            : ''}
        <div class="detail-info">
            <h2>${book.title}</h2>
            <p><span class="label">Tác giả:</span> ${book.author}</p>
            ${book.isbn ? `<p><span class="label">ISBN:</span> ${book.isbn}</p>` : ''}
            ${book.year ? `<p><span class="label">Năm:</span> ${book.year}</p>` : ''}
            ${book.category ? `<p><span class="label">Thể loại:</span> ${book.category}</p>` : ''}
            <p><span class="label">Rating:</span> ⭐ ${book.rating ? book.rating.toFixed(1) : 'Chưa đánh giá'}</p>
            ${book.description ? `<p><span class="label">Mô tả:</span> ${book.description}</p>` : ''}
            <p style="color:var(--text-secondary);font-size:0.85rem;margin-top:16px;">
                Tạo lúc: ${formatDate(book.createdAt)} | Cập nhật: ${formatDate(book.updatedAt)}
            </p>
        </div>
        <div style="margin-top:20px;display:flex;gap:10px;">
            <button class="btn btn-primary" onclick="closeModal(); editBook(${book.id})">✏️ Chỉnh sửa</button>
            <button class="btn btn-danger" onclick="deleteBook(${book.id})">🗑️ Xóa</button>
        </div>
    `;
    document.getElementById('modal').classList.remove('hidden');
}

// ==================== Form Helpers ====================

function showAddForm() {
    document.getElementById('form-title').textContent = 'Thêm Sách Mới';
    document.getElementById('book-id').value = '';
    document.getElementById('bookForm').reset();
    document.getElementById('book-form').classList.remove('hidden');
    document.getElementById('book-grid').classList.add('hidden');
    document.getElementById('pagination').classList.add('hidden');
}

async function editBook(id) {
    try {
        const res = await fetch(`${API_BASE}/${id}`);
        const book = await res.json();

        document.getElementById('form-title').textContent = 'Chỉnh Sửa Sách';
        document.getElementById('book-id').value = book.id;
        document.getElementById('title').value = book.title || '';
        document.getElementById('author').value = book.author || '';
        document.getElementById('isbn').value = book.isbn || '';
        document.getElementById('year').value = book.year || '';
        document.getElementById('category').value = book.category || '';
        document.getElementById('rating').value = book.rating || '';
        document.getElementById('description').value = book.description || '';

        document.getElementById('book-form').classList.remove('hidden');
        document.getElementById('book-grid').classList.add('hidden');
        document.getElementById('pagination').classList.add('hidden');
    } catch (err) {
        showToast('Lỗi tải thông tin sách', 'error');
    }
}

function hideForm() {
    document.getElementById('book-form').classList.add('hidden');
    document.getElementById('book-grid').classList.remove('hidden');
    document.getElementById('pagination').classList.remove('hidden');
}

// ==================== Utilities ====================

function goToPage(page) { currentPage = page; loadBooks(); }

function filterBooks() { currentPage = 0; loadBooks(); }

function handleSearch(e) { if (e.key === 'Enter') searchBooks(); }

function closeModal() { document.getElementById('modal').classList.add('hidden'); }

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleString('vi-VN');
}

function showToast(msg, type) {
    const toast = document.getElementById('toast');
    toast.textContent = msg;
    toast.className = `toast ${type}`;
    setTimeout(() => toast.classList.add('hidden'), 3000);
}
