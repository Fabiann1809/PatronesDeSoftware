package com.factory.factory;

import com.factory.encryption.DocumentEncryptor;
import com.factory.model.Country;
import com.factory.processor.ColombiaDocumentProcessor;
import com.factory.processor.DocumentProcessor;
import com.factory.validation.ColombiaValidator;

/**
 * Concrete Factory for Colombia.
 * Creates ColombiaDocumentProcessor instances with DIAN validation rules.
 */
public class ColombiaProcessorFactory extends DocumentProcessorFactory {

    public ColombiaProcessorFactory(DocumentEncryptor encryptor) {
        super(new ColombiaValidator(), encryptor);
    }

    @Override
    public DocumentProcessor createProcessor() {
        return new ColombiaDocumentProcessor();
    }

    @Override
    public Country getCountry() {
        return Country.COLOMBIA;
    }
}
