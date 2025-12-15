package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import model.*;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Personne.class)
                    .addAnnotatedClass(Ville.class)
                    .addAnnotatedClass(Agence.class)
                    .addAnnotatedClass(Exploitation.class)
                    .addAnnotatedClass(Dirige.class)
                    .addAnnotatedClass(EstComptablePour.class)
                    .addAnnotatedClass(EstComptableDans.class);

            SessionFactory factory = configuration.buildSessionFactory();
            System.out.println("✅ Hibernate SessionFactory créée avec succès");
            return factory;

        } catch (Throwable ex) {
            System.err.println("❌ Échec de création de SessionFactory : " + ex.getMessage());
            ex.printStackTrace();
            throw new ExceptionInInitializerError("Erreur Hibernate : " + ex.getMessage());
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("✅ Hibernate SessionFactory fermée proprement");
        }
    }
}
