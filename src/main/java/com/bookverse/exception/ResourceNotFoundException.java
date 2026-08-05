package com.bookverse.exception;

/**
 * Exception khi không tìm thấy tài nguyên (sách, ảnh bìa, ...).
 * Trả về HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s với ID %d không tồn tại", resourceName, id));
    }
}
