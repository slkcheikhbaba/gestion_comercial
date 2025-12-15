package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestion Commerciale - PostgreSQL");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 🎨 Style global
        UIManager.put("TabbedPane.selected", new Color(70, 130, 180));
        UIManager.put("TabbedPane.background", new Color(240, 248, 255));
        UIManager.put("TabbedPane.foreground", Color.BLACK);

        // Barre de menus
        JMenuBar menuBar = new JMenuBar();

        JMenu menuDonnees = new JMenu("Données");
        menuDonnees.add(new JMenuItem("Exporter PDF"));
        menuDonnees.add(new JMenuItem("Actualiser"));
        menuBar.add(menuDonnees);

        JMenu menuAide = new JMenu("Aide");
        menuAide.add(new JMenuItem("Documentation"));
        menuAide.add(new JMenuItem("À propos"));
        menuBar.add(menuAide);

        setJMenuBar(menuBar);

        // Onglets
        JTabbedPane tabbedPane = new JTabbedPane();

        // Onglet Accueil
        JPanel accueilPanel = new JPanel(new BorderLayout());
        JLabel accueilLabel = new JLabel("📊 Application de Gestion Commerciale", SwingConstants.CENTER);
        accueilLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        accueilLabel.setForeground(new Color(30, 60, 120));
        accueilPanel.add(accueilLabel, BorderLayout.CENTER);
        tabbedPane.addTab("Accueil", accueilPanel);

        // Onglet Personnes
        tabbedPane.addTab("Personnes", new PersonnePanel());

        // Onglet Exploitations
        tabbedPane.addTab("Exploitations", new ExploitationPanel());

        // (Tu pourras ajouter plus tard Agences, Villes, Comptables…)
        // tabbedPane.addTab("Agences", new AgencePanel());
        // tabbedPane.addTab("Villes", new VillePanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
