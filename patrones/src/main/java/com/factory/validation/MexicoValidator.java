package com.factory.validation;

import com.factory.exception.ValidationException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;

/**
 * Validates documents according to Mexican regulations (SAT).
 * 
 * Key regulations:
 * - RFC (Registro Federal de Contribuyentes) required for all documents
 * - Electronic invoices must have CFDI UUID
 * - Validated against SAT requirements (Servicio de Administración Tributaria)
 * - CFDI 4.0 compliance for electronic invoices
 */
public class MexicoValidator implements CountryValidator {

    private static final String RFC_PATTERN = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$";
    private static final String CFDI_UUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    @Override
    public void validate(Document document) throws ValidationException {
        // Validate RFC (required for all documents in Mexico)
        validateRfc(document);

        // Specific validations per document type
        switch (document.getType()) {
            case ELECTRONIC_INVOICE -> validateElectronicInvoice(document);
            case TAX_DECLARATION -> validateTaxDeclaration(document);
            case LEGAL_CONTRACT -> validateLegalContract(document);
            case FINANCIAL_REPORT -> validateFinancialReport(document);
            case DIGITAL_CERTIFICATE -> validateDigitalCertificate(document);
        }
    }

    private void validateRfc(Document document) throws ValidationException {
        if (document.getTaxId() == null || document.getTaxId().isBlank()) {
            throw new ValidationException(
                    "El RFC (Registro Federal de Contribuyentes) es obligatorio para documentos en México.",
                    Country.MEXICO, "RFC_REQUERIDO");
        }
        if (!document.getTaxId().matches(RFC_PATTERN)) {
            throw new ValidationException(
                    String.format("El RFC '%s' no tiene un formato válido. Formato esperado: XAXX010101000",
                            document.getTaxId()),
                    Country.MEXICO, "RFC_FORMATO_INVALIDO");
        }
    }

    private void validateElectronicInvoice(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "La factura electrónica (CFDI) requiere un UUID de folio fiscal.",
                    Country.MEXICO, "CFDI_UUID_REQUERIDO");
        }
        if (!document.getAuthorizationCode().matches(CFDI_UUID_PATTERN)) {
            throw new ValidationException(
                    String.format("El UUID del CFDI '%s' no tiene un formato válido.",
                            document.getAuthorizationCode()),
                    Country.MEXICO, "CFDI_UUID_INVALIDO");
        }
    }

    private void validateTaxDeclaration(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().length() < 10) {
            throw new ValidationException(
                    "La declaración tributaria debe contener información suficiente para el SAT.",
                    Country.MEXICO, "DECLARACION_CONTENIDO_INSUFICIENTE");
        }
    }

    private void validateLegalContract(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El contrato legal no puede estar vacío según regulaciones mexicanas.",
                    Country.MEXICO, "CONTRATO_VACIO");
        }
    }

    private void validateFinancialReport(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El reporte financiero debe contener información para cumplir con normativa del SAT.",
                    Country.MEXICO, "REPORTE_VACIO");
        }
    }

    private void validateDigitalCertificate(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "El certificado digital requiere el sello digital del SAT.",
                    Country.MEXICO, "SELLO_DIGITAL_REQUERIDO");
        }
    }

    @Override
    public String getRegulatoryBodyName() {
        return "SAT (Servicio de Administración Tributaria)";
    }
}
