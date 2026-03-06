package com.factory.exception;

import com.factory.model.Country;

/**
 * Exception thrown when a document fails country-specific validation.
 * Includes the country and validation rule that failed.
 */
public class ValidationException extends DocumentProcessingException {

    private final Country country;
    private final String validationRule;

    public ValidationException(String message, Country country, String validationRule) {
        super(message, "VAL-" + country.getIsoCode());
        this.country = country;
        this.validationRule = validationRule;
    }

    public Country getCountry() {
        return country;
    }

    public String getValidationRule() {
        return validationRule;
    }

    @Override
    public String toString() {
        return String.format("[%s] Validación fallida en %s (%s): %s - Regla: %s",
                getErrorCode(), country.getDisplayName(), country.getRegulatoryBody(),
                getMessage(), validationRule);
    }
}
