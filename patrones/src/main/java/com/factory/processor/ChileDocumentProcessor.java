package com.factory.processor;

import com.factory.exception.DocumentProcessingException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;

/**
 * Concrete DocumentProcessor for Chile.
 * Applies SII-specific processing rules (DTE compliance).
 */
public class ChileDocumentProcessor implements DocumentProcessor {

    @Override
    public ProcessingResult process(Document document) throws DocumentProcessingException {
        String details = switch (document.getType()) {
            case ELECTRONIC_INVOICE -> String.format(
                    "DTE (Documento Tributario Electrónico) procesado exitosamente por el SII. " +
                    "RUT: %s | Folio: %s | Timbre electrónico verificado.",
                    document.getTaxId(), document.getAuthorizationCode());
            case LEGAL_CONTRACT -> String.format(
                    "Contrato legal procesado y registrado. RUT: %s | " +
                    "Cumple con el Código Civil de Chile.",
                    document.getTaxId());
            case FINANCIAL_REPORT -> String.format(
                    "Reporte financiero procesado conforme a IFRS adoptadas por Chile. " +
                    "RUT: %s | Validado por CMF (Comisión para el Mercado Financiero).",
                    document.getTaxId());
            case DIGITAL_CERTIFICATE -> String.format(
                    "Certificado digital emitido. RUT: %s | " +
                    "Certificado válido ante el SII con firma electrónica avanzada.",
                    document.getTaxId());
            case TAX_DECLARATION -> String.format(
                    "Declaración tributaria procesada para el SII. RUT: %s | " +
                    "Formulario electrónico validado según Código Tributario.",
                    document.getTaxId());
        };

        return ProcessingResult.success(document, details);
    }

    @Override
    public String getProcessorDescription() {
        return "Procesador de documentos para Chile (SII)";
    }
}
