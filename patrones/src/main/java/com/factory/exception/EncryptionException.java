package com.factory.exception;

/**
 * Exception thrown when document encryption fails.
 */
public class EncryptionException extends DocumentProcessingException {

    public EncryptionException(String message) {
        super(message, "ENC-001");
    }

    public EncryptionException(String message, Throwable cause) {
        super(message, "ENC-001", cause);
    }
}
