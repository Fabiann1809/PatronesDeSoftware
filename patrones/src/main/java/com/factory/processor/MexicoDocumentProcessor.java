package com.factory.processor;

import com.factory.exception.DocumentProcessingException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;

/**
 * Concrete DocumentProcessor for Mexico.
 * Applies SAT-specific processing rules (CFDI 4.0 compliance).
 */
public class MexicoDocumentProcessor implements DocumentProcessor {

    @Override
    public ProcessingResult process(Document document) throws DocumentProcessingException {
        String details = switch (document.getType()) {
            case ELECTRONIC_INVOICE -> String.format(
                    "CFDI 4.0 procesado exitosamente por el SAT. " +
                    "RFC: %s | UUID Folio Fiscal: %s | Sello digital verificado.",
                    document.getTaxId(), document.getAuthorizationCode());
            case LEGAL_CONTRACT -> String.format(
                    "Contrato legal procesado y registrado. RFC: %s | " +
                    "Cumple con el Código Civil Federal de México.",
                    document.getTaxId());
            case FINANCIAL_REPORT -> String.format(
                    "Reporte financiero procesado conforme a NIF (Normas de Información Financiera). " +
                    "RFC: %s | Validado por CNBV.",
                    document.getTaxId());
            case DIGITAL_CERTIFICATE -> String.format(
                    "Certificado de Sello Digital (CSD) emitido. RFC: %s | " +
                    "Certificado válido ante el SAT.",
                    document.getTaxId());
            case TAX_DECLARATION -> String.format(
                    "Declaración fiscal procesada para el SAT. RFC: %s | " +
                    "Formato electrónico validado según Código Fiscal de la Federación.",
                    document.getTaxId());
        };

        return ProcessingResult.success(document, details);
    }

    @Override
    public String getProcessorDescription() {
        return "Procesador de documentos para México (SAT)";
    }
}
