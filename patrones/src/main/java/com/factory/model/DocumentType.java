package com.factory.model;

/**
 * Enum representing the types of sensitive documents handled by the system.
 * All document types require encryption due to their sensitive nature.
 */
public enum DocumentType {
    ELECTRONIC_INVOICE("Factura Electrónica"),
    LEGAL_CONTRACT("Contrato Legal"),
    FINANCIAL_REPORT("Reporte Financiero"),
    DIGITAL_CERTIFICATE("Certificado Digital"),
    TAX_DECLARATION("Declaración Tributaria");

    private final String displayName;

    DocumentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
