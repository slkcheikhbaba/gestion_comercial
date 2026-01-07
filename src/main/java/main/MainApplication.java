 package main;

import gui.Fenetre;
import javax.swing.SwingUtilities;

public class MainApplication {
    public static void main(String[] args) {
        
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        
        SwingUtilities.invokeLater(() -> {
            Fenetre fenetre = new Fenetre();
            fenetre.setVisible(true);
            System.out.println("🚀 Application Swing démarrée !");
        });
    }
}