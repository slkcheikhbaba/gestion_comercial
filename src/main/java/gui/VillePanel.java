package gui;

import dao.VilleDAO;
import model.Ville;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VillePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNom, txtCodePostal, txtSearch;
    private JButton btnAjouter, btnModifier, btnEnregistrer, btnSupprimer, btnRechercher, btnActualiser;
    private VilleDAO villeDAO = new VilleDAO();
    private boolean isEditMode = false;
    private Long currentEditId = null;

    public VillePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 250, 240));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // TITRE
        JLabel titre = new JLabel("GESTION DES VILLES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(139, 69, 19));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titre, BorderLayout.NORTH);

        // PANEL CENTRE (Tableau)
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Modèle de tableau
        String[] colonnes = {"ID", "NOM VILLE", "CODE POSTAL"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Table
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(210, 105, 30));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(255, 228, 181));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Renderer pour l'alternance des couleurs
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(255, 228, 181));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(255, 250, 240) : Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(210, 105, 30), 2),
            "Liste des Villes"
        ));
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // PANEL FORMULAIRE (Sud)
        JPanel formPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Informations de la Ville"
        ));
        
        txtNom = createTextField();
        txtCodePostal = createTextField();
        
        formPanel.add(createLabel("Nom Ville *:"));
        formPanel.add(txtNom);
        formPanel.add(createLabel("Code Postal *:"));
        formPanel.add(txtCodePostal);
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        formContainer.add(formPanel, BorderLayout.CENTER);
        add(formContainer, BorderLayout.SOUTH);

        // PANEL BOUTONS (Est)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        btnAjouter = createButton("➕ AJOUTER", new Color(34, 139, 34));
        btnModifier = createButton("✏️ MODIFIER", new Color(30, 144, 255));
        btnEnregistrer = createButton("💾 ENREGISTRER", new Color(255, 140, 0));
        btnSupprimer = createButton("🗑️ SUPPRIMER", new Color(220, 20, 60));
        btnActualiser = createButton("🔄 ACTUALISER", new Color(138, 43, 226));
        
        // Panel recherche
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Recherche"));
        searchPanel.setBackground(Color.WHITE);
        
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRechercher = createButton("🔍 RECHERCHER", new Color(32, 178, 170));
        
        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnRechercher, BorderLayout.EAST);
        
        // Ajout des boutons au panel
        buttonPanel.add(btnAjouter);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnModifier);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnEnregistrer);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnActualiser);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(searchPanel);
        
        add(buttonPanel, BorderLayout.EAST);
        
        // ÉCOUTEURS D'ÉVÉNEMENTS
        btnAjouter.addActionListener(e -> activerAjout());
        btnModifier.addActionListener(e -> activerModification());
        btnEnregistrer.addActionListener(e -> enregistrerVille());
        btnSupprimer.addActionListener(e -> supprimerVille());
        btnRechercher.addActionListener(e -> chercherVille());
        btnActualiser.addActionListener(e -> actualiserTableau());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                remplirChampsDepuisTable();
            }
        });
        
        // CHARGEMENT INITIAL
        chargerDonnees();
        mettreAJourBoutons();
    }
    
    // MÉTHODES AUXILIAIRES
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
    
    // MÉTHODES FONCTIONNELLES
    private void activerAjout() {
        isEditMode = false;
        currentEditId = null;
        viderChamps();
        txtNom.requestFocus();
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
                "Veuillez sélectionner une ville à modifier.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void enregistrerVille() {
        try {
            // Validation des champs
            String nom = txtNom.getText().trim();
            String codePostal = txtCodePostal.getText().trim();
            
            if (nom.isEmpty() || codePostal.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs sont obligatoires !",
                    "Champs manquants",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation code postal (format numérique)
            if (!codePostal.matches("\\d{5}")) {
                JOptionPane.showMessageDialog(this,
                    "Code postal invalide !\nDoit contenir 5 chiffres.\nExemple: 75001",
                    "Code postal incorrect",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!isEditMode) {
                // AJOUT
                Ville nouvelle = new Ville(nom, codePostal);
                
                if (villeDAO.save(nouvelle)) {
                    JOptionPane.showMessageDialog(this,
                        "Ville ajoutée avec succès !\nID : " + nouvelle.getIdVille(),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                }
            } else {
                // MODIFICATION
                if (currentEditId == null) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide pour la modification",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Ville ville = villeDAO.findById(currentEditId);
                if (ville == null) {
                    JOptionPane.showMessageDialog(this,
                        "Ville introuvable dans la base de données",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                ville.setNomVille(nom);
                ville.setCodePostal(codePostal);
                
                if (villeDAO.update(ville)) {
                    JOptionPane.showMessageDialog(this,
                        "Ville modifiée avec succès !",
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
    
    private void supprimerVille() {
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
            
            String nom = model.getValueAt(row, 1).toString();
            String codePostal = model.getValueAt(row, 2).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette ville ?\n\n" +
                "ID : " + id + "\n" +
                "Nom : " + nom + "\n" +
                "Code Postal : " + codePostal,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (villeDAO.deleteById(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Ville supprimée avec succès !",
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
                "Veuillez sélectionner une ville à supprimer.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void chercherVille() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            chargerDonnees();
            return;
        }
        
        List<Ville> resultats = villeDAO.search(searchText);
        model.setRowCount(0);
        
        if (resultats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucun résultat trouvé pour : \"" + searchText + "\"",
                "Recherche",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Ville v : resultats) {
                model.addRow(new Object[]{
                    v.getIdVille(),
                    v.getNomVille(),
                    v.getCodePostal()
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
        txtSearch.setText("");
        isEditMode = false;
        currentEditId = null;
        mettreAJourBoutons();
        JOptionPane.showMessageDialog(this,
            "Tableau actualisé !",
            "Actualisation",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void chargerDonnees() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Ville> villes = villeDAO.findAll();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Ville v : villes) {
                            model.addRow(new Object[]{
                                v.getIdVille(),
                                v.getNomVille(),
                                v.getCodePostal()
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
                    JOptionPane.showMessageDialog(VillePanel.this,
                        "Erreur lors du chargement : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void viderChamps() {
        txtNom.setText("");
        txtCodePostal.setText("");
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
            txtNom.setText(model.getValueAt(row, 1).toString());
            txtCodePostal.setText(model.getValueAt(row, 2).toString());
        }
    }
    
    // GETTER
    public List<Ville> getVilles() {
        return villeDAO.findAll();
    }
}