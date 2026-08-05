package com.bookverse.exception;

/**
 * Exception khi xử lý file gặp lỗi (upload, resize, convert ảnh).
 * Trả về HTTP 500 Internal Server Error.
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
