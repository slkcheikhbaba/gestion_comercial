package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    
    private static SessionFactory sessionFactory;
    
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                
                Configuration configuration = new Configuration();
                

                configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
                configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/gestion_commerciale");
                configuration.setProperty("hibernate.connection.username", "postgres");
                configuration.setProperty("hibernate.connection.password", "7968");
                
             
                configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                configuration.setProperty("hibernate.show_sql", "true");
                configuration.setProperty("hibernate.format_sql", "true");
                configuration.setProperty("hibernate.hbm2ddl.auto", "update");
                
                
                configuration.addAnnotatedClass(model.Personne.class);
                configuration.addAnnotatedClass(model.Ville.class);
                configuration.addAnnotatedClass(model.Agence.class);
                configuration.addAnnotatedClass(model.Exploitation.class);
                configuration.addAnnotatedClass(model.Dirige.class);
                configuration.addAnnotatedClass(model.EstComptablePour.class);
                configuration.addAnnotatedClass(model.EstComptableDans.class);
                
               
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
                
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                
                System.out.println(" Hibernate SessionFactory créée avec succès");
                
            } catch (Exception e) {
                System.err.println(" Échec création SessionFactory : " + e.getMessage());
                e.printStackTrace();
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;
    }
    
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println(" Hibernate SessionFactory fermée");
        }
    }
}