package com.factory.validation;

import com.factory.exception.ValidationException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;

/**
 * Validates documents according to Chilean regulations (SII).
 * 
 * Key regulations:
 * - RUT (Rol Único Tributario) required for all documents
 * - Electronic invoices must have DTE (Documento Tributario Electrónico) folio
 * - Timbre electrónico SII required for electronic invoices
 * - Regulated by SII (Servicio de Impuestos Internos)
 */
public class ChileValidator implements CountryValidator {

    private static final String RUT_PATTERN = "^\\d{7,8}-[\\dkK]$";
    private static final String DTE_FOLIO_PATTERN = "^\\d{1,10}$";

    @Override
    public void validate(Document document) throws ValidationException {
        // Validate RUT (required for all documents in Chile)
        validateRut(document);

        // Specific validations per document type
        switch (document.getType()) {
            case ELECTRONIC_INVOICE -> validateElectronicInvoice(document);
            case TAX_DECLARATION -> validateTaxDeclaration(document);
            case LEGAL_CONTRACT -> validateLegalContract(document);
            case FINANCIAL_REPORT -> validateFinancialReport(document);
            case DIGITAL_CERTIFICATE -> validateDigitalCertificate(document);
        }
    }

    private void validateRut(Document document) throws ValidationException {
        if (document.getTaxId() == null || document.getTaxId().isBlank()) {
            throw new ValidationException(
                    "El RUT (Rol Único Tributario) es obligatorio para documentos en Chile.",
                    Country.CHILE, "RUT_REQUERIDO");
        }
        if (!document.getTaxId().matches(RUT_PATTERN)) {
            throw new ValidationException(
                    String.format("El RUT '%s' no tiene un formato válido. Formato esperado: 12345678-9",
                            document.getTaxId()),
                    Country.CHILE, "RUT_FORMATO_INVALIDO");
        }
    }

    private void validateElectronicInvoice(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "La factura electrónica (DTE) requiere un número de folio autorizado por el SII.",
                    Country.CHILE, "DTE_FOLIO_REQUERIDO");
        }
        if (!document.getAuthorizationCode().matches(DTE_FOLIO_PATTERN)) {
            throw new ValidationException(
                    String.format("El folio DTE '%s' no tiene un formato válido.",
                            document.getAuthorizationCode()),
                    Country.CHILE, "DTE_FOLIO_INVALIDO");
        }
    }

    private void validateTaxDeclaration(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().length() < 10) {
            throw new ValidationException(
                    "La declaración tributaria debe contener información suficiente para el SII.",
                    Country.CHILE, "DECLARACION_CONTENIDO_INSUFICIENTE");
        }
    }

    private void validateLegalContract(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El contrato legal no puede estar vacío según regulaciones chilenas.",
                    Country.CHILE, "CONTRATO_VACIO");
        }
    }

    private void validateFinancialReport(Document document) throws ValidationException {
        if (document.getContent() == null || document.getContent().isBlank()) {
            throw new ValidationException(
                    "El reporte financiero debe contener datos según normativa del SII.",
                    Country.CHILE, "REPORTE_VACIO");
        }
    }

    private void validateDigitalCertificate(Document document) throws ValidationException {
        if (document.getAuthorizationCode() == null || document.getAuthorizationCode().isBlank()) {
            throw new ValidationException(
                    "El certificado digital requiere timbre electrónico del SII.",
                    Country.CHILE, "TIMBRE_SII_REQUERIDO");
        }
    }

    @Override
    public String getRegulatoryBodyName() {
        return "SII (Servicio de Impuestos Internos)";
    }
}
