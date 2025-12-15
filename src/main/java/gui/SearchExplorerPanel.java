package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

import dao.GenericDAO;
import model.*;
import util.PdfExporter;

public class SearchExplorerPanel extends JPanel {
    private JComboBox<String> tableComboBox;
    private JTextField searchField;
    private JButton searchButton;
    private JButton exportOneButton;
    private JButton exportAllResultsButton;
    private JButton exportWholeTableButton;
    private JButton clearButton;
    private JTable resultsTable;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    private GenericDAO<?> currentDao;
    private List<?> currentResults;
    private Class<?> currentEntityClass;

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
        
        // Panel des boutons d'export
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        exportPanel.setBorder(BorderFactory.createTitledBorder("Export PDF"));
        
        exportOneButton = new JButton("Exporter la sélection");
        exportOneButton.setBackground(new Color(46, 139, 87));
        exportOneButton.setForeground(Color.WHITE);
        exportAllResultsButton = new JButton("Exporter tous les résultats");
        exportAllResultsButton.setBackground(new Color(65, 105, 225));
        exportAllResultsButton.setForeground(Color.WHITE);
        exportWholeTableButton = new JButton("Exporter toute la table");
        exportWholeTableButton.setBackground(new Color(178, 34, 34));
        exportWholeTableButton.setForeground(Color.WHITE);
        
        exportPanel.add(exportOneButton);
        exportPanel.add(exportAllResultsButton);
        exportPanel.add(exportWholeTableButton);
        
        // Panel Nord qui combine les deux
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(controlPanel, BorderLayout.NORTH);
        northPanel.add(exportPanel, BorderLayout.SOUTH);
        
        add(northPanel, BorderLayout.NORTH);
        
        // Tableau de résultats
        resultsTable = new JTable();
        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        
        exportOneButton.addActionListener(e -> exportSelectedRow());
        exportAllResultsButton.addActionListener(e -> exportAllResults());
        exportWholeTableButton.addActionListener(e -> exportWholeTable());
        
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
        
        // Afficher un indicateur de chargement
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
                            DefaultTableModel model = dao.toTableModel(currentResults);
                            resultsTable.setModel(model);
                            statusLabel.setText("Trouvé " + currentResults.size() + " résultat(s) dans " + selectedTable);
                            
                            JOptionPane.showMessageDialog(SearchExplorerPanel.this, 
                                "Trouvé " + currentResults.size() + " résultat(s)", 
                                "Recherche", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            resultsTable.setModel(new DefaultTableModel());
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
                            DefaultTableModel model = dao.toTableModel(currentResults);
                            resultsTable.setModel(model);
                            statusLabel.setText("Table " + selectedTable + " chargée (" + currentResults.size() + " enregistrements)");
                        } else {
                            resultsTable.setModel(new DefaultTableModel());
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

    private void exportSelectedRow() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une ligne à exporter", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (currentResults != null && selectedRow < currentResults.size()) {
            Object entity = currentResults.get(selectedRow);
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new java.io.File(
                currentEntityClass.getSimpleName() + "_" + (selectedRow + 1) + ".pdf"));
            
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();
                if (!fileName.toLowerCase().endsWith(".pdf")) {
                    fileName += ".pdf";
                }
                
                PdfExporter.exportEntityToPdf(entity, fileName);
                JOptionPane.showMessageDialog(this, 
                    "Export réussi: " + fileChooser.getSelectedFile().getName(),
                    "Export PDF", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void exportAllResults() {
        if (currentResults == null || currentResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun résultat à exporter", 
                "Avertissement", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File(
            currentEntityClass.getSimpleName() + "_resultats.pdf"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            
            PdfExporter.exportListToPdf(currentResults, fileName);
            JOptionPane.showMessageDialog(this, 
                "Export réussi: " + currentResults.size() + " enregistrements exportés",
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
    
    // Méthode pour sélectionner une table spécifique depuis Fenetre.java
    public void setSelectedTable(String tableName) {
        for (int i = 0; i < tableComboBox.getItemCount(); i++) {
            if (tableComboBox.getItemAt(i).equals(tableName)) {
                tableComboBox.setSelectedIndex(i);
                loadWholeTable();
                break;
            }
        }
    }
    
    // Méthode pour définir le texte de recherche
    public void setSearchText(String text) {
        searchField.setText(text);
        if (!text.isEmpty()) {
            performSearch(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "search"));
        }
    }
}