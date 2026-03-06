package com.factory.model;

/**
 * Enum representing the countries supported by the Global Docs system.
 * Each country has its own regulatory body and specific document processing rules.
 */
public enum Country {
    COLOMBIA("Colombia", "CO", "DIAN"),
    MEXICO("México", "MX", "SAT"),
    ARGENTINA("Argentina", "AR", "AFIP"),
    CHILE("Chile", "CL", "SII");

    private final String displayName;
    private final String isoCode;
    private final String regulatoryBody;

    Country(String displayName, String isoCode, String regulatoryBody) {
        this.displayName = displayName;
        this.isoCode = isoCode;
        this.regulatoryBody = regulatoryBody;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getRegulatoryBody() {
        return regulatoryBody;
    }

    @Override
    public String toString() {
        return displayName + " (" + isoCode + ")";
    }
}
