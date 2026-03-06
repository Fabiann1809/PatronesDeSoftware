package com.factory.processor;

import com.factory.exception.DocumentProcessingException;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;

/**
 * Interface for document processors.
 * Each country has its own concrete implementation with specific processing logic.
 * This is the "Product" in the Factory Method pattern.
 */
public interface DocumentProcessor {

    /**
     * Processes a document according to country-specific rules.
     *
     * @param document The document to process
     * @return ProcessingResult with success/failure status and feedback
     * @throws DocumentProcessingException if a critical error occurs during processing
     */
    ProcessingResult process(Document document) throws DocumentProcessingException;

    /**
     * Returns a description of this processor for display purposes.
     */
    String getProcessorDescription();
}
