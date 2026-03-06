package com.factory.validation;

import com.factory.exception.ValidationException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;

/**
 * Validates documents according to Argentine regulations (AFIP).
 * 
 * Key regulations:
 * - CUIT (Clave Única de Identificación Tributaria) required for all documents
 * - Electronic invoices must have CAE (Código de Autorización Electrónica)
 * - Regulated by AFIP (Administración Federal de Ingresos Públicos)
 * - Factura electrónica compliance per RG 4291
 */
public class ArgentinaValidator implements CountryValidator {

    private static final String CUIT_PATTERN = "^(20|23|24|25|26|27|30|33|34)\\d{8}\\d$";
    private static final String CAE_PATTERN = "^\\d{14}$";

    @Override
    public void validate(Document document) throws ValidationException {
        // Validate CUIT (required for all documents in Argentina)
        validateCuit(document);

        // Specific validations per document type
        switch (document.getType()) {
            case ELECTRONIC_INVOICE -> validateElectronicInvoice(document);
            case TAX_DECLARATION -> validateTaxDeclaration(document);
            case LEGAL_CONTRACT -> validateLegalContract(document);
            case FINANCIAL_REPORT -> validateFinancialReport(document);
            case DIGITAL_CERTIFICATE -> validateDigitalCertificate(document);
        }
    }

    private void validateCuit(Document document) throws ValidationException {
        if (document.getTaxId() == null || document.getTaxId().isBlank()) {
            throw new ValidationException(
                    "El CUIT (Clave Única de Identificación Tributaria) es obligatorio para documentos en Argentina.",
                    Country.ARGENTINA, "CUIT_REQUERIDO");
        }
        if (!document.getTaxId().matches(CUIT_PATTERN)) {
            throw new ValidationException(
                    String.format("El CUIT '%s' no tiene un formato válido. Formato esperado: 20123456789",
                            document.getTaxId()),
                    Country.ARGENTINA, "CUIT_FORMATO_INVALIDO");
        }
    }

    private void validateElectronicInvoice(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "La factura electrónica requiere un CAE (Código de Autorización Electrónica) de AFIP.",
                    Country.ARGENTINA, "CAE_REQUERIDO");
        }
        if (!document.getAuthorizationCode().matches(CAE_PATTERN)) {
            throw new ValidationException(
                    String.format("El CAE '%s' no tiene un formato válido. Se esperan 14 dígitos.",
                            document.getAuthorizationCode()),
                    Country.ARGENTINA, "CAE_FORMATO_INVALIDO");
        }
    }

    private void validateTaxDeclaration(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().length() < 10) {
            throw new ValidationException(
                    "La declaración tributaria debe contener información suficiente para AFIP.",
                    Country.ARGENTINA, "DECLARACION_CONTENIDO_INSUFICIENTE");
        }
    }

    private void validateLegalContract(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El contrato legal no puede estar vacío según regulaciones argentinas.",
                    Country.ARGENTINA, "CONTRATO_VACIO");
        }
    }

    private void validateFinancialReport(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El reporte financiero debe contener datos según normativa de AFIP.",
                    Country.ARGENTINA, "REPORTE_VACIO");
        }
    }

    private void validateDigitalCertificate(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "El certificado digital requiere código de autorización de AFIP.",
                    Country.ARGENTINA, "CERTIFICADO_SIN_AUTORIZACION");
        }
    }

    @Override
    public String getRegulatoryBodyName() {
        return "AFIP (Administración Federal de Ingresos Públicos)";
    }
}
