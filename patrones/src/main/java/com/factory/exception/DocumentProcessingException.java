package com.factory.exception;

/**
 * Base exception for all document processing errors.
 * Provides descriptive error messages in Spanish for user feedback.
 */
public class DocumentProcessingException extends Exception {

    private final String errorCode;

    public DocumentProcessingException(String message) {
        super(message);
        this.errorCode = "PROC-000";
    }

    public DocumentProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DocumentProcessingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}
