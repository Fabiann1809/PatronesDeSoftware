package com.factory.factory;

import com.factory.encryption.DocumentEncryptor;
import com.factory.exception.DocumentProcessingException;
import com.factory.exception.EncryptionException;
import com.factory.exception.ValidationException;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;
import com.factory.processor.DocumentProcessor;
import com.factory.validation.CountryValidator;

/**
 * Abstract Factory class implementing the Factory Method pattern.
 * 
 * This is the "Creator" in the Factory Method pattern.
 * Subclasses must implement {@link #createProcessor()} to return the
 * country-specific DocumentProcessor (the "Product").
 * 
 * The {@link #processDocument(Document)} method acts as a Template Method,
 * orchestrating: validation → encryption → processing.
 */
public abstract class DocumentProcessorFactory {

    protected final CountryValidator validator;
    protected final DocumentEncryptor encryptor;

    protected DocumentProcessorFactory(CountryValidator validator, DocumentEncryptor encryptor) {
        this.validator = validator;
        this.encryptor = encryptor;
    }

    /**
     * Factory Method — must be implemented by each concrete factory.
     * Creates and returns the appropriate DocumentProcessor for the country.
     *
     * @return A country-specific DocumentProcessor instance
     */
    public abstract DocumentProcessor createProcessor();

    /**
     * Returns the country this factory handles.
     */
    public abstract Country getCountry();

    /**
     * Static factory method to obtain the correct factory for a given country.
     *
     * @param country The target country
     * @return The corresponding DocumentProcessorFactory
     * @throws EncryptionException if the encryptor cannot be initialized
     */
    public static DocumentProcessorFactory getFactory(Country country) throws EncryptionException {
        DocumentEncryptor encryptor = new DocumentEncryptor();
        return switch (country) {
            case COLOMBIA -> new ColombiaProcessorFactory(encryptor);
            case MEXICO -> new MexicoProcessorFactory(encryptor);
            case ARGENTINA -> new ArgentinaProcessorFactory(encryptor);
            case CHILE -> new ChileProcessorFactory(encryptor);
        };
    }

    /**
     * Template Method that orchestrates the full document processing pipeline:
     * 1. Validate according to country regulations
     * 2. Encrypt sensitive content
     * 3. Process with the country-specific processor
     *
     * @param document The document to process
     * @return ProcessingResult with success/failure and detailed feedback
     */
    public ProcessingResult processDocument(Document document) {
        try {
            // Step 1: Validate according to country-specific regulations
            validator.validate(document);

            // Step 2: Encrypt sensitive document content
            encryptor.encrypt(document);

            // Step 3: Process using the factory method's product
            DocumentProcessor processor = createProcessor();
            return processor.process(document);

        } catch (ValidationException e) {
            return ProcessingResult.failure(document,
                    String.format("Error de validación [%s]: %s (Regla: %s)",
                            e.getErrorCode(), e.getMessage(), e.getValidationRule()));

        } catch (EncryptionException e) {
            return ProcessingResult.failure(document,
                    String.format("Error de encriptación [%s]: %s",
                            e.getErrorCode(), e.getMessage()));

        } catch (DocumentProcessingException e) {
            return ProcessingResult.failure(document,
                    String.format("Error de procesamiento [%s]: %s",
                            e.getErrorCode(), e.getMessage()));

        } catch (Exception e) {
            return ProcessingResult.failure(document,
                    "Error inesperado durante el procesamiento: " + e.getMessage());
        }
    }
}
