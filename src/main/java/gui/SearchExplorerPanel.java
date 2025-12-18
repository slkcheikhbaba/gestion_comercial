package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import dao.GenericDAO;
import model.*;
import util.PdfExporter;

public class SearchExplorerPanel extends JPanel {
    private JComboBox<String> tableComboBox;
    private JTextField searchField;
    private JButton searchButton;
    private JButton exportSelectedButton;
    private JButton exportWholeTableButton;
    private JButton clearButton;
    private JButton selectAllButton;
    private JButton deselectAllButton;
    private JTable resultsTable;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    private GenericDAO<?> currentDao;
    private List<?> currentResults;
    private Class<?> currentEntityClass;
    private DefaultTableModel tableModel;
    private List<Boolean> selectedRows;

    public SearchExplorerPanel() {
        initUI();
        setupListeners();
        loadWholeTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel de contrôle (en haut)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Critères de recherche"));
        
        controlPanel.add(new JLabel("Table:"));
        tableComboBox = new JComboBox<>(new String[]{
            "Personne", "Ville", "Agence", "Exploitation", 
            "Dirige", "EstComptablePour", "EstComptableDans"
        });
        tableComboBox.setPreferredSize(new Dimension(150, 25));
        controlPanel.add(tableComboBox);
        
        controlPanel.add(new JLabel("Rechercher:"));
        searchField = new JTextField(25);
        controlPanel.add(searchField);
        
        searchButton = new JButton("Rechercher");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        controlPanel.add(searchButton);
        
        clearButton = new JButton("Effacer");
        clearButton.setBackground(new Color(220, 220, 220));
        controlPanel.add(clearButton);
        
        // Panel des boutons de sélection
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Sélection"));
        
        selectAllButton = new JButton("Tout sélectionner");
        selectAllButton.setBackground(new Color(100, 149, 237));
        selectAllButton.setForeground(Color.WHITE);
        
        deselectAllButton = new JButton("Tout désélectionner");
        deselectAllButton.setBackground(new Color(240, 128, 128));
        deselectAllButton.setForeground(Color.WHITE);
        
        selectionPanel.add(selectAllButton);
        selectionPanel.add(deselectAllButton);
        
        // Panel des boutons d'export (SUPPRIMÉ "Exporter tous les résultats")
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        exportPanel.setBorder(BorderFactory.createTitledBorder("Export PDF"));
        
        exportSelectedButton = new JButton("Exporter la sélection");
        exportSelectedButton.setBackground(new Color(46, 139, 87));
        exportSelectedButton.setForeground(Color.WHITE);
        
        exportWholeTableButton = new JButton("Exporter toute la table");
        exportWholeTableButton.setBackground(new Color(178, 34, 34));
        exportWholeTableButton.setForeground(Color.WHITE);
        
        exportPanel.add(exportSelectedButton);
        // SUPPRIMÉ: exportAllResultsButton
        exportPanel.add(exportWholeTableButton);
        
        // Panel Nord qui combine les trois panels
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(controlPanel);
        northPanel.add(selectionPanel);
        northPanel.add(exportPanel);
        
        add(northPanel, BorderLayout.NORTH);
        
        // Tableau de résultats
        resultsTable = new JTable();
        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.setRowHeight(25);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Résultats"));
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Barre de statut
        statusLabel = new JLabel("Prêt");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        searchButton.addActionListener(this::performSearch);
        tableComboBox.addActionListener(e -> loadWholeTable());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadWholeTable();
        });
        
        // Recherche avec Enter
        searchField.addActionListener(this::performSearch);
        
        exportSelectedButton.addActionListener(e -> exportSelectedRows());
        exportWholeTableButton.addActionListener(e -> exportWholeTable());
        
        selectAllButton.addActionListener(e -> selectAllRows());
        deselectAllButton.addActionListener(e -> deselectAllRows());
        
        // Double-clic sur une ligne
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = resultsTable.getSelectedRow();
                    if (row != -1) {
                        showDetailsDialog(row);
                    }
                }
            }
        });
    }

    private void performSearch(ActionEvent e) {
        String searchText = searchField.getText().trim();
        String selectedTable = (String) tableComboBox.getSelectedItem();
        
        if (searchText.isEmpty()) {
            loadWholeTable();
            return;
        }
        
        statusLabel.setText("Recherche en cours...");
        searchButton.setEnabled(false);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    currentEntityClass = getEntityClass(selectedTable);
                    GenericDAO<?> dao = new GenericDAO<>(currentEntityClass);
                    currentResults = dao.search(searchText);
                    
                    SwingUtilities.invokeLater(() -> {
                        if (currentResults != null && !currentResults.isEmpty()) {
                            updateTableModel(dao.toTableModel(currentResults));
                            statusLabel.setText("Trouvé " + currentResults.size() + " résultat(s) dans " + selectedTable);
                        } else {
                            resultsTable.setModel(new DefaultTableModel());
                            selectedRows = new ArrayList<>();
                            statusLabel.setText("Aucun résultat trouvé pour: '" + searchText + "'");
                            JOptionPane.showMessageDialog(SearchExplorerPanel.this, 
                                "Aucun résultat trouvé", 
                                "Recherche", JOptionPane.WARNING_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        ex.printStackTrace();
                        statusLabel.setText("Erreur lors de la recherche");
                        JOptionPane.showMessageDialog(SearchExplorerPanel.this, 
                            "Erreur lors de la recherche: " + ex.getMessage(), 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
            
            @Override
            protected void done() {
                searchButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void loadWholeTable() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        statusLabel.setText("Chargement de la table " + selectedTable + "...");
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    currentEntityClass = getEntityClass(selectedTable);
                    GenericDAO<?> dao = new GenericDAO<>(currentEntityClass);
                    currentResults = dao.findAll();
                    
                    SwingUtilities.invokeLater(() -> {
                        if (currentResults != null && !currentResults.isEmpty()) {
                            updateTableModel(dao.toTableModel(currentResults));
                            statusLabel.setText("Table " + selectedTable + " chargée (" + currentResults.size() + " enregistrements)");
                        } else {
                            resultsTable.setModel(new DefaultTableModel());
                            selectedRows = new ArrayList<>();
                            statusLabel.setText("Table " + selectedTable + " est vide");
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        ex.printStackTrace();
                        statusLabel.setText("Erreur lors du chargement");
                        JOptionPane.showMessageDialog(SearchExplorerPanel.this, 
                            "Erreur lors du chargement: " + ex.getMessage() + 
                            "\nVérifiez que la table existe et que Hibernate est configuré.", 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private void updateTableModel(DefaultTableModel originalModel) {
        // Créer un nouveau modèle avec une colonne supplémentaire pour les cases à cocher
        String[] columnNames = new String[originalModel.getColumnCount() + 1];
        columnNames[0] = "Sélection";
        for (int i = 0; i < originalModel.getColumnCount(); i++) {
            columnNames[i + 1] = originalModel.getColumnName(i);
        }
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Seule la colonne de sélection est éditable
            }
        };
        
        // Copier les données
        for (int i = 0; i < originalModel.getRowCount(); i++) {
            Object[] rowData = new Object[originalModel.getColumnCount() + 1];
            rowData[0] = false; // Case à cocher non cochée par défaut
            for (int j = 0; j < originalModel.getColumnCount(); j++) {
                rowData[j + 1] = originalModel.getValueAt(i, j);
            }
            tableModel.addRow(rowData);
        }
        
        resultsTable.setModel(tableModel);
        
        // Initialiser la liste des sélections
        selectedRows = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            selectedRows.add(false);
        }
        
        // Ajouter un écouteur pour mettre à jour les sélections
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 0 && row >= 0 && row < selectedRows.size()) {
                Boolean selected = (Boolean) tableModel.getValueAt(row, column);
                selectedRows.set(row, selected);
            }
        });
        
        // Ajuster la largeur de la colonne de sélection
        TableColumn checkboxColumn = resultsTable.getColumnModel().getColumn(0);
        checkboxColumn.setPreferredWidth(60);
        checkboxColumn.setMaxWidth(60);
    }

    private void exportSelectedRows() {
        if (selectedRows == null || selectedRows.isEmpty() || !selectedRows.contains(true)) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner au moins une ligne à exporter", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Object> selectedEntities = new ArrayList<>();
        for (int i = 0; i < selectedRows.size(); i++) {
            if (selectedRows.get(i) && i < currentResults.size()) {
                selectedEntities.add(currentResults.get(i));
            }
        }
        
        if (selectedEntities.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucune ligne sélectionnée", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(
            currentEntityClass.getSimpleName() + "_selection.pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            
            PdfExporter.exportListToPdf(selectedEntities, fileName);
            JOptionPane.showMessageDialog(this, 
                "Export réussi: " + selectedEntities.size() + " enregistrement(s) exporté(s)",
                "Export PDF", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportWholeTable() {
        try {
            String selectedTable = (String) tableComboBox.getSelectedItem();
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(selectedTable + "_complet.pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".pdf")) {
                    fileName += ".pdf";
                }
                
                Class<?> entityClass = getEntityClass(selectedTable);
                GenericDAO<?> dao = new GenericDAO<>(entityClass);
                List<?> allEntities = dao.findAll();
                
                PdfExporter.exportListToPdf(allEntities, fileName);
                JOptionPane.showMessageDialog(this, 
                    "Export réussi: " + allEntities.size() + " enregistrements exportés",
                    "Export PDF", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'export: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selectAllRows() {
        if (tableModel != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(true, i, 0);
            }
            statusLabel.setText("Toutes les lignes sélectionnées (" + tableModel.getRowCount() + ")");
        }
    }

    private void deselectAllRows() {
        if (tableModel != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                tableModel.setValueAt(false, i, 0);
            }
            statusLabel.setText("Toutes les lignes désélectionnées");
        }
    }

    private void showDetailsDialog(int row) {
        if (currentResults != null && row < currentResults.size()) {
            Object entity = currentResults.get(row);
            JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                        "Détails", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(500, 400);
            
            JTextArea textArea = new JTextArea(entity.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
            
            JButton closeButton = new JButton("Fermer");
            closeButton.addActionListener(e -> dialog.dispose());
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(closeButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private Class<?> getEntityClass(String tableName) {
        switch (tableName) {
            case "Personne": return Personne.class;
            case "Ville": return Ville.class;
            case "Agence": return Agence.class;
            case "Exploitation": return Exploitation.class;
            case "Dirige": return Dirige.class;
            case "EstComptablePour": return EstComptablePour.class;
            case "EstComptableDans": return EstComptableDans.class;
            default: throw new IllegalArgumentException("Table inconnue: " + tableName);
        }
    }
    
    public void setSelectedTable(String tableName) {
        for (int i = 0; i < tableComboBox.getItemCount(); i++) {
            if (tableComboBox.getItemAt(i).equals(tableName)) {
                tableComboBox.setSelectedIndex(i);
                loadWholeTable();
                break;
            }
        }
    }
    
    public void setSearchText(String text) {
        searchField.setText(text);
        if (!text.isEmpty()) {
            performSearch(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "search"));
        }
    }
}