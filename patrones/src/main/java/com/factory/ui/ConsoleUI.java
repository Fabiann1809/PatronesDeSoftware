package com.factory.ui;

import com.factory.batch.BatchProcessor;
import com.factory.factory.DocumentProcessorFactory;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;
import com.factory.model.ProcessingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive console user interface for the Global Docs document processing system.
 * All user-facing text is displayed in Spanish.
 */
public class ConsoleUI {

    private final Scanner scanner;
    private final List<Document> pendingDocuments;
    private final BatchProcessor batchProcessor;
    private boolean running;

    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.pendingDocuments = new ArrayList<>();
        this.batchProcessor = new BatchProcessor();
        this.running = true;
    }

    /**
     * Starts the interactive console interface.
     */
    public void start() {
        printWelcomeBanner();

        while (running) {
            printMainMenu();
            int option = readIntOption(1, 6);

            switch (option) {
                case 1 -> createDocument();
                case 2 -> processIndividualDocument();
                case 3 -> processBatch();
                case 4 -> viewPendingDocuments();
                case 5 -> runDemo();
                case 6 -> exit();
            }
        }

        scanner.close();
    }

    private void printWelcomeBanner() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                        ║");
        System.out.println("║      🌐  G L O B A L   D O C S  —  Sistema de Procesamiento  🌐       ║");
        System.out.println("║                                                                        ║");
        System.out.println("║   Sistema Empresarial de Procesamiento de Documentos                   ║");
        System.out.println("║   Cobertura: 🇨🇴 Colombia | 🇲🇽 México | 🇦🇷 Argentina | 🇨🇱 Chile       ║");
        System.out.println("║                                                                        ║");
        System.out.println("║   🔐 Encriptación AES-256-GCM para documentos sensibles                ║");
        System.out.println("║   📋 Validación regulatoria por país (DIAN, SAT, AFIP, SII)            ║");
        System.out.println("║   📦 Procesamiento individual y por lotes                              ║");
        System.out.println("║   🏭 Patrón de diseño: Factory Method                                  ║");
        System.out.println("║                                                                        ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────┐");
        System.out.println("│          📋 MENÚ PRINCIPAL               │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1. 📄 Crear nuevo documento             │");
        System.out.println("│  2. ⚙️  Procesar un documento            │");
        System.out.println("│  3. 📦 Procesar lote de documentos       │");
        System.out.println("│  4. 👁️  Ver documentos pendientes        │");
        System.out.println("│  5. 🚀 Ejecutar demostración completa   │");
        System.out.println("│  6. 🚪 Salir                             │");
        System.out.println("└──────────────────────────────────────────┘");
        System.out.print("   Seleccione una opción: ");
    }

    // ──────────────────────────────────────────────────────
    //  1. CREATE DOCUMENT
    // ──────────────────────────────────────────────────────

    private void createDocument() {
        printSectionHeader("CREAR NUEVO DOCUMENTO");

        // Select country
        Country country = selectCountry();
        if (country == null) return;

        // Select document type
        DocumentType docType = selectDocumentType();
        if (docType == null) return;

        // Enter content
        System.out.print("   📝 Ingrese el contenido del documento: ");
        String content = scanner.nextLine().trim();
        if (content.isEmpty()) {
            content = "Contenido del documento de ejemplo para " + country.getDisplayName();
        }

        // Enter tax ID
        System.out.print("   🆔 Ingrese el identificador tributario (" + getTaxIdLabel(country) + "): ");
        String taxId = scanner.nextLine().trim();

        // Enter authorization code (if applicable)
        String authCode = null;
        if (requiresAuthorizationCode(docType)) {
            System.out.print("   🔑 Ingrese el código de autorización (" + getAuthCodeLabel(country) + "): ");
            authCode = scanner.nextLine().trim();
            if (authCode.isEmpty()) authCode = null;
        }

        Document doc = new Document(docType, country, content, taxId.isEmpty() ? null : taxId, authCode);
        pendingDocuments.add(doc);

        System.out.println();
        System.out.println("   ✅ Documento creado exitosamente:");
        System.out.println("   " + doc);
        System.out.println("   📌 Agregado a la cola de documentos pendientes (" + pendingDocuments.size() + " en cola)");
    }

    // ──────────────────────────────────────────────────────
    //  2. PROCESS INDIVIDUAL DOCUMENT
    // ──────────────────────────────────────────────────────

    private void processIndividualDocument() {
        printSectionHeader("PROCESAR DOCUMENTO INDIVIDUAL");

        if (pendingDocuments.isEmpty()) {
            System.out.println("   ⚠️  No hay documentos pendientes. Cree uno primero (opción 1).");
            return;
        }

        // Show list of pending documents
        System.out.println("   Documentos pendientes:");
        for (int i = 0; i < pendingDocuments.size(); i++) {
            System.out.printf("   %d. %s%n", i + 1, pendingDocuments.get(i));
        }

        System.out.print("   Seleccione el documento a procesar: ");
        int index = readIntOption(1, pendingDocuments.size()) - 1;

        Document doc = pendingDocuments.get(index);

        System.out.println();
        System.out.println("   ⏳ Procesando documento...");
        System.out.printf("   🏭 Usando fábrica de procesamiento para: %s%n", doc.getCountry().getDisplayName());
        System.out.printf("   📋 Entidad reguladora: %s%n", doc.getCountry().getRegulatoryBody());

        try {
            DocumentProcessorFactory factory = DocumentProcessorFactory.getFactory(doc.getCountry());
            System.out.printf("   🔧 Procesador creado: %s%n", factory.createProcessor().getProcessorDescription());

            ProcessingResult result = factory.processDocument(doc);
            System.out.println();
            System.out.println(result);

            // Remove from pending if successful
            if (result.isSuccess()) {
                pendingDocuments.remove(index);
                System.out.println("   📌 Documento removido de la cola de pendientes.");
            }

        } catch (Exception e) {
            System.out.println("   ❌ Error crítico: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────
    //  3. PROCESS BATCH
    // ──────────────────────────────────────────────────────

    private void processBatch() {
        printSectionHeader("PROCESAMIENTO POR LOTES");

        if (pendingDocuments.isEmpty()) {
            System.out.println("   ⚠️  No hay documentos pendientes para procesar en lote.");
            System.out.println("   💡 Use la opción 1 para crear documentos o la opción 5 para una demostración.");
            return;
        }

        System.out.printf("   📦 Se procesarán %d documentos en lote.%n", pendingDocuments.size());
        System.out.print("   ¿Desea continuar? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("s") && !confirm.equals("si") && !confirm.equals("sí")) {
            System.out.println("   ⏸️  Procesamiento por lotes cancelado.");
            return;
        }

        System.out.println("   ⏳ Procesando lote...");

        List<Document> toProcess = new ArrayList<>(pendingDocuments);
        BatchProcessor.BatchResult batchResult = batchProcessor.processBatch(toProcess);

        System.out.println(batchResult.generateReport());

        // Show each individual result
        System.out.println("   📋 DETALLE POR DOCUMENTO:");
        System.out.println("   ─────────────────────────────────────────────────────");
        for (ProcessingResult result : batchResult.getResults()) {
            System.out.println(result);
            System.out.println();
        }

        // Remove successful documents from pending
        long successCount = batchResult.getSuccessCount();
        pendingDocuments.clear();
        for (ProcessingResult r : batchResult.getFailures()) {
            pendingDocuments.add(r.getDocument());
        }

        System.out.printf("   📌 %d documentos procesados exitosamente y removidos de la cola.%n", successCount);
        if (!pendingDocuments.isEmpty()) {
            System.out.printf("   ⚠️  %d documentos fallidos permanecen en la cola.%n", pendingDocuments.size());
        }
    }

    // ──────────────────────────────────────────────────────
    //  4. VIEW PENDING DOCUMENTS
    // ──────────────────────────────────────────────────────

    private void viewPendingDocuments() {
        printSectionHeader("DOCUMENTOS PENDIENTES");

        if (pendingDocuments.isEmpty()) {
            System.out.println("   📭 No hay documentos pendientes en la cola.");
            return;
        }

        System.out.printf("   📄 Total en cola: %d documentos%n%n", pendingDocuments.size());
        for (int i = 0; i < pendingDocuments.size(); i++) {
            System.out.printf("   %d. %s%n", i + 1, pendingDocuments.get(i));
        }
    }

    // ──────────────────────────────────────────────────────
    //  5. RUN DEMO
    // ──────────────────────────────────────────────────────

    private void runDemo() {
        printSectionHeader("DEMOSTRACIÓN COMPLETA DEL SISTEMA");

        System.out.println("   🚀 Creando documentos de ejemplo para los 4 países...\n");

        List<Document> demoDocuments = createDemoDocuments();

        // Show created documents
        System.out.println("   📄 Documentos creados:");
        System.out.println("   ─────────────────────────────────────────────────────");
        for (Document doc : demoDocuments) {
            System.out.println("   • " + doc);
        }

        System.out.println("\n   ⏳ Procesando lote de demostración...\n");

        // Process batch
        BatchProcessor.BatchResult batchResult = batchProcessor.processBatch(demoDocuments);

        // Show report
        System.out.println(batchResult.generateReport());

        // Show detail of each result
        System.out.println("   📋 DETALLE DE CADA DOCUMENTO PROCESADO:");
        System.out.println("   ═══════════════════════════════════════════════════════");
        for (ProcessingResult result : batchResult.getResults()) {
            System.out.println(result);
            System.out.println();
        }

        System.out.println("   ✅ Demostración completada.");
    }

    // ──────────────────────────────────────────────────────
    //  6. EXIT
    // ──────────────────────────────────────────────────────

    private void exit() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║  👋 ¡Gracias por usar Global Docs!                                     ║");
        System.out.println("║  Sistema de Procesamiento de Documentos Empresariales                  ║");
        System.out.println("║  Patrón de diseño: Factory Method                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
        System.out.println();
        running = false;
    }

    // ──────────────────────────────────────────────────────
    //  HELPER METHODS
    // ──────────────────────────────────────────────────────

    private Country selectCountry() {
        System.out.println("   🌎 Seleccione el país:");
        Country[] countries = Country.values();
        for (int i = 0; i < countries.length; i++) {
            System.out.printf("      %d. %s (%s)%n", i + 1, countries[i].getDisplayName(), countries[i].getRegulatoryBody());
        }
        System.out.print("   Opción: ");
        int index = readIntOption(1, countries.length) - 1;
        Country selected = countries[index];
        System.out.println("   ✔ País seleccionado: " + selected.getDisplayName());
        return selected;
    }

    private DocumentType selectDocumentType() {
        System.out.println("   📄 Seleccione el tipo de documento:");
        DocumentType[] types = DocumentType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.printf("      %d. %s%n", i + 1, types[i].getDisplayName());
        }
        System.out.print("   Opción: ");
        int index = readIntOption(1, types.length) - 1;
        DocumentType selected = types[index];
        System.out.println("   ✔ Tipo seleccionado: " + selected.getDisplayName());
        return selected;
    }

    private int readIntOption(int min, int max) {
        while (true) {
            try {
                String line = scanner.nextLine().trim();
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("   ⚠️  Por favor ingrese un número entre %d y %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("   ⚠️  Entrada inválida. Ingrese un número entre %d y %d: ", min, max);
            }
        }
    }

    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println("   ══════════════════════════════════════════════════════");
        System.out.println("   " + title);
        System.out.println("   ══════════════════════════════════════════════════════");
        System.out.println();
    }

    private String getTaxIdLabel(Country country) {
        return switch (country) {
            case COLOMBIA -> "NIT, ej: 900123456-7";
            case MEXICO -> "RFC, ej: XAXX010101000";
            case ARGENTINA -> "CUIT, ej: 20123456789";
            case CHILE -> "RUT, ej: 12345678-9";
        };
    }

    private String getAuthCodeLabel(Country country) {
        return switch (country) {
            case COLOMBIA -> "Resolución DIAN";
            case MEXICO -> "UUID CFDI";
            case ARGENTINA -> "CAE AFIP";
            case CHILE -> "Folio DTE";
        };
    }

    private boolean requiresAuthorizationCode(DocumentType type) {
        return type == DocumentType.ELECTRONIC_INVOICE || type == DocumentType.DIGITAL_CERTIFICATE;
    }

    /**
     * Creates a set of demo documents covering all countries and document types,
     * including intentionally invalid documents to demonstrate error handling.
     */
    private List<Document> createDemoDocuments() {
        List<Document> docs = new ArrayList<>();

        // ── COLOMBIA (valid documents)
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.COLOMBIA,
                "Factura de venta de servicios tecnológicos - Total: $15,000,000 COP",
                "900123456-7", "1234567890123456"));
        docs.add(new Document(DocumentType.LEGAL_CONTRACT, Country.COLOMBIA,
                "Contrato de prestación de servicios profesionales por 12 meses",
                "800999888-1", null));
        docs.add(new Document(DocumentType.TAX_DECLARATION, Country.COLOMBIA,
                "Declaración de renta año gravable 2025 - Ingresos: $120,000,000 COP",
                "123456789-0", null));

        // ── MEXICO (valid documents)
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.MEXICO,
                "CFDI por servicios de consultoría - Total: $250,000 MXN",
                "XAXX010101000", "550e8400-e29b-41d4-a716-446655440000"));
        docs.add(new Document(DocumentType.FINANCIAL_REPORT, Country.MEXICO,
                "Reporte financiero trimestral Q4 2025 - Ingresos: $5,000,000 MXN",
                "ABC123456AB1", null));

        // ── ARGENTINA (valid documents)
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.ARGENTINA,
                "Factura electrónica por servicios de software - Total: $500,000 ARS",
                "20345678901", "12345678901234"));
        docs.add(new Document(DocumentType.DIGITAL_CERTIFICATE, Country.ARGENTINA,
                "Certificado de firma digital empresarial",
                "30712345678", "AUTH-AFIP-2025"));

        // ── CHILE (valid documents)
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.CHILE,
                "DTE por servicios de hosting - Total: $2,500,000 CLP",
                "7654321-K", "12345"));
        docs.add(new Document(DocumentType.TAX_DECLARATION, Country.CHILE,
                "Declaración mensual de IVA - Período: Enero 2026",
                "1234567-8", null));

        // ── DOCUMENTS WITH INTENTIONAL ERRORS (for error handling demo)
        // Missing NIT
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.COLOMBIA,
                "Factura sin NIT - debe fallar", null, "1234567890"));
        // Invalid RFC
        docs.add(new Document(DocumentType.FINANCIAL_REPORT, Country.MEXICO,
                "Reporte con RFC inválido", "INVALIDO", null));
        // Missing CUIT
        docs.add(new Document(DocumentType.LEGAL_CONTRACT, Country.ARGENTINA,
                "Contrato sin CUIT - debe fallar", null, null));

        return docs;
    }
}
