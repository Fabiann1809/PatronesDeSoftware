package com.factory.factory;

import com.factory.encryption.DocumentEncryptor;
import com.factory.model.Country;
import com.factory.processor.DocumentProcessor;
import com.factory.processor.MexicoDocumentProcessor;
import com.factory.validation.MexicoValidator;

/**
 * Concrete Factory for Mexico.
 * Creates MexicoDocumentProcessor instances with SAT validation rules.
 */
public class MexicoProcessorFactory extends DocumentProcessorFactory {

    public MexicoProcessorFactory(DocumentEncryptor encryptor) {
        super(new MexicoValidator(), encryptor);
    }

    @Override
    public DocumentProcessor createProcessor() {
        return new MexicoDocumentProcessor();
    }

    @Override
    public Country getCountry() {
        return Country.MEXICO;
    }
}
