package com.factory;

import com.factory.ui.ConsoleUI;

/**
 * Global Docs — Enterprise Document Processing System
 * 
 * Entry point for the document processing system that demonstrates
 * the Factory Method design pattern.
 * 
 * Coverage: Colombia (DIAN), Mexico (SAT), Argentina (AFIP), Chile (SII)
 * 
 * Features:
 * - Factory Method pattern for country-specific document processing
 * - AES-256-GCM encryption for all sensitive document types
 * - Country-specific validation (NIT, RFC, CUIT, RUT)
 * - Batch processing with detailed reporting
 * - Error handling with descriptive Spanish-language feedback
 * - Interactive console user interface
 * 
 * Document types: Electronic Invoices, Legal Contracts, Financial Reports,
 *                 Digital Certificates, Tax Declarations
 * 
 * @author Global Docs Team
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();
        ui.start();
    }
}