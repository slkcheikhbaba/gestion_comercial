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
    
    // ====================== CREATE - Ajouter une personne ======================
    public boolean save(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println("🔵 Tentative d'ajout de : " + personne.getNom() + " " + personne.getPrenom());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Utiliser persist() qui est standard JPA
            session.persist(personne);
            
            transaction.commit();
            success = true;
            
            // Force le flush et vérifie l'ID généré
            session.flush();
            System.out.println("✅ Personne ajoutée avec ID: " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur save(): " + e.getMessage());
            e.printStackTrace();
            // Log plus détaillé
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
    
    // ====================== READ ALL - Récupérer toutes les personnes ======================
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
            
            System.out.println("📊 " + personnes.size() + " personne(s) trouvée(s)");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        
        return personnes;
    }
    
    // ====================== READ BY ID - Récupérer une personne par ID ======================
    public Personne findById(Integer id) {
        Session session = null;
        Personne personne = null;
        
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            personne = session.get(Personne.class, id);
            
            if (personne != null) {
                System.out.println("🔍 Personne trouvée ID " + id + ": " + personne.getNom());
            } else {
                System.out.println("⚠️ Personne ID " + id + " non trouvée");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return personne;
    }
    
    // ====================== UPDATE - Mettre à jour une personne ======================
    public boolean update(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println("🔵 Tentative de mise à jour ID: " + personne.getIdPersonne());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Utiliser merge() au lieu de update() pour gérer les détachements
            session.merge(personne);
            
            transaction.commit();
            success = true;
            
            System.out.println("✅ Personne mise à jour ID " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur update(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }
    
    // ====================== DELETE - Supprimer une personne ======================
    public boolean delete(Personne personne) {
        Session session = null;
        Transaction transaction = null;
        boolean success = false;
        
        try {
            System.out.println("🔵 Tentative de suppression ID: " + personne.getIdPersonne());
            
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            
            // Recharger l'entité pour s'assurer qu'elle est attachée
            Personne attachedPersonne = session.get(Personne.class, personne.getIdPersonne());
            if (attachedPersonne != null) {
                session.remove(attachedPersonne);
            }
            
            transaction.commit();
            success = true;
            
            System.out.println("✅ Personne supprimée ID " + personne.getIdPersonne());
            
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur delete(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return success;
    }
    
    // ====================== DELETE BY ID - Supprimer une personne par ID ======================
    public boolean deleteById(Integer id) {
        Personne personne = findById(id);
        if (personne != null) {
            return delete(personne);
        }
        System.err.println("⚠️ Personne ID " + id + " non trouvée pour suppression");
        return false;
    }
    
    // ====================== SEARCH - Rechercher des personnes ======================
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
            
            System.out.println("🔎 " + personnes.size() + " résultat(s) pour '" + searchText + "'");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
        return personnes;
    }
    
    // ====================== COUNT - Compter le nombre de personnes ======================
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
            System.err.println("❌ Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }
    
    // ====================== EXISTS - Vérifier si une personne existe ======================
    public boolean exists(Integer id) {
        return findById(id) != null;
    }
    
    // ====================== TEST CONNEXION ======================
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
    
    // ====================== MÉTHODE DE DÉBUG ======================
    public void debugDatabase() {
        System.out.println("\n🔍 DEBUG BASE DE DONNÉES HIBERNATE");
        System.out.println("==================================");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Test 1: Connexion et comptage
            Query<Long> countQuery = session.createQuery("SELECT COUNT(p) FROM Personne p", Long.class);
            Long count = countQuery.uniqueResult();
            System.out.println("🔗 Connexion Hibernate OK");
            System.out.println("📊 Nombre total de personnes: " + count);
            
            // Test 2: Lister toutes les personnes
            if (count > 0) {
                Query<Personne> allQuery = session.createQuery("FROM Personne p", Personne.class);
                List<Personne> personnes = allQuery.list();
                
                System.out.println("\n📋 LISTE DES PERSONNES DANS LA BASE:");
                System.out.println("=====================================");
                System.out.println("ID | Nom | Prénom | Téléphone | Date Naissance");
                System.out.println("------------------------------------------------");
                
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
                System.out.println("\nℹ️ La table 'personnes' est vide dans la base de données");
            }
            
            // Test 3: Informations sur la session
            System.out.println("\nℹ️ Informations Session Hibernate:");
            System.out.println("Session ouverte: " + session.isOpen());
            System.out.println("Session connectée: " + session.isConnected());
            
        } catch (Exception e) {
            System.err.println("❌ Erreur debugDatabase: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ====================== MÉTHODE POUR TESTER L'AJOUT ======================
    public void testAjoutDirect() {
        System.out.println("\n🧪 TEST AJOUT DIRECT HIBERNATE");
        System.out.println("==============================");
        
        Personne testPersonne = new Personne();
        testPersonne.setNom("TEST_HIBERNATE");
        testPersonne.setPrenom("Test");
        testPersonne.setDateNaissance(new Date());
        testPersonne.setTelephone("0000000000");
        
        if (save(testPersonne)) {
            System.out.println("✅ Test d'ajout Hibernate réussi!");
            System.out.println("📝 ID généré: " + testPersonne.getIdPersonne());
            
            // Vérification immédiate
            Personne verif = findById(testPersonne.getIdPersonne());
            if (verif != null) {
                System.out.println("✅ Vérification: Personne trouvée dans la base");
            } else {
                System.err.println("❌ Vérification: Personne NON trouvée après ajout!");
            }
        } else {
            System.err.println("❌ Test d'ajout Hibernate échoué!");
        }
    }
    
    // ====================== MÉTHODE MAIN DE TEST ======================
    public static void main(String[] args) {
        System.out.println("🧪 TEST COMPLET PERSONNEDAO AVEC HIBERNATE");
        System.out.println("==========================================");
        
        PersonneDAO dao = new PersonneDAO();
        
        // Étape 1: Test connexion Hibernate
        System.out.println("\n1. 🔗 TEST CONNEXION HIBERNATE:");
        boolean connected = dao.testConnection();
        System.out.println("Connexion: " + (connected ? "✅ OK" : "❌ ÉCHEC"));
        
        if (!connected) {
            System.err.println("❌ Impossible de continuer, connexion Hibernate échouée");
            return;
        }
        
        // Étape 2: Debug de la base
        System.out.println("\n2. 🔍 ÉTAT ACTUEL DE LA BASE:");
        dao.debugDatabase();
        
        // Étape 3: Test comptage
        System.out.println("\n3. 📊 TEST COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de personnes: " + total);
        
        // Étape 4: Test ajout si nécessaire
        if (total == 0) {
            System.out.println("\n4. ➕ TEST AJOUT (base vide):");
            dao.testAjoutDirect();
            
            // Re-debug après ajout
            System.out.println("\n5. 🔍 ÉTAT APRÈS AJOUT:");
            dao.debugDatabase();
        }
        
        // Étape 5: Test recherche
        System.out.println("\n6. 🔎 TEST RECHERCHE:");
        List<Personne> resultats = dao.search("test");
        System.out.println("Résultats recherche 'test': " + resultats.size());
        
        // Étape 6: Test CRUD complet sur une personne de test
        System.out.println("\n7. 🧪 TEST CRUD COMPLET:");
        testCRUD(dao);
        
        System.out.println("\n✅ TESTS HIBERNATE TERMINÉS");
    }
    
    // ====================== MÉTHODE DE TEST CRUD ======================
    private static void testCRUD(PersonneDAO dao) {
        try {
            // Création
            Personne test = new Personne();
            test.setNom("CRUD");
            test.setPrenom("Test");
            test.setDateNaissance(new Date());
            test.setTelephone("1111111111");
            
            System.out.println("  ➕ Création...");
            if (dao.save(test)) {
                System.out.println("    ✅ Création réussie, ID: " + test.getIdPersonne());
                
                // Lecture
                System.out.println("  📖 Lecture...");
                Personne lue = dao.findById(test.getIdPersonne());
                if (lue != null) {
                    System.out.println("    ✅ Lecture réussie: " + lue.getNom());
                    
                    // Mise à jour
                    System.out.println("  ✏️ Mise à jour...");
                    lue.setTelephone("2222222222");
                    if (dao.update(lue)) {
                        System.out.println("    ✅ Mise à jour réussie");
                        
                        // Suppression
                        System.out.println("  🗑️ Suppression...");
                        if (dao.deleteById(lue.getIdPersonne())) {
                            System.out.println("    ✅ Suppression réussie");
                        } else {
                            System.err.println("    ❌ Suppression échouée");
                        }
                    } else {
                        System.err.println("    ❌ Mise à jour échouée");
                    }
                } else {
                    System.err.println("    ❌ Lecture échouée");
                }
            } else {
                System.err.println("    ❌ Création échouée");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur test CRUD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}