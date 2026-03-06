package com.factory.factory;

import com.factory.encryption.DocumentEncryptor;
import com.factory.model.Country;
import com.factory.processor.ArgentinaDocumentProcessor;
import com.factory.processor.DocumentProcessor;
import com.factory.validation.ArgentinaValidator;

/**
 * Concrete Factory for Argentina.
 * Creates ArgentinaDocumentProcessor instances with AFIP validation rules.
 */
public class ArgentinaProcessorFactory extends DocumentProcessorFactory {

    public ArgentinaProcessorFactory(DocumentEncryptor encryptor) {
        super(new ArgentinaValidator(), encryptor);
    }

    @Override
    public DocumentProcessor createProcessor() {
        return new ArgentinaDocumentProcessor();
    }

    @Override
    public Country getCountry() {
        return Country.ARGENTINA;
    }
}
