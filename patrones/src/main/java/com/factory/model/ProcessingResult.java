package com.factory.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the result of processing a document.
 * Contains success/failure status and detailed error feedback in Spanish.
 */
public class ProcessingResult {

    private final Document document;
    private final boolean success;
    private final String message;
    private final LocalDateTime processedAt;

    private ProcessingResult(Document document, boolean success, String message) {
        this.document = document;
        this.success = success;
        this.message = message;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Creates a successful processing result.
     */
    public static ProcessingResult success(Document document, String message) {
        return new ProcessingResult(document, true, message);
    }

    /**
     * Creates a failed processing result with error feedback.
     */
    public static ProcessingResult failure(Document document, String errorMessage) {
        return new ProcessingResult(document, false, errorMessage);
    }

    // Getters
    public Document getDocument() { return document; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public LocalDateTime getProcessedAt() { return processedAt; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String status = success ? "✅ EXITOSO" : "❌ ERROR";
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════════════════════════════\n");
        sb.append(String.format("║ %s | Documento: %s\n", status, document.getId()));
        sb.append(String.format("║ Tipo: %s\n", document.getType().getDisplayName()));
        sb.append(String.format("║ País: %s (%s)\n", document.getCountry().getDisplayName(), document.getCountry().getRegulatoryBody()));
        sb.append(String.format("║ Encriptado: %s\n", document.isEncrypted() ? "Sí" : "No"));
        sb.append(String.format("║ Mensaje: %s\n", message));
        sb.append(String.format("║ Procesado: %s\n", processedAt.format(formatter)));
        sb.append("╚══════════════════════════════════════════════════════════════");
        return sb.toString();
    }
}
