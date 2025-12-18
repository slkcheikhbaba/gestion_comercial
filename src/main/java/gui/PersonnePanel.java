package gui;

import dao.PersonneDAO;
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

// Import pour JDateChooser
import com.toedter.calendar.JDateChooser;

public class PersonnePanel extends JPanel {
    
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNom, txtPrenom, txtTelephone;
    private JDateChooser dateChooser;  // Remplace JTextField txtDateNaissance
    private JButton btnAjouter, btnModifier, btnEnregistrer, btnSupprimer, btnActualiser, btnExportPdf;
    private PersonneDAO personneDAO = new PersonneDAO();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
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
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "Informations de la Personne"
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Créer le JDateChooser (calendrier)
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 12));
        dateChooser.getJCalendar().setFont(new Font("Arial", Font.PLAIN, 10));
        dateChooser.setPreferredSize(new Dimension(150, 30));
        
        // Champs de formulaire
        txtNom = createTextField();
        txtPrenom = createTextField();
        txtTelephone = createTextField();
        
        // Nom
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Nom *:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtNom, gbc);
        
        // Prénom
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Prénom *:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtPrenom, gbc);
        
        // Date de Naissance
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Date Naissance *:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(dateChooser, gbc);
        
        // Téléphone
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(createLabel("Téléphone *:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtTelephone, gbc);
        
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
        btnExportPdf = createButton("📄 EXPORT PDF", new Color(139, 0, 139));
        
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
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(btnExportPdf);
        
        add(buttonPanel, BorderLayout.EAST);
        
        // ÉCOUTEURS D'ÉVÉNEMENTS
        btnAjouter.addActionListener(e -> activerAjout());
        btnModifier.addActionListener(e -> activerModification());
        btnEnregistrer.addActionListener(e -> enregistrerPersonne());
        btnSupprimer.addActionListener(e -> supprimerPersonne());
        btnActualiser.addActionListener(e -> actualiserTableau());
        btnExportPdf.addActionListener(e -> exporterEnPDF());
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                remplirChampsDepuisTable();
            }
        });
        
        // TEST HIBERNATE AU DÉMARRAGE
        testHibernateConnection();
        
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
    
    // TEST CONNEXION HIBERNATE
    private void testHibernateConnection() {
        System.out.println("\n🔍 TEST HIBERNATE DANS PERSONNEPANEL");
        System.out.println("===================================");
        
        // Test 1: Connexion via DAO
        boolean connected = personneDAO.testConnection();
        System.out.println("Connexion DAO: " + (connected ? "✅ OK" : "❌ ÉCHEC"));
        
        // Test 2: Debug de la base
        personneDAO.debugDatabase();
        
        // Test 3: Comptage
        long count = personneDAO.count();
        System.out.println("Nombre de personnes dans la base: " + count);
        
        if (count == 0) {
            // Message d'information
            JOptionPane.showMessageDialog(this,
                "La base de données est vide.\nCliquez sur AJOUTER pour créer une nouvelle personne.",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        }
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
            String telephone = txtTelephone.getText().trim();
            
            // Récupération de la date depuis JDateChooser
            Date dateNaissance = dateChooser.getDate();
            
            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || telephone.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Tous les champs marqués d'un * sont obligatoires !\n" +
                    "Veuillez sélectionner une date de naissance avec le calendrier.",
                    "Champs manquants",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!isEditMode) {
                // AJOUT
                Personne nouvelle = new Personne(nom, prenom, dateNaissance, telephone);
                System.out.println("🔵 Tentative d'ajout Hibernate: " + nouvelle.getNom() + " " + nouvelle.getPrenom());
                
                if (personneDAO.save(nouvelle)) {
                    JOptionPane.showMessageDialog(this,
                        "Personne ajoutée avec succès !\nID : " + nouvelle.getIdPersonne() +
                        "\nDate : " + displayFormat.format(dateNaissance),
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                    
                    // Debug après ajout
                    System.out.println("✅ Ajout réussi, debug après ajout:");
                    personneDAO.debugDatabase();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Échec de l'ajout dans la base de données.\nVérifiez les logs Hibernate.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
                
                System.out.println("🔵 Tentative de modification ID: " + currentEditId);
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
                    
                    System.out.println("✅ Modification réussie");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Échec de la modification",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
                System.out.println("🔵 Tentative de suppression ID: " + id);
                
                if (personneDAO.deleteById(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Personne supprimée avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                    chargerDonnees();
                    viderChamps();
                    
                    System.out.println("✅ Suppression réussie");
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
    
    private void actualiserTableau() {
        System.out.println("🔄 Actualisation du tableau...");
        chargerDonnees();
        viderChamps();
        isEditMode = false;
        currentEditId = null;
        mettreAJourBoutons();
        
        // Debug après actualisation
        personneDAO.debugDatabase();
    }
    
    private void chargerDonnees() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    System.out.println("📥 Chargement des données depuis Hibernate...");
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
                        System.out.println("✅ " + personnes.size() + " personne(s) chargée(s) dans le tableau");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(PersonnePanel.this,
                            "Erreur lors du chargement des données : " + e.getMessage(),
                            "Erreur Hibernate",
                            JOptionPane.ERROR_MESSAGE);
                    });
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
    
    private void exporterEnPDF() {
        List<Personne> personnes = personneDAO.findAll();
        if (personnes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucune donnée à exporter",
                "Export PDF",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter en PDF");
        fileChooser.setSelectedFile(new File("personnes_export.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            // Ajouter l'extension .pdf si nécessaire
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                // Export PDF
                PdfExporter.exportPersonnesToPdf(personnes, filePath);
                
                JOptionPane.showMessageDialog(this,
                    "Export PDF réussi !\nFichier : " + filePath,
                    "Export PDF",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Ouvrir le fichier si demandé
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
    
    private void viderChamps() {
        txtNom.setText("");
        txtPrenom.setText("");
        txtTelephone.setText("");
        dateChooser.setDate(null);  // Vider le calendrier
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
            txtTelephone.setText(model.getValueAt(row, 4).toString());
            
            // Remplir le JDateChooser
            Object dateValue = model.getValueAt(row, 3);
            if (dateValue instanceof Date) {
                Date date = (Date) dateValue;
                dateChooser.setDate(date);
            }
        }
    }
    
    // MÉTHODE DE TEST DIRECTE
    public void testAjoutDirect() {
        System.out.println("\n🧪 TEST AJOUT DIRECT");
        System.out.println("====================");
        
        Personne test = new Personne();
        test.setNom("TEST_DIRECT");
        test.setPrenom("Test");
        test.setDateNaissance(new Date());
        test.setTelephone("0000000000");
        
        if (personneDAO.save(test)) {
            System.out.println("✅ Test ajout direct réussi, ID: " + test.getIdPersonne());
            
            // Actualiser l'affichage
            SwingUtilities.invokeLater(() -> {
                chargerDonnees();
                JOptionPane.showMessageDialog(this,
                    "Test réussi ! Personne ajoutée avec ID: " + test.getIdPersonne(),
                    "Test Hibernate",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            System.err.println("❌ Test ajout direct échoué");
            JOptionPane.showMessageDialog(this,
                "Échec du test d'ajout Hibernate",
                "Erreur Test",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // GETTER
    public List<Personne> getPersonnes() {
        return personneDAO.findAll();
    }
}