package com.factory.batch;

import com.factory.factory.DocumentProcessorFactory;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.ProcessingResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles batch processing of multiple documents.
 * Groups documents by country and processes each group with the appropriate factory.
 * Generates a detailed report with statistics and error feedback.
 */
public class BatchProcessor {

    /**
     * Processes a batch of documents, grouping by country for optimized processing.
     *
     * @param documents List of documents to process
     * @return BatchResult with all individual results and summary statistics
     */
    public BatchResult processBatch(List<Document> documents) {
        List<ProcessingResult> results = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.now();

        // Group documents by country for efficient factory reuse
        Map<Country, List<Document>> groupedByCountry = documents.stream()
                .collect(Collectors.groupingBy(Document::getCountry));

        for (Map.Entry<Country, List<Document>> entry : groupedByCountry.entrySet()) {
            Country country = entry.getKey();
            List<Document> countryDocs = entry.getValue();

            try {
                DocumentProcessorFactory factory = DocumentProcessorFactory.getFactory(country);

                for (Document doc : countryDocs) {
                    ProcessingResult result = factory.processDocument(doc);
                    results.add(result);
                }
            } catch (Exception e) {
                // If factory creation fails, mark all documents from this country as failed
                for (Document doc : countryDocs) {
                    results.add(ProcessingResult.failure(doc,
                            String.format("Error al crear la fábrica de procesamiento para %s: %s",
                                    country.getDisplayName(), e.getMessage())));
                }
            }
        }

        LocalDateTime endTime = LocalDateTime.now();
        return new BatchResult(results, startTime, endTime);
    }

    /**
     * Holds the results of a batch processing operation.
     */
    public static class BatchResult {

        private final List<ProcessingResult> results;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;

        public BatchResult(List<ProcessingResult> results, LocalDateTime startTime, LocalDateTime endTime) {
            this.results = results;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public List<ProcessingResult> getResults() { return results; }
        public int getTotalProcessed() { return results.size(); }
        public long getSuccessCount() { return results.stream().filter(ProcessingResult::isSuccess).count(); }
        public long getFailureCount() { return results.stream().filter(r -> !r.isSuccess()).count(); }
        public List<ProcessingResult> getFailures() {
            return results.stream().filter(r -> !r.isSuccess()).collect(Collectors.toList());
        }
        public List<ProcessingResult> getSuccesses() {
            return results.stream().filter(ProcessingResult::isSuccess).collect(Collectors.toList());
        }

        /**
         * Generates a formatted summary report in Spanish.
         */
        public String generateReport() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            StringBuilder sb = new StringBuilder();

            sb.append("\n");
            sb.append("╔══════════════════════════════════════════════════════════════════════════╗\n");
            sb.append("║           📊 REPORTE DE PROCESAMIENTO POR LOTES - GLOBAL DOCS          ║\n");
            sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║  🕐 Inicio:      %s                            ║\n", startTime.format(formatter)));
            sb.append(String.format("║  🕐 Fin:         %s                            ║\n", endTime.format(formatter)));
            sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
            sb.append(String.format("║  📄 Total documentos:  %-5d                                         ║\n", getTotalProcessed()));
            sb.append(String.format("║  ✅ Exitosos:          %-5d                                         ║\n", getSuccessCount()));
            sb.append(String.format("║  ❌ Fallidos:           %-5d                                         ║\n", getFailureCount()));
            sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");

            // Group results by country for summary
            Map<Country, List<ProcessingResult>> byCountry = results.stream()
                    .collect(Collectors.groupingBy(r -> r.getDocument().getCountry()));

            sb.append("║                    📈 RESUMEN POR PAÍS                                 ║\n");
            sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");

            for (Map.Entry<Country, List<ProcessingResult>> entry : byCountry.entrySet()) {
                Country country = entry.getKey();
                List<ProcessingResult> countryResults = entry.getValue();
                long countrySuccess = countryResults.stream().filter(ProcessingResult::isSuccess).count();
                long countryFail = countryResults.size() - countrySuccess;

                sb.append(String.format("║  🌎 %-15s  Total: %-3d | ✅ %-3d | ❌ %-3d                     ║\n",
                        country.getDisplayName(), countryResults.size(), countrySuccess, countryFail));
            }

            // Show error details if any
            List<ProcessingResult> failures = getFailures();
            if (!failures.isEmpty()) {
                sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");
                sb.append("║                    ⚠️  DETALLE DE ERRORES                               ║\n");
                sb.append("╠══════════════════════════════════════════════════════════════════════════╣\n");

                for (ProcessingResult failure : failures) {
                    sb.append(String.format("║  Doc [%s] %s - %s\n",
                            failure.getDocument().getId(),
                            failure.getDocument().getType().getDisplayName(),
                            failure.getDocument().getCountry().getDisplayName()));
                    sb.append(String.format("║  └─ %s\n", failure.getMessage()));
                }
            }

            sb.append("╚══════════════════════════════════════════════════════════════════════════╝\n");

            return sb.toString();
        }
    }
}
