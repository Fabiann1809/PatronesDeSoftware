package com.factory.validation;

import com.factory.exception.ValidationException;
import com.factory.model.Document;

/**
 * Interface for country-specific document validation.
 * Each country implementation validates documents against its regulatory requirements.
 */
public interface CountryValidator {

    /**
     * Validates a document according to country-specific regulations.
     *
     * @param document The document to validate
     * @throws ValidationException if the document does not meet the country's requirements
     */
    void validate(Document document) throws ValidationException;

    /**
     * Returns the name of the regulatory body for this country.
     */
    String getRegulatoryBodyName();
}
