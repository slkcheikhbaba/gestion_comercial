package dao;

import model.Personne;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonneDAO {
    
    
    public boolean save(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println(" Tentative d'ajout de : " + personne.getNom() + " " + personne.getPrenom());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
           
            session.persist(personne);
            
            transaction.commit();
            success = true;
            
           
            session.flush();
            System.out.println(" Personne ajoutée avec ID: " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println(" Erreur save(): " + e.getMessage());
            e.printStackTrace();
            
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }
    
    
    public List<Personne> findAll() {
        Session session = null;
        List<Personne> personnes = new ArrayList<>();
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            Query<Personne> query = session.createQuery(
                "FROM Personne p ORDER BY p.nom, p.prenom", 
                Personne.class
            );
            personnes = query.list();
            
            System.out.println(" " + personnes.size() + " personne(s) trouvée(s)");
            
        } catch (Exception e) {
            System.err.println(" Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        
        return personnes;
    }
    
   
    public Personne findById(Integer id) {
        Session session = null;
        Personne personne = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            personne = session.get(Personne.class, id);
            
            if (personne != null) {
                System.out.println(" Personne trouvée ID " + id + ": " + personne.getNom());
            } else {
                System.out.println(" Personne ID " + id + " non trouvée");
            }
            
        } catch (Exception e) {
            System.err.println(" Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return personne;
    }
    
    
    public boolean update(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println(" Tentative de mise à jour ID: " + personne.getIdPersonne());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
           
            session.merge(personne);
            
            transaction.commit();
            success = true;
            
            System.out.println(" Personne mise à jour ID " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println(" Erreur update(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }
    
    
    public boolean delete(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println(" Tentative de suppression ID: " + personne.getIdPersonne());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            
            Personne attachedPersonne = session.get(Personne.class, personne.getIdPersonne());
            if (attachedPersonne != null) {
                session.remove(attachedPersonne);
            }
            
            transaction.commit();
            success = true;
            
            System.out.println(" Personne supprimée ID " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println(" Erreur delete(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }
    
    
    public boolean deleteById(Integer id) {
        Personne personne = findById(id);
        if (personne != null) {
            return delete(personne);
        }
        System.err.println(" Personne ID " + id + " non trouvée pour suppression");
        return false;
    }
    
    
    public List<Personne> search(String searchText) {
        Session session = null;
        List<Personne> personnes = new ArrayList<>();
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            String searchPattern = "%" + searchText + "%";
            
            Query<Personne> query = session.createQuery(
                "FROM Personne p WHERE LOWER(p.nom) LIKE LOWER(:search) " +
                "OR LOWER(p.prenom) LIKE LOWER(:search) " +
                "OR p.telephone LIKE :search " +
                "ORDER BY p.nom, p.prenom", 
                Personne.class
            );
            query.setParameter("search", searchPattern);
            
            personnes = query.list();
            
            System.out.println(" " + personnes.size() + " résultat(s) pour '" + searchText + "'");
            
        } catch (Exception e) {
            System.err.println(" Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return personnes;
    }
    
    
    public long count() {
        Session session = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            
            Query<Long> query = session.createQuery(
                "SELECT COUNT(p) FROM Personne p", 
                Long.class
            );
            Long result = query.uniqueResult();
            
            return result != null ? result : 0;
            
        } catch (Exception e) {
            System.err.println(" Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    
    public boolean exists(Integer id) {
        return findById(id) != null;
    }
    
    
    public boolean testConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Test simple de connexion
            Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Personne p", Long.class);
            query.setMaxResults(1);
            Long result = query.uniqueResult();
            System.out.println("🔗 Test connexion OK - Count: " + result);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion Hibernate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    
    public void debugDatabase() {
        System.out.println("\n DEBUG BASE DE DONNÉES HIBERNATE");
        
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            Query<Long> countQuery = session.createQuery("SELECT COUNT(p) FROM Personne p", Long.class);
            Long count = countQuery.uniqueResult();
            System.out.println(" Connexion Hibernate OK");
            System.out.println(" Nombre total de personnes: " + count);
            
            
            if (count > 0) {
                Query<Personne> allQuery = session.createQuery("FROM Personne p", Personne.class);
                List<Personne> personnes = allQuery.list();
                
                System.out.println("\nLISTE DES PERSONNES DANS LA BASE:");
             
                System.out.println("ID | Nom | Prénom | Téléphone | Date Naissance");
              
                
                for (Personne p : personnes) {
                    System.out.println(
                        p.getIdPersonne() + " | " + 
                        p.getNom() + " | " + 
                        p.getPrenom() + " | " + 
                        p.getTelephone() + " | " + 
                        p.getDateNaissance()
                    );
                }
            } else {
                System.out.println("\n La table 'personnes' est vide dans la base de données");
            }
            
            
            System.out.println("\nInformations Session Hibernate:");
            System.out.println("Session ouverte: " + session.isOpen());
            System.out.println("Session connectée: " + session.isConnected());
            
        } catch (Exception e) {
            System.err.println(" Erreur debugDatabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    public void testAjoutDirect() {
        System.out.println("\n TEST AJOUT DIRECT HIBERNATE");
       
        
        Personne testPersonne = new Personne();
        testPersonne.setNom("TEST_HIBERNATE");
        testPersonne.setPrenom("Test");
        testPersonne.setDateNaissance(new Date());
        testPersonne.setTelephone("0000000000");
        
        if (save(testPersonne)) {
            System.out.println(" Test d'ajout Hibernate réussi!");
            System.out.println(" ID généré: " + testPersonne.getIdPersonne());
            
            
            Personne verif = findById(testPersonne.getIdPersonne());
            if (verif != null) {
                System.out.println(" Vérification: Personne trouvée dans la base");
            } else {
                System.err.println(" Vérification: Personne NON trouvée après ajout!");
            }
        } else {
            System.err.println(" Test d'ajout Hibernate échoué!");
        }
    }
    
    
    public static void main(String[] args) {
        System.out.println(" TEST COMPLET PERSONNEDAO AVEC HIBERNATE");
        
        
        PersonneDAO dao = new PersonneDAO();
        
        
        System.out.println("\n1.  TEST CONNEXION HIBERNATE:");
        boolean connected = dao.testConnection();
        System.out.println("Connexion: " + (connected ? " OK" : " ÉCHEC"));
        
        if (!connected) {
            System.err.println(" Impossible de continuer, connexion Hibernate échouée");
            return;
        }
        
        
        System.out.println("\n2.  ÉTAT ACTUEL DE LA BASE:");
        dao.debugDatabase();
        
        
        System.out.println("\n3.  TEST COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de personnes: " + total);
        
        
        if (total == 0) {
            System.out.println("\n4.  TEST AJOUT (base vide):");
            dao.testAjoutDirect();
            
            
            System.out.println("\n5.  ÉTAT APRÈS AJOUT:");
            dao.debugDatabase();
        }
        
        
        System.out.println("\n6.  TEST RECHERCHE:");
        List<Personne> resultats = dao.search("test");
        System.out.println("Résultats recherche 'test': " + resultats.size());
        
        
        System.out.println("\n7.  TEST CRUD COMPLET:");
        testCRUD(dao);
        
        System.out.println("\n TESTS HIBERNATE TERMINÉS");
    }
    
    
    private static void testCRUD(PersonneDAO dao) {
        try {
            
            Personne test = new Personne();
            test.setNom("CRUD");
            test.setPrenom("Test");
            test.setDateNaissance(new Date());
            test.setTelephone("1111111111");
            
            System.out.println("   Création...");
            if (dao.save(test)) {
                System.out.println("     Création réussie, ID: " + test.getIdPersonne());
                
                
                System.out.println("   Lecture...");
                Personne lue = dao.findById(test.getIdPersonne());
                if (lue != null) {
                    System.out.println("     Lecture réussie: " + lue.getNom());
                    
                    
                    System.out.println("  ✏ Mise à jour...");
                    lue.setTelephone("2222222222");
                    if (dao.update(lue)) {
                        System.out.println("     Mise à jour réussie");
                        
                        
                        System.out.println("   Suppression...");
                        if (dao.deleteById(lue.getIdPersonne())) {
                            System.out.println("     Suppression réussie");
                        } else {
                            System.err.println("     Suppression échouée");
                        }
                    } else {
                        System.err.println("     Mise à jour échouée");
                    }
                } else {
                    System.err.println("     Lecture échouée");
                }
            } else {
                System.err.println("     Création échouée");
            }
        } catch (Exception e) {
            System.err.println(" Erreur test CRUD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}