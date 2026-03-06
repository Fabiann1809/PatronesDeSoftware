package com.factory.processor;

import com.factory.exception.DocumentProcessingException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;

/**
 * Concrete DocumentProcessor for Argentina.
 * Applies AFIP-specific processing rules (CAE authorization).
 */
public class ArgentinaDocumentProcessor implements DocumentProcessor {

    @Override
    public ProcessingResult process(Document document) throws DocumentProcessingException {
        String details = switch (document.getType()) {
            case ELECTRONIC_INVOICE -> String.format(
                    "Factura electrónica procesada exitosamente por AFIP. " +
                    "CUIT: %s | CAE: %s | Comprobante autorizado.",
                    document.getTaxId(), document.getAuthorizationCode());
            case LEGAL_CONTRACT -> String.format(
                    "Contrato legal procesado y registrado. CUIT: %s | " +
                    "Cumple con el Código Civil y Comercial de la Nación Argentina.",
                    document.getTaxId());
            case FINANCIAL_REPORT -> String.format(
                    "Reporte financiero procesado conforme a RT (Resoluciones Técnicas) de FACPCE. " +
                    "CUIT: %s | Validado por CNV.",
                    document.getTaxId());
            case DIGITAL_CERTIFICATE -> String.format(
                    "Certificado digital emitido. CUIT: %s | " +
                    "Certificado válido ante AFIP y AC habilitada.",
                    document.getTaxId());
            case TAX_DECLARATION -> String.format(
                    "Declaración jurada procesada para AFIP. CUIT: %s | " +
                    "Formato electrónico validado según Ley 11.683.",
                    document.getTaxId());
        };

        return ProcessingResult.success(document, details);
    }

    @Override
    public String getProcessorDescription() {
        return "Procesador de documentos para Argentina (AFIP)";
    }
}
