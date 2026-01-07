package gui;

import dao.AgenceDAO;
import model.Agence;
import util.PdfExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AgencePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNom, txtAdresse, txtTelephone, txtEmail, txtVille;
    private JButton btnAjouter, btnModifier, btnEnregistrer, btnSupprimer, btnActualiser, btnExportPdf;
    private AgenceDAO agenceDAO = new AgenceDAO();
    private boolean isEditMode = false;
    private Long currentEditId = null;

    public AgencePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 255));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        
        JLabel titre = new JLabel("GESTION DES AGENCES", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 24));
        titre.setForeground(new Color(25, 25, 112));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(titre, BorderLayout.NORTH);

        
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        
        String[] colonnes = {"ID", "NOM AGENCE", "ADRESSE", "TÉLÉPHONE", "EMAIL", "ID VILLE"};
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
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(135, 206, 250));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
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
            "Liste des Agences"
        ));
        
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "Informations de l'Agence"
        ));
        
       
        txtNom = createTextField();
        txtAdresse = createTextField();
        txtTelephone = createTextField();
        txtEmail = createTextField();
        txtVille = createTextField();
        
        formPanel.add(createLabel("Nom Agence *:"));
        formPanel.add(txtNom);
        formPanel.add(createLabel("Adresse:"));
        formPanel.add(txtAdresse);
        formPanel.add(createLabel("Téléphone:"));
        formPanel.add(txtTelephone);
        formPanel.add(createLabel("Email *:"));
        formPanel.add(txtEmail);
        formPanel.add(createLabel("ID Ville *:"));
        formPanel.add(txtVille);
        
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        formContainer.add(formPanel, BorderLayout.CENTER);
        add(formContainer, BorderLayout.SOUTH);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        btnAjouter = createButton("➕ AJOUTER", new Color(34, 139, 34));
        btnModifier = createButton("✏️ MODIFIER", new Color(30, 144, 255));
        btnEnregistrer = createButton("💾 ENREGISTRER", new Color(255, 140, 0));
        btnSupprimer = createButton("🗑️ SUPPRIMER", new Color(220, 20, 60));
        btnActualiser = createButton("🔄 ACTUALISER", new Color(138, 43, 226));
        btnExportPdf = createButton("📄 EXPORT PDF", new Color(139, 0, 139));
        
       
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
        
        
        btnAjouter.addActionListener(e -> activerAjout());
        btnModifier.addActionListener(e -> activerModification());
        btnEnregistrer.addActionListener(e -> enregistrerAgence());
        btnSupprimer.addActionListener(e -> supprimerAgence());
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
                "Veuillez sélectionner une agence à modifier.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void enregistrerAgence() {
        try {
            
            String nom = txtNom.getText().trim();
            String adresse = txtAdresse.getText().trim();
            String telephone = txtTelephone.getText().trim();
            String email = txtEmail.getText().trim();
            String villeStr = txtVille.getText().trim();

            
            if (nom.isEmpty() || villeStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Les champs marqués d'un * sont obligatoires !",
                    "Champs manquants",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

           
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "L'email est obligatoire !",
                    "Email manquant",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            
            String emailRegex = "^[A-Za-z0-9+_.-]+@agence\\.mr$";
            if (!email.matches(emailRegex)) {
                JOptionPane.showMessageDialog(this,
                    "L'email doit se terminer par @agence.mr\nExemple : contact@agence.mr",
                    "Email invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

           
            Long idVille;
            try {
                idVille = Long.parseLong(villeStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "ID Ville doit être un nombre !",
                    "ID Ville invalide",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isEditMode) {
               
                Agence nouvelle = new Agence(nom, idVille);
                nouvelle.setAdresse(adresse);
                nouvelle.setTelephone(telephone);
                nouvelle.setEmail(email);
                
                if (agenceDAO.save(nouvelle)) {
                    JOptionPane.showMessageDialog(this,
                        "Agence ajoutée avec succès !\nID : " + nouvelle.getIdAgence(),
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
                
                Agence agence = agenceDAO.findById(currentEditId);
                if (agence == null) {
                    JOptionPane.showMessageDialog(this,
                        "Agence introuvable dans la base de données",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                agence.setNomAgence(nom);
                agence.setAdresse(adresse);
                agence.setTelephone(telephone);
                agence.setEmail(email);
                agence.setIdVille(idVille);
                
                if (agenceDAO.update(agence)) {
                    JOptionPane.showMessageDialog(this,
                        "Agence modifiée avec succès !",
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
    
    private void supprimerAgence() {
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
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir supprimer cette agence ?\n\n" +
                "ID : " + id + "\n" +
                "Nom : " + nom,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (agenceDAO.deleteById(id)) {
                    JOptionPane.showMessageDialog(this,
                        "Agence supprimée avec succès !",
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
                "Veuillez sélectionner une agence à supprimer.",
                "Aucune sélection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void actualiserTableau() {
        chargerDonnees();
        viderChamps();
        isEditMode = false;
        currentEditId = null;
        mettreAJourBoutons();
    }
    
    private void chargerDonnees() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    List<Agence> agences = agenceDAO.findAll();
                    SwingUtilities.invokeLater(() -> {
                        model.setRowCount(0);
                        for (Agence a : agences) {
                            model.addRow(new Object[]{
                                a.getIdAgence(),
                                a.getNomAgence(),
                                a.getAdresse(),
                                a.getTelephone(),
                                a.getEmail(),
                                a.getIdVille()
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
                    JOptionPane.showMessageDialog(AgencePanel.this,
                        "Erreur lors du chargement : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    private void exporterEnPDF() {
        List<Agence> agences = agenceDAO.findAll();
        if (agences.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Aucune donnée à exporter",
                "Export PDF",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exporter en PDF");
        fileChooser.setSelectedFile(new File("agences_export.pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                PdfExporter.exportListToPdf(agences, filePath);
                
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
        txtAdresse.setText("");
        txtTelephone.setText("");
        txtEmail.setText("");
        txtVille.setText("");
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
            txtAdresse.setText(model.getValueAt(row, 2).toString());
            txtTelephone.setText(model.getValueAt(row, 3).toString());
            txtEmail.setText(model.getValueAt(row, 4).toString());
            txtVille.setText(model.getValueAt(row, 5).toString());
        }
    }
    
   
    public List<Agence> getAgences() {
        return agenceDAO.findAll();
    }
}