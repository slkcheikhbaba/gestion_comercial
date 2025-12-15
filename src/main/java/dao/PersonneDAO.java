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
    
    // CREATE - Ajouter une personne
    public boolean save(Personne personne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(personne);
            transaction.commit();
            success = true;
            System.out.println("✅ Personne ajoutée: " + personne.getNom() + " " + personne.getPrenom());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur save(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    // READ ALL - Récupérer toutes les personnes
    public List<Personne> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Personne> personnes = new ArrayList<>();
        
        try {
            Query<Personne> query = session.createQuery(
                "FROM Personne p ORDER BY p.nom, p.prenom", 
                Personne.class
            );
            personnes = query.list();
            System.out.println("📊 " + personnes.size() + " personne(s) trouvée(s)");
        } catch (Exception e) {
            System.err.println("❌ Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return personnes;
    }
    
    // READ BY ID - Récupérer une personne par ID
    public Personne findById(Integer id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Personne personne = null;
        try {
            personne = session.get(Personne.class, id);
            if (personne != null) {
                System.out.println("🔍 Personne trouvée ID " + id + ": " + personne.getNom());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return personne;
    }
    
    // UPDATE - Mettre à jour une personne
    public boolean update(Personne personne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(personne);
            transaction.commit();
            success = true;
            System.out.println("✅ Personne mise à jour ID " + personne.getIdPersonne());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur update(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    // DELETE - Supprimer une personne
    public boolean delete(Personne personne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(personne);
            transaction.commit();
            success = true;
            System.out.println("✅ Personne supprimée ID " + personne.getIdPersonne());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur delete(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    // DELETE BY ID - Supprimer une personne par ID
    public boolean deleteById(Integer id) {
        Personne personne = findById(id);
        if (personne != null) {
            return delete(personne);
        }
        System.err.println("⚠️ Personne ID " + id + " non trouvée pour suppression");
        return false;
    }
    
    // SEARCH - Rechercher des personnes
    public List<Personne> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Personne> personnes = new ArrayList<>();
        
        try {
            Query<Personne> query = session.createQuery(
                "FROM Personne p WHERE LOWER(p.nom) LIKE LOWER(:search) " +
                "OR LOWER(p.prenom) LIKE LOWER(:search) " +
                "OR p.telephone LIKE :search " +
                "ORDER BY p.nom, p.prenom", 
                Personne.class
            );
            query.setParameter("search", "%" + searchText + "%");
            personnes = query.list();
            System.out.println("🔎 " + personnes.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println("❌ Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return personnes;
    }
    
    // COUNT - Compter le nombre de personnes
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Personne p", Long.class);
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }
    
    // TEST CONNEXION
    public boolean testConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT 1 FROM Personne", Long.class);
            query.setMaxResults(1);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur de connexion: " + e.getMessage());
            return false;
        }
    }
    
    // MÉTHODE DE DÉBUG
    public void debugDatabase() {
        System.out.println("\n🔍 DEBUG BASE DE DONNÉES");
        System.out.println("=========================");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Test 1: Connexion et comptage
            Query<Long> countQuery = session.createQuery("SELECT COUNT(p) FROM Personne p", Long.class);
            Long count = countQuery.uniqueResult();
            System.out.println("🔗 Connexion OK");
            System.out.println("📊 Nombre total de personnes: " + count);
            
            // Test 2: Lister toutes les personnes
            if (count > 0) {
                Query<Personne> allQuery = session.createQuery("FROM Personne p", Personne.class);
                List<Personne> personnes = allQuery.list();
                System.out.println("\n📋 LISTE DES PERSONNES:");
                System.out.println("ID | Nom | Prénom | Téléphone | Date Naissance");
                System.out.println("------------------------------------------------");
                for (Personne p : personnes) {
                    System.out.println(p.getIdPersonne() + " | " + 
                                     p.getNom() + " | " + 
                                     p.getPrenom() + " | " + 
                                     p.getTelephone() + " | " + 
                                     p.getDateNaissance());
                }
            } else {
                System.out.println("\nℹ️ La table 'personnes' est vide");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur debugDatabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // MÉTHODE MAIN DE TEST
    public static void main(String[] args) {
        System.out.println("🧪 TEST PERSONNEDAO");
        System.out.println("===================");
        
        PersonneDAO dao = new PersonneDAO();
        
        // Debug de la base de données
        dao.debugDatabase();
        
        // Test connexion
        System.out.println("\n1. 🔗 TEST CONNEXION:");
        boolean connected = dao.testConnection();
        System.out.println("Connexion: " + (connected ? "✅ OK" : "❌ ÉCHEC"));
        
        // Test comptage
        System.out.println("\n2. 📊 TEST COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de personnes: " + total);
        
        if (total == 0) {
            // Test ajout si la base est vide
            System.out.println("\n3. ➕ TEST AJOUT (base vide):");
            Personne nouvelle = new Personne();
            nouvelle.setNom("Dupont");
            nouvelle.setPrenom("Jean");
            nouvelle.setDateNaissance(new Date());
            nouvelle.setTelephone("0612345678");
            
            if (dao.save(nouvelle)) {
                System.out.println("✅ Ajout réussi - Nouvel ID: " + nouvelle.getIdPersonne());
                dao.debugDatabase(); // Vérifier l'ajout
            }
        }
        
        System.out.println("\n✅ TESTS TERMINÉS");
    }
}