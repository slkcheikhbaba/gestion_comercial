package gui;

import dao.PersonneDAO;
import model.Personne;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PersonnePanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNom, txtPrenom, txtDateNaissance, txtTelephone, txtSearch;
    private JButton btnAjouter, btnModifier, btnEnregistrer, btnSupprimer, btnRechercher, btnActualiser;
    private PersonneDAO personneDAO = new PersonneDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isEditMode = false;
    private Integer currentEditId = null;
    
    public PersonnePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 248, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // TITRE
        JLabel titre = new JLabel("GESTION DES PERSONNES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(25, 25, 112));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titre, BorderLayout.NORTH);
        
        // PANEL CENTRE (Tableau)
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Modèle de tableau
        String[] colonnes = {"ID", "NOM", "PRÉNOM", "DATE NAISSANCE", "TÉLÉPHONE"};
        model = new DefaultTableModel(colonnes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Date.class;
                return Object.class;
            }
        };
        
        // Table
        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(135, 206, 250));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Renderer pour les dates
        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Date) {
                    value = sdf.format((Date) value);
                }
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                
                if (isSelected) {
                    c.setBackground(new Color(135, 206, 250));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(248, 248, 255) : Color.WHITE);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Liste des Personnes"
        ));
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // PANEL FORMULAIRE (Sud)
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "Informations de la Personne"
        ));
        
        // Champs de formulaire
        txtNom = createTextField();
        txtPrenom = createTextField();
        txtDateNaissance = createTextField();
        txtTelephone = createTextField();
        
        formPanel.add(createLabel("Nom *:"));
        formPanel.add(txtNom);
        formPanel.add(createLabel("Prénom *:"));
        formPanel.add(txtPrenom);
        formPanel.add(createLabel("Date Naissance (YYYY-MM-DD) *:"));
        formPanel.add(txtDateNaissance);
        formPanel.add(createLabel("Téléphone *:"));
        formPanel.add(txtTelephone);
        
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
        btnEnregistrer.addActionListener(e -> enregistrerPersonne());
        btnSupprimer.addActionListener(e -> supprimerPersonne());
        btnRechercher.addActionListener(e -> chercherPersonne());
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
            if (idObj instanceof Integer) {
                currentEditId = (Integer) idObj;
            } else {
                try {
                    currentEditId = Integer.parseInt(idObj.toString());
                } catch (NumberFormatException e) {
                    currentEditId = null;
                }
            }
            remplirChampsDepuisTable();
            mettreAJourBoutons();
        } else {
            JOptionPane.showMessageDialog(this,
                "Veuillez sélectionner une personne à modifier.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void enregistrerPersonne() {
        try {
            // Validation des champs
            String nom = txtNom.getText().trim();
            String prenom = txtPrenom.getText().trim();
            String dateStr = txtDateNaissance.getText().trim();
            String telephone = txtTelephone.getText().trim();
            
            if (nom.isEmpty() || prenom.isEmpty() || dateStr.isEmpty() || telephone.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs marqués d'un * sont obligatoires !",
                    "Champs manquants",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Validation de la date
            Date dateNaissance;
            try {
                dateNaissance = dateFormat.parse(dateStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Format de date invalide !\nUtilisez le format : YYYY-MM-DD\nExemple : 1990-12-31",
                    "Date incorrecte",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!isEditMode) {
                // AJOUT
                Personne nouvelle = new Personne(nom, prenom, dateNaissance, telephone);
                if (personneDAO.save(nouvelle)) {
                    JOptionPane.showMessageDialog(this,
                        "Personne ajoutée avec succès !\nID : " + nouvelle.getIdPersonne(),
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
                
                Personne personne = personneDAO.findById(currentEditId);
                if (personne == null) {
                    JOptionPane.showMessageDialog(this,
                        "Personne introuvable dans la base de données",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                personne.setNom(nom);
                personne.setPrenom(prenom);
                personne.setDateNaissance(dateNaissance);
                personne.setTelephone(telephone);
                
                if (personneDAO.update(personne)) {
                    JOptionPane.showMessageDialog(this,
                        "Personne modifiée avec succès !",
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
    
    private void supprimerPersonne() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Object idObj = model.getValueAt(row, 0);
            Integer id = null;
            
            if (idObj instanceof Integer) {
                id = (Integer) idObj;
            } else {
                try {
                    id = Integer.parseInt(idObj.toString());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "ID invalide",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String nom = model.getValueAt(row, 1).toString();
            String prenom = model.getValueAt(row, 2).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette personne ?\n\n" +
                "ID : " + id + "\n" +
                "Nom : " + nom + " " + prenom,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (personneDAO.deleteById(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Personne supprimée avec succès !",
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
                "Veuillez sélectionner une personne à supprimer.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void chercherPersonne() {
        String searchText = txtSearch.getText().trim();
        if (searchText.isEmpty()) {
            chargerDonnees();
            return;
        }
        
        List<Personne> resultats = personneDAO.search(searchText);
        model.setRowCount(0);
        
        if (resultats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucun résultat trouvé pour : \"" + searchText + "\"",
                "Recherche",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Personne p : resultats) {
                model.addRow(new Object[]{
                    p.getIdPersonne(),
                    p.getNom(),
                    p.getPrenom(),
                    p.getDateNaissance(),
                    p.getTelephone()
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
    }
    
    private void chargerDonnees() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Personne> personnes = personneDAO.findAll();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Personne p : personnes) {
                            model.addRow(new Object[]{
                                p.getIdPersonne(),
                                p.getNom(),
                                p.getPrenom(),
                                p.getDateNaissance(),
                                p.getTelephone()
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
                    JOptionPane.showMessageDialog(PersonnePanel.this,
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
        txtPrenom.setText("");
        txtDateNaissance.setText("");
        txtTelephone.setText("");
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
            txtPrenom.setText(model.getValueAt(row, 2).toString());
            
            Object dateValue = model.getValueAt(row, 3);
            if (dateValue instanceof Date) {
                txtDateNaissance.setText(dateFormat.format((Date) dateValue));
            } else {
                txtDateNaissance.setText(dateValue.toString());
            }
            
            txtTelephone.setText(model.getValueAt(row, 4).toString());
        }
    }
    
    // GETTER
    public List<Personne> getPersonnes() {
        return personneDAO.findAll();
    }
}