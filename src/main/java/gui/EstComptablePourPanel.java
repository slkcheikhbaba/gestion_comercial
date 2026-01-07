package gui;

import dao.EstComptablePourDAO;
import dao.PersonneDAO;
import model.EstComptablePour;
import model.Personne;
import util.PdfExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EstComptablePourPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtIdEntite, txtDateDebut, txtSearch;
    private JComboBox<String> comboPersonne, comboTypeEntite;
    private JButton btnAjouter, btnModifier, btnEnregistrer, btnSupprimer, btnRechercher, btnActualiser, btnExportPdf;
    private EstComptablePourDAO comptablePourDAO = new EstComptablePourDAO();
    private PersonneDAO personneDAO = new PersonneDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isEditMode = false;
    private Long currentEditId = null;

    public EstComptablePourPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 255, 240));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        JLabel titre = new JLabel("COMPTABLES POUR LES ENTITÉS", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(0, 100, 0));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titre, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        
        String[] colonnes = {"ID", "PERSONNE", "ID ENTITÉ", "TYPE ENTITÉ", "DATE DÉBUT"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(60, 179, 113));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(144, 238, 144));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(144, 238, 144));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(240, 255, 240) : Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "Liste des Comptables pour les Entités"
        ));
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        
        JPanel formPanel = new JPanel(new GridLayout(4, 4, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Nouveau Lien Comptable pour Entité"
        ));
        
        
        comboPersonne = new JComboBox<>();
        comboTypeEntite = new JComboBox<>();
        chargerComboBox();
        
        txtIdEntite = createTextField();
        txtDateDebut = createTextField();
        
        formPanel.add(createLabel("Personne *:"));
        formPanel.add(comboPersonne);
        formPanel.add(createLabel("ID Entité *:"));
        formPanel.add(txtIdEntite);
        formPanel.add(createLabel("Type Entité *:"));
        formPanel.add(comboTypeEntite);
        formPanel.add(createLabel("Date Début * (YYYY-MM-DD):"));
        formPanel.add(txtDateDebut);
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        formContainer.add(formPanel, BorderLayout.CENTER);
        add(formContainer, BorderLayout.SOUTH);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        btnAjouter = createButton(" AJOUTER", new Color(34, 139, 34));
        btnModifier = createButton(" MODIFIER", new Color(30, 144, 255));
        btnEnregistrer = createButton(" ENREGISTRER", new Color(255, 140, 0));
        btnSupprimer = createButton(" SUPPRIMER", new Color(220, 20, 60));
        btnActualiser = createButton(" ACTUALISER", new Color(138, 43, 226));
        btnExportPdf = createButton(" EXPORT PDF", new Color(139, 0, 139));
        
        
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche"));
        searchPanel.setBackground(Color.WHITE);
        
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRechercher = createButton("🔍 RECHERCHER", new Color(32, 178, 170));
        
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnRechercher, BorderLayout.EAST);
        
       
        buttonPanel.add(btnAjouter);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnModifier);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnEnregistrer);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnActualiser);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnExportPdf);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(searchPanel);
        
        add(buttonPanel, BorderLayout.EAST);
        
        
        btnAjouter.addActionListener(e -> activerAjout());
        btnModifier.addActionListener(e -> activerModification());
        btnEnregistrer.addActionListener(e -> enregistrerLien());
        btnSupprimer.addActionListener(e -> supprimerLien());
        btnRechercher.addActionListener(e -> chercherLien());
        btnActualiser.addActionListener(e -> actualiserTableau());
        btnExportPdf.addActionListener(e -> exporterEnPDF());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                remplirChampsDepuisTable();
            }
        });
        
        
        chargerDonnees();
        mettreAJourBoutons();
    }
    
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }
    
    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return textField;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker()),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 40));
        return button;
    }
    
    private void chargerComboBox() {
        comboPersonne.removeAllItems();
        comboPersonne.addItem("-- Sélectionner une personne --");
        
        comboTypeEntite.removeAllItems();
        comboTypeEntite.addItem("-- Sélectionner un type --");
        comboTypeEntite.addItem("Agence");
        comboTypeEntite.addItem("Exploitation");
        
        try {
            
            List<Personne> personnes = personneDAO.findAll();
            for (Personne p : personnes) {
                comboPersonne.addItem(p.getIdPersonne() + " - " + p.getNomComplet());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur chargement combobox: " + e.getMessage());
        }
    }
    
    
    private void activerAjout() {
        isEditMode = false;
        currentEditId = null;
        viderChamps();
        chargerComboBox();
        mettreAJourBoutons();
        JOptionPane.showMessageDialog(this,
            "Mode AJOUT activé.\nRemplissez les champs et cliquez sur ENREGISTRER.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void activerModification() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            isEditMode = true;
            Object idObj = model.getValueAt(row, 0);
            if (idObj instanceof Long) {
                currentEditId = (Long) idObj;
            } else {
                try {
                    currentEditId = Long.parseLong(idObj.toString());
                } catch (NumberFormatException e) {
                    currentEditId = null;
                }
            }
            remplirChampsDepuisTable();
            mettreAJourBoutons();
        } else {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un lien à modifier.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void enregistrerLien() {
        try {
            
            String idEntiteStr = txtIdEntite.getText().trim();
            String dateStr = txtDateDebut.getText().trim();
            String personneSelection = (String) comboPersonne.getSelectedItem();
            String typeEntiteSelection = (String) comboTypeEntite.getSelectedItem();
            
            if (idEntiteStr.isEmpty() || dateStr.isEmpty() || 
                personneSelection == null || personneSelection.equals(" Sélectionner une personne ") ||
                typeEntiteSelection == null || typeEntiteSelection.equals(" Sélectionner un type ")) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs sont obligatoires !",
                    "Champs manquants",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
           
            Long idEntite;
            try {
                idEntite = Long.parseLong(idEntiteStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "ID Entité doit être un nombre !",
                    "ID Entité invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            Date dateDebut;
            try {
                dateDebut = dateFormat.parse(dateStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide !\nUtilisez : YYYY-MM-DD\nExemple : 2023-12-31",
                    "Date incorrecte",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            
            Integer idPersonne;
            try {
                String[] personneParts = personneSelection.split(" - ");
                idPersonne = Integer.parseInt(personneParts[0]);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Personne invalide",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!isEditMode) {
                
                EstComptablePour nouveau = new EstComptablePour();
                nouveau.setIdEntite(idEntite);
                nouveau.setTypeEntite(typeEntiteSelection);
                nouveau.setDateDebut(dateDebut);
                
                
                Personne personne = personneDAO.findById(idPersonne);
                if (personne == null) {
                    JOptionPane.showMessageDialog(this,
                        "Personne introuvable",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                nouveau.setPersonne(personne);
                
                if (comptablePourDAO.save(nouveau)) {
                    JOptionPane.showMessageDialog(this,
                        "Lien comptable ajouté avec succès !\nID : " + nouveau.getIdComptablePour(),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                }
            } else {
               
                if (currentEditId == null) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide pour la modification",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                EstComptablePour lien = comptablePourDAO.findById(currentEditId);
                if (lien == null) {
                    JOptionPane.showMessageDialog(this,
                        "Lien introuvable dans la base de données",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                lien.setIdEntite(idEntite);
                lien.setTypeEntite(typeEntiteSelection);
                lien.setDateDebut(dateDebut);
                
                
                Personne personne = personneDAO.findById(idPersonne);
                if (personne == null) {
                    JOptionPane.showMessageDialog(this,
                        "Personne introuvable",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                lien.setPersonne(personne);
                
                if (comptablePourDAO.update(lien)) {
                    JOptionPane.showMessageDialog(this,
                        "Lien comptable modifié avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                    isEditMode = false;
                    currentEditId = null;
                    mettreAJourBoutons();
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur : " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void supprimerLien() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Object idObj = model.getValueAt(row, 0);
            Long id = null;
            
            if (idObj instanceof Long) {
                id = (Long) idObj;
            } else {
                try {
                    id = Long.parseLong(idObj.toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String personne = model.getValueAt(row, 1).toString();
            String typeEntite = model.getValueAt(row, 3).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer ce lien comptable ?\n\n" +
                "ID : " + id + "\n" +
                "Personne : " + personne + "\n" +
                "Type Entité : " + typeEntite,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (comptablePourDAO.deleteById(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Lien comptable supprimé avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Échec de la suppression",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner un lien à supprimer.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void chercherLien() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            chargerDonnees();
            return;
        }
        
        List<EstComptablePour> resultats = comptablePourDAO.search(searchText);
        model.setRowCount(0);
        
        if (resultats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucun résultat trouvé pour : \"" + searchText + "\"",
                "Recherche",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (EstComptablePour lien : resultats) {
                model.addRow(new Object[]{
                    lien.getIdComptablePour(),
                    lien.getPersonne() != null ? lien.getPersonne().getNomComplet() : "N/A",
                    lien.getIdEntite(),
                    lien.getTypeEntite(),
                    lien.getDateDebut()
                });
            }
            
            JOptionPane.showMessageDialog(this,
                resultats.size() + " résultat(s) trouvé(s)",
                "Recherche",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void actualiserTableau() {
        chargerDonnees();
        viderChamps();
        chargerComboBox();
        txtSearch.setText("");
        isEditMode = false;
        currentEditId = null;
        mettreAJourBoutons();
    }
    
    private void exporterEnPDF() {
        List<EstComptablePour> liens = getLiensComptablesPour();
        if (liens.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucune donnée à exporter",
                "Export PDF",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter en PDF");
        fileChooser.setSelectedFile(new File("comptables_pour_entites_export.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                PdfExporter.exportListToPdf(liens, filePath);
                
                JOptionPane.showMessageDialog(this,
                    "Export PDF réussi !\nFichier : " + filePath,
                    "Export PDF",
                    JOptionPane.INFORMATION_MESSAGE);
                
                
                int openFile = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous ouvrir le fichier PDF ?",
                    "Ouvrir PDF",
                    JOptionPane.YES_NO_OPTION);
                
                if (openFile == JOptionPane.YES_OPTION) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(new File(filePath));
                    }
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'export PDF : " + e.getMessage(),
                    "Erreur Export",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void chargerDonnees() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<EstComptablePour> liens = comptablePourDAO.findAll();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (EstComptablePour lien : liens) {
                            model.addRow(new Object[]{
                                lien.getIdComptablePour(),
                                lien.getPersonne() != null ? lien.getPersonne().getNomComplet() : "N/A",
                                lien.getIdEntite(),
                                lien.getTypeEntite(),
                                lien.getDateDebut()
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EstComptablePourPanel.this,
                        "Erreur lors du chargement : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void viderChamps() {
        txtIdEntite.setText("");
        txtDateDebut.setText("");
        comboPersonne.setSelectedIndex(0);
        comboTypeEntite.setSelectedIndex(0);
    }
    
    private void mettreAJourBoutons() {
        btnEnregistrer.setEnabled(true);
        btnAjouter.setEnabled(!isEditMode);
        btnModifier.setEnabled(!isEditMode);
        btnSupprimer.setEnabled(!isEditMode);
        
        if (isEditMode) {
            btnEnregistrer.setText("💾 METTRE À JOUR");
            btnEnregistrer.setBackground(new Color(255, 140, 0));
        } else {
            btnEnregistrer.setText("💾 ENREGISTRER");
            btnEnregistrer.setBackground(new Color(60, 179, 113));
        }
    }
    
    private void remplirChampsDepuisTable() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            txtIdEntite.setText(model.getValueAt(row, 2).toString());
            
            Object dateValue = model.getValueAt(row, 4);
            if (dateValue instanceof Date) {
                txtDateDebut.setText(dateFormat.format((Date) dateValue));
            } else {
                txtDateDebut.setText(dateValue.toString());
            }
            
           
        }
    }
    
    // GETTER
    public List<EstComptablePour> getLiensComptablesPour() {
        return comptablePourDAO.findAll();
    }
}