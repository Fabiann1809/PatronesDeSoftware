package com.factory.ui;

import com.factory.batch.BatchProcessor;
import com.factory.factory.DocumentProcessorFactory;
import com.factory.model.Country;
import com.factory.model.Document;
import com.factory.model.DocumentType;
import com.factory.model.ProcessingResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interfaz gráfica para el Sistema de Procesamiento de Documentos Empresariales
 * Global Docs. Utiliza la lógica existente (Factory, BatchProcessor, validación por país).
 */
public class SwingUI extends JFrame {

    private static final String[] FORMATOS = { ".pdf", ".doc", ".docx", ".md", ".csv", ".txt", ".xlsx" };
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final List<Document> pendingDocuments = new ArrayList<>();
    private final BatchProcessor batchProcessor = new BatchProcessor();

    private JComboBox<Country> comboCountry;
    private JComboBox<DocumentType> comboTipoDocumento;
    private JComboBox<String> comboFormato;
    private JTextArea areaContenido;
    private JTextField fieldTaxId;
    private JTextField fieldAuthCode;
    private JPanel panelAuthCode;
    private JTable tablePendientes;
    private DefaultTableModel modelPendientes;
    private JTextArea areaResultados;

    public SwingUI() {
        setTitle("Global Docs — Sistema de Procesamiento de Documentos Empresariales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JPanel main = new JPanel(new BorderLayout(12, 12));
        main.setBorder(new EmptyBorder(12, 12, 12, 12));

        main.add(buildHeader(), BorderLayout.NORTH);
        main.add(buildCenter(), BorderLayout.CENTER);
        main.add(buildSouth(), BorderLayout.SOUTH);

        setContentPane(main);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBorder(new EmptyBorder(0, 0, 8, 0));
        JLabel title = new JLabel("Global Docs — Procesamiento de Documentos Empresariales");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        p.add(title);
        JLabel sub = new JLabel("Colombia (DIAN) | México (SAT) | Argentina (AFIP) | Chile (SII)  •  Procesamiento por lotes  •  Factory Method");
        sub.setFont(sub.getFont().deriveFont(11f));
        sub.setForeground(new Color(80, 80, 80));
        p.add(sub);
        return p;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(8, 8));

        JPanel form = buildFormPanel();
        center.add(form, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setResizeWeight(0.45);

        JPanel pendientesPanel = new JPanel(new BorderLayout(4, 4));
        pendientesPanel.setBorder(new TitledBorder("Documentos pendientes"));
        modelPendientes = new DefaultTableModel(
                new String[]{"ID", "Tipo", "País", "Regulador", "Identificador", "Vista contenido"}, 0);
        tablePendientes = new JTable(modelPendientes);
        tablePendientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePendientes.getColumnModel().getColumn(5).setPreferredWidth(180);
        pendientesPanel.add(new JScrollPane(tablePendientes), BorderLayout.CENTER);
        split.setTopComponent(pendientesPanel);

        JPanel resultadosPanel = new JPanel(new BorderLayout(4, 4));
        resultadosPanel.setBorder(new TitledBorder("Resultados del procesamiento y errores"));
        areaResultados = new JTextArea(8, 40);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultadosPanel.add(new JScrollPane(areaResultados), BorderLayout.CENTER);
        split.setBottomComponent(resultadosPanel);

        center.add(split, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new TitledBorder("Crear nuevo documento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel("País:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        comboCountry = new JComboBox<>(Country.values());
        form.add(comboCountry, gbc);

        gbc.gridx = 0; gbc.gridy = ++row;
        form.add(new JLabel("Tipo de documento:"), gbc);
        gbc.gridx = 1;
        comboTipoDocumento = new JComboBox<>(DocumentType.values());
        comboTipoDocumento.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                DocumentType t = (DocumentType) e.getItem();
                boolean show = t == DocumentType.ELECTRONIC_INVOICE || t == DocumentType.DIGITAL_CERTIFICATE;
                panelAuthCode.setVisible(show);
            }
        });
        form.add(comboTipoDocumento, gbc);

        gbc.gridx = 0; gbc.gridy = ++row;
        form.add(new JLabel("Formato:"), gbc);
        gbc.gridx = 1;
        comboFormato = new JComboBox<>(FORMATOS);
        form.add(comboFormato, gbc);

        gbc.gridx = 0; gbc.gridy = ++row;
        form.add(new JLabel("Identificador tributario:"), gbc);
        gbc.gridx = 1;
        fieldTaxId = new JTextField(25);
        fieldTaxId.setToolTipText("NIT (CO), RFC (MX), CUIT (AR), RUT (CL)");
        form.add(fieldTaxId, gbc);

        gbc.gridx = 0; gbc.gridy = ++row;
        panelAuthCode = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelAuthCode.add(new JLabel("Código de autorización:"));
        fieldAuthCode = new JTextField(20);
        fieldAuthCode.setToolTipText("Resolución DIAN, UUID CFDI, CAE AFIP, Folio DTE");
        panelAuthCode.add(fieldAuthCode);
        DocumentType sel = (DocumentType) comboTipoDocumento.getSelectedItem();
        panelAuthCode.setVisible(sel == DocumentType.ELECTRONIC_INVOICE || sel == DocumentType.DIGITAL_CERTIFICATE);
        gbc.gridx = 1;
        form.add(panelAuthCode, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        form.add(new JLabel("Contenido del documento:"), gbc);
        gbc.gridy = ++row; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.3;
        areaContenido = new JTextArea(3, 50);
        areaContenido.setLineWrap(true);
        form.add(new JScrollPane(areaContenido), gbc);

        gbc.gridy = ++row; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weighty = 0;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar documento");
        btnAgregar.addActionListener(e -> agregarDocumento());
        btnPanel.add(btnAgregar);
        form.add(btnPanel, gbc);

        return form;
    }

    private JPanel buildSouth() {
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton btnIndividual = new JButton("Procesar documento seleccionado");
        btnIndividual.addActionListener(e -> procesarIndividual());
        JButton btnLote = new JButton("Procesar lote");
        btnLote.addActionListener(e -> procesarLote());
        JButton btnDemo = new JButton("Ejecutar demostración");
        btnDemo.addActionListener(e -> ejecutarDemo());
        south.add(btnIndividual);
        south.add(btnLote);
        south.add(btnDemo);
        return south;
    }

    private void agregarDocumento() {
        Country country = (Country) comboCountry.getSelectedItem();
        DocumentType tipo = (DocumentType) comboTipoDocumento.getSelectedItem();
        String contenido = areaContenido.getText().trim();
        if (contenido.isEmpty()) {
            contenido = "Contenido de ejemplo para " + country.getDisplayName();
        }
        String taxId = fieldTaxId.getText().trim();
        String authCode = fieldAuthCode.getText().trim();
        if (authCode.isEmpty()) authCode = null;

        Document doc = new Document(tipo, country, contenido,
                taxId.isEmpty() ? null : taxId, authCode);
        pendingDocuments.add(doc);
        addRow(doc);
        areaResultados.append("Documento agregado: " + doc.getId() + " — " + tipo.getDisplayName() + " (" + country.getDisplayName() + ")\n");
    }

    private void addRow(Document doc) {
        String preview = doc.getContent().length() > 40 ? doc.getContent().substring(0, 40) + "…" : doc.getContent();
        modelPendientes.addRow(new Object[]{
                doc.getId(),
                doc.getType().getDisplayName(),
                doc.getCountry().getDisplayName(),
                doc.getCountry().getRegulatoryBody(),
                doc.getTaxId() != null ? doc.getTaxId() : "—",
                preview
        });
    }

    private void procesarIndividual() {
        int idx = tablePendientes.getSelectedRow();
        if (idx < 0 || idx >= pendingDocuments.size()) {
            JOptionPane.showMessageDialog(this, "Seleccione un documento de la tabla.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Document doc = pendingDocuments.get(idx);
        areaResultados.append("\n--- Procesando documento " + doc.getId() + " ---\n");
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());

        SwingWorker<ProcessingResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ProcessingResult doInBackground() throws Exception {
                return DocumentProcessorFactory.getFactory(doc.getCountry()).processDocument(doc);
            }
            @Override
            protected void done() {
                try {
                    ProcessingResult r = get();
                    appendResult(r);
                    if (r.isSuccess()) {
                        pendingDocuments.remove(idx);
                        modelPendientes.removeRow(idx);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    areaResultados.append("Error: " + e.getCause().getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    private void procesarLote() {
        if (pendingDocuments.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay documentos pendientes. Agregue documentos o ejecute la demostración.", "Sin documentos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        areaResultados.append("\n--- Procesamiento por lotes ---\n");
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());

        List<Document> copia = new ArrayList<>(pendingDocuments);
        SwingWorker<BatchProcessor.BatchResult, Void> worker = new SwingWorker<>() {
            @Override
            protected BatchProcessor.BatchResult doInBackground() {
                return batchProcessor.processBatch(copia);
            }
            @Override
            protected void done() {
                try {
                    BatchProcessor.BatchResult br = get();
                    areaResultados.append(br.generateReport());
                    for (ProcessingResult r : br.getResults()) {
                        appendResult(r);
                    }
                    pendingDocuments.clear();
                    modelPendientes.setRowCount(0);
                    for (ProcessingResult r : br.getFailures()) {
                        pendingDocuments.add(r.getDocument());
                        addRow(r.getDocument());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    areaResultados.append("Error en lote: " + e.getCause().getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    private void ejecutarDemo() {
        areaResultados.append("\n--- Demostración completa ---\n");
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());

        SwingWorker<BatchProcessor.BatchResult, Void> worker = new SwingWorker<>() {
            @Override
            protected BatchProcessor.BatchResult doInBackground() {
                List<Document> demo = createDemoDocuments();
                return batchProcessor.processBatch(demo);
            }
            @Override
            protected void done() {
                try {
                    BatchProcessor.BatchResult br = get();
                    areaResultados.append(br.generateReport());
                    for (ProcessingResult r : br.getResults()) {
                        appendResult(r);
                    }
                    areaResultados.append("\nDemostración completada.\n");
                } catch (InterruptedException | ExecutionException e) {
                    areaResultados.append("Error: " + e.getCause().getMessage() + "\n");
                }
            }
        };
        worker.execute();
    }

    private void appendResult(ProcessingResult r) {
        String status = r.isSuccess() ? "✅ EXITOSO" : "❌ ERROR";
        areaResultados.append(status + " | " + r.getDocument().getId() + " | " + r.getDocument().getType().getDisplayName()
                + " | " + r.getDocument().getCountry().getDisplayName() + "\n");
        areaResultados.append("  Mensaje: " + r.getMessage() + "\n");
        areaResultados.append("  Procesado: " + r.getProcessedAt().format(TIME_FMT) + "\n");
        areaResultados.setCaretPosition(areaResultados.getDocument().getLength());
    }

    private List<Document> createDemoDocuments() {
        List<Document> docs = new ArrayList<>();
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.COLOMBIA,
                "Factura de venta de servicios tecnológicos - Total: $15,000,000 COP",
                "900123456-7", "1234567890123456"));
        docs.add(new Document(DocumentType.LEGAL_CONTRACT, Country.COLOMBIA,
                "Contrato de prestación de servicios profesionales por 12 meses",
                "800999888-1", null));
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.MEXICO,
                "CFDI por servicios de consultoría - Total: $250,000 MXN",
                "XAXX010101000", "550e8400-e29b-41d4-a716-446655440000"));
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.ARGENTINA,
                "Factura electrónica por servicios de software - Total: $500,000 ARS",
                "20345678901", "12345678901234"));
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.CHILE,
                "DTE por servicios de hosting - Total: $2,500,000 CLP",
                "7654321-K", "12345"));
        docs.add(new Document(DocumentType.ELECTRONIC_INVOICE, Country.COLOMBIA,
                "Factura sin NIT - debe fallar", null, "1234567890"));
        docs.add(new Document(DocumentType.FINANCIAL_REPORT, Country.MEXICO,
                "Reporte con RFC inválido", "INVALIDO", null));
        return docs;
    }

    public void start() {
        setVisible(true);
    }
}
