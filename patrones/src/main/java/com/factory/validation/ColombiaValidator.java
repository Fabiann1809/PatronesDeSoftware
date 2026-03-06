package com.factory.validation;

import com.factory.exception.ValidationException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;

/**
 * Validates documents according to Colombian regulations (DIAN).
 * 
 * Key regulations:
 * - NIT (Número de Identificación Tributaria) required for all documents
 * - Electronic invoices must have DIAN authorization resolution number
 * - Digital signatures required for tax declarations
 * - Facturación electrónica regulated by Resolución 000042 de 2020
 */
public class ColombiaValidator implements CountryValidator {

    private static final String NIT_PATTERN = "^\\d{9,10}-\\d$";
    private static final String DIAN_RESOLUTION_PATTERN = "^\\d{10,18}$";

    @Override
    public void validate(Document document) throws ValidationException {
        // Validate NIT (required for all documents in Colombia)
        validateNit(document);

        // Specific validations per document type
        switch (document.getType()) {
            case ELECTRONIC_INVOICE -> validateElectronicInvoice(document);
            case TAX_DECLARATION -> validateTaxDeclaration(document);
            case LEGAL_CONTRACT -> validateLegalContract(document);
            case FINANCIAL_REPORT -> validateFinancialReport(document);
            case DIGITAL_CERTIFICATE -> validateDigitalCertificate(document);
        }
    }

    private void validateNit(Document document) throws ValidationException {
        if (document.getTaxId() == null || document.getTaxId().isBlank()) {
            throw new ValidationException(
                    "El NIT (Número de Identificación Tributaria) es obligatorio para documentos en Colombia.",
                    Country.COLOMBIA, "NIT_REQUERIDO");
        }
        if (!document.getTaxId().matches(NIT_PATTERN)) {
            throw new ValidationException(
                    String.format("El NIT '%s' no tiene un formato válido. Formato esperado: 123456789-0",
                            document.getTaxId()),
                    Country.COLOMBIA, "NIT_FORMATO_INVALIDO");
        }
    }

    private void validateElectronicInvoice(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "La factura electrónica requiere un número de resolución de autorización DIAN.",
                    Country.COLOMBIA, "RESOLUCION_DIAN_REQUERIDA");
        }
        if (!document.getAuthorizationCode().matches(DIAN_RESOLUTION_PATTERN)) {
            throw new ValidationException(
                    String.format("El número de resolución DIAN '%s' no es válido.",
                            document.getAuthorizationCode()),
                    Country.COLOMBIA, "RESOLUCION_DIAN_INVALIDA");
        }
    }

    private void validateTaxDeclaration(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().length() < 10) {
            throw new ValidationException(
                    "La declaración tributaria debe contener información suficiente (mínimo 10 caracteres).",
                    Country.COLOMBIA, "DECLARACION_CONTENIDO_INSUFICIENTE");
        }
    }

    private void validateLegalContract(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El contrato legal no puede estar vacío.",
                    Country.COLOMBIA, "CONTRATO_VACIO");
        }
    }

    private void validateFinancialReport(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El reporte financiero debe contener información.",
                    Country.COLOMBIA, "REPORTE_VACIO");
        }
    }

    private void validateDigitalCertificate(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "El certificado digital requiere un código de autorización.",
                    Country.COLOMBIA, "CERTIFICADO_SIN_AUTORIZACION");
        }
    }

    @Override
    public String getRegulatoryBodyName() {
        return "DIAN (Dirección de Impuestos y Aduanas Nacionales)";
    }
}
