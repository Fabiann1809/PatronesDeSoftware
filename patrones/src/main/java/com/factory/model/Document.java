package com.factory.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a business document to be processed by the system.
 * Contains metadata, content, and encryption state.
 */
public class Document {

    private final String id;
    private final DocumentType type;
    private final Country country;
    private String content;
    private boolean encrypted;
    private final LocalDateTime createdAt;
    private String taxId;          // NIT, RFC, CUIT, RUT depending on country
    private String authorizationCode; // Resolution, CFDI, CAE, DTE code

    public Document(DocumentType type, Country country, String content) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.type = type;
        this.country = country;
        this.content = content;
        this.encrypted = false;
        this.createdAt = LocalDateTime.now();
    }

    public Document(DocumentType type, Country country, String content, String taxId, String authorizationCode) {
        this(type, country, content);
        this.taxId = taxId;
        this.authorizationCode = authorizationCode;
    }

    // Getters
    public String getId() { return id; }
    public DocumentType getType() { return type; }
    public Country getCountry() { return country; }
    public String getContent() { return content; }
    public boolean isEncrypted() { return encrypted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getTaxId() { return taxId; }
    public String getAuthorizationCode() { return authorizationCode; }

    // Setters
    public void setContent(String content) { this.content = content; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return String.format("[%s] %s - %s | País: %s | Encriptado: %s | Fecha: %s",
                id, type.getDisplayName(), country.getDisplayName(),
                country.getIsoCode(),
                encrypted ? "Sí" : "No",
                createdAt.format(formatter));
    }
}
