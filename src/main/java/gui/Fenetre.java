package gui;

import javax.swing.*;
import java.awt.*;
import util.HibernateUtil;

public class Fenetre extends JFrame {

    public Fenetre() {

        setTitle("Gestion commerciale - PostgreSQL");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ✅ Barre de menu
        JMenuBar menuBar = new JMenuBar();

        JMenu mData = new JMenu("Données");
        JMenuItem miExplorer = new JMenuItem("Explorer / Rechercher / Exporter");
        JMenuItem miPersonnes = new JMenuItem("Gérer les personnes");
        JMenuItem miVilles = new JMenuItem("Gérer les villes");
        JMenuItem miAgences = new JMenuItem("Gérer les agences");
        JMenuItem miExploitations = new JMenuItem("Gérer les exploitations");
        JMenuItem miDirections = new JMenuItem("Gérer les directions");
        JMenuItem miComptablesDans = new JMenuItem("Gérer les comptables dans agences");
        JMenuItem miComptablesPour = new JMenuItem("Gérer les comptables pour entités");

        mData.add(miPersonnes);
        mData.add(miVilles);
        mData.add(miAgences);
        mData.add(miExploitations);
        mData.add(new JSeparator());
        mData.add(miDirections);
        mData.add(miComptablesDans);
        mData.add(miComptablesPour);
        mData.addSeparator();
        mData.add(miExplorer);

        menuBar.add(mData);

        JMenu mHelp = new JMenu("Aide");
        JMenuItem miAbout = new JMenuItem("À propos");
        mHelp.add(miAbout);
        menuBar.add(mHelp);

        setJMenuBar(menuBar);

        // ✅ Panneau principal avec onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Accueil", createHomePanel());
        tabbedPane.addTab("Personnes", new PersonnePanel());
        tabbedPane.addTab("Agences", new AgencePanel());
        tabbedPane.addTab("Villes", new VillePanel());
        tabbedPane.addTab("Exploitations", new ExploitationPanel());
        tabbedPane.addTab("Directions", new DirigePanel());
        tabbedPane.addTab("Comptables dans agences", new EstComptableDansPanel());
        tabbedPane.addTab("Comptables pour entités", new EstComptablePourPanel());

        setContentPane(tabbedPane);

        // ✅ Actions des menus
        miExplorer.addActionListener(e -> {
            SearchExplorerPanel explorer = new SearchExplorerPanel();
            JDialog dlg = new JDialog(this, "Explorateur des données", true);
            dlg.setContentPane(explorer);
            dlg.setSize(900, 600);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        });

        miPersonnes.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        miAgences.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        miVilles.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        miExploitations.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        miDirections.addActionListener(e -> tabbedPane.setSelectedIndex(5));
        miComptablesDans.addActionListener(e -> tabbedPane.setSelectedIndex(6));
        miComptablesPour.addActionListener(e -> tabbedPane.setSelectedIndex(7));

        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Application de Gestion Commerciale\n" +
                "Version 1.0\n" +
                "Développée avec Java Swing et PostgreSQL\n\n" +
                "Tables disponibles:\n" +
                "• Personnes\n" +
                "• Villes\n" +
                "• Agences\n" +
                "• Exploitations\n" +
                "• Directions\n" +
                "• Comptables dans agences\n" +
                "• Comptables pour entités",
                "À propos", JOptionPane.INFORMATION_MESSAGE));

        // ✅ Vérifier la connexion au démarrage
        checkDatabaseConnection();
    }

    private JPanel createHomePanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Application de Gestion Commerciale", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 70, 140));

        JLabel subtitleLabel = new JLabel("Gestion complète de votre activité commerciale", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel infoPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        infoPanel.add(new JLabel("✓ Gestion des personnes (clients, employés)"));
        infoPanel.add(new JLabel("✓ Gestion des villes et agences"));
        infoPanel.add(new JLabel("✓ Gestion des exploitations"));
        infoPanel.add(new JLabel("✓ Gestion des directions"));
        infoPanel.add(new JLabel("✓ Gestion des comptables dans les agences"));
        infoPanel.add(new JLabel("✓ Gestion des comptables pour les entités"));
        infoPanel.add(new JLabel("✓ Recherche et export de données"));
        infoPanel.add(new JLabel("✓ Export PDF pour tous les enregistrements"));
        infoPanel.add(new JLabel("✓ Interface intuitive avec onglets"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton quickAccessBtn = new JButton("Accéder à l'explorateur de données");
        quickAccessBtn.addActionListener(e -> {
            SearchExplorerPanel explorer = new SearchExplorerPanel();
            JDialog dlg = new JDialog(this, "Explorateur des données", true);
            dlg.setContentPane(explorer);
            dlg.setSize(900, 600);
            dlg.setLocationRelativeTo(this);
            dlg.setVisible(true);
        });
        
        JButton statsBtn = new JButton("Voir les statistiques");
        statsBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Statistiques de l'application:\n\n" +
                "• 7 tables de données disponibles\n" +
                "• Interface en onglets pour navigation facile\n" +
                "• Export PDF pour toutes les tables\n" +
                "• Recherche avancée dans toutes les données\n" +
                "• Connexion PostgreSQL avec Hibernate",
                "Statistiques", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(quickAccessBtn);
        buttonPanel.add(statsBtn);

        // Panel d'accueil organisé
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(subtitleLabel, BorderLayout.NORTH);
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void checkDatabaseConnection() {
        try {
            HibernateUtil.getSessionFactory().openSession().close();
            JOptionPane.showMessageDialog(this,
                    "Connexion à la base de données réussie",
                    "Connexion", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données:\n" + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            Fenetre fenetre = new Fenetre();
            fenetre.setVisible(true);
        });
    }
}