package com.factory.factory;

import com.factory.encryption.DocumentEncryptor;
import com.factory.model.Country;
import com.factory.processor.ChileDocumentProcessor;
import com.factory.processor.DocumentProcessor;
import com.factory.validation.ChileValidator;

/**
 * Concrete Factory for Chile.
 * Creates ChileDocumentProcessor instances with SII validation rules.
 */
public class ChileProcessorFactory extends DocumentProcessorFactory {

    public ChileProcessorFactory(DocumentEncryptor encryptor) {
        super(new ChileValidator(), encryptor);
    }

    @Override
    public DocumentProcessor createProcessor() {
        return new ChileDocumentProcessor();
    }

    @Override
    public Country getCountry() {
        return Country.CHILE;
    }
}
