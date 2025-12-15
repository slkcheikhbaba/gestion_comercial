package base_de_donner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private static String host = "localhost";
    private static String port = "5432";
    private static String username = "postgres";
    private static String password = "7968";
    private static String db = "gestion_commerciale";

    public static Connection getConnection() {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + db;
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion à PostgreSQL réussie ");
            return connection;
        } catch (ClassNotFoundException ex) {
            System.err.println("Driver PostgreSQL non trouvé ");
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            System.err.println("Erreur de connexion à la base de données ");
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            System.out.println(" Test de connexion réussi ");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Échec de la connexion");
        }
    }
}