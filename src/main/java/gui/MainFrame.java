package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Gestion Commerciale - PostgreSQL");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        UIManager.put("TabbedPane.selected", new Color(70, 130, 180));
        UIManager.put("TabbedPane.background", new Color(240, 248, 255));
        UIManager.put("TabbedPane.foreground", Color.BLACK);

        
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

        
        JTabbedPane tabbedPane = new JTabbedPane();

        
        JPanel accueilPanel = new JPanel(new BorderLayout());
        JLabel accueilLabel = new JLabel("📊 Application de Gestion Commerciale", SwingConstants.CENTER);
        accueilLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        accueilLabel.setForeground(new Color(30, 60, 120));
        accueilPanel.add(accueilLabel, BorderLayout.CENTER);
        tabbedPane.addTab("Accueil", accueilPanel);

        
        tabbedPane.addTab("Personnes", new PersonnePanel());

        
        tabbedPane.addTab("Exploitations", new ExploitationPanel());

        
        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
