package dao;

import model.EstComptablePour;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class EstComptablePourDAO {
    
    // CREATE - Ajouter un lien comptable pour
    public boolean save(EstComptablePour comptablePour) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(comptablePour);
            transaction.commit();
            success = true;
            System.out.println("✅ Lien comptable pour ajouté: " + comptablePour.getTypeEntite());
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
    
    // READ ALL - Récupérer tous les liens comptables
    public List<EstComptablePour> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptablePour> liens = new ArrayList<>();
        
        try {
            Query<EstComptablePour> query = session.createQuery(
                "FROM EstComptablePour ORDER BY dateDebut DESC", 
                EstComptablePour.class
            );
            liens = query.list();
            System.out.println("📊 " + liens.size() + " lien(s) comptable(s) pour trouvé(s)");
        } catch (Exception e) {
            System.err.println("❌ Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }
    
    // READ BY ID - Récupérer un lien par ID
    public EstComptablePour findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        EstComptablePour lien = null;
        try {
            lien = session.get(EstComptablePour.class, id);
            if (lien != null) {
                System.out.println("🔍 Lien comptable pour trouvé ID " + id + ": " + lien.getTypeEntite());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lien;
    }
    
    // UPDATE - Mettre à jour un lien
    public boolean update(EstComptablePour comptablePour) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(comptablePour);
            transaction.commit();
            success = true;
            System.out.println("✅ Lien comptable pour mis à jour ID " + comptablePour.getIdComptablePour());
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
    
    // DELETE - Supprimer un lien
    public boolean delete(EstComptablePour comptablePour) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(comptablePour);
            transaction.commit();
            success = true;
            System.out.println("✅ Lien comptable pour supprimé ID " + comptablePour.getIdComptablePour());
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
    
    // DELETE BY ID - Supprimer un lien par ID
    public boolean deleteById(Long id) {
        EstComptablePour comptablePour = findById(id);
        if (comptablePour != null) {
            return delete(comptablePour);
        }
        System.err.println("⚠️ Lien comptable pour ID " + id + " non trouvé pour suppression");
        return false;
    }
    
    // SEARCH - Rechercher des liens
    public List<EstComptablePour> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptablePour> liens = new ArrayList<>();
        
        try {
            Query<EstComptablePour> query = session.createQuery(
                "FROM EstComptablePour e WHERE LOWER(e.typeEntite) LIKE LOWER(:search) " +
                "   OR (e.personne IS NOT NULL AND LOWER(e.personne.nom) LIKE LOWER(:search)) " +
                "   OR (e.personne IS NOT NULL AND LOWER(e.personne.prenom) LIKE LOWER(:search)) " +
                "ORDER BY e.dateDebut DESC", 
                EstComptablePour.class
            );
            query.setParameter("search", "%" + searchText + "%");
            liens = query.list();
            System.out.println("🔎 " + liens.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println("❌ Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }
    
    // FIND BY PERSONNE - Rechercher les liens d'une personne
    public List<EstComptablePour> findByPersonneId(Long idPersonne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptablePour> liens = new ArrayList<>();
        
        try {
            Query<EstComptablePour> query = session.createQuery(
                "FROM EstComptablePour e WHERE e.personne.idPersonne = :idPersonne ORDER BY e.dateDebut DESC", 
                EstComptablePour.class
            );
            query.setParameter("idPersonne", idPersonne);
            liens = query.list();
        } catch (Exception e) {
            System.err.println("❌ Erreur findByPersonneId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }
    
    // FIND BY ENTITE - Rechercher les liens d'une entité
    public List<EstComptablePour> findByEntiteId(Long idEntite) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptablePour> liens = new ArrayList<>();
        
        try {
            Query<EstComptablePour> query = session.createQuery(
                "FROM EstComptablePour e WHERE e.idEntite = :idEntite ORDER BY e.dateDebut DESC", 
                EstComptablePour.class
            );
            query.setParameter("idEntite", idEntite);
            liens = query.list();
        } catch (Exception e) {
            System.err.println("❌ Erreur findByEntiteId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }
    
    // FIND BY TYPE ENTITE - Rechercher par type d'entité
    public List<EstComptablePour> findByTypeEntite(String typeEntite) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptablePour> liens = new ArrayList<>();
        
        try {
            Query<EstComptablePour> query = session.createQuery(
                "FROM EstComptablePour e WHERE LOWER(e.typeEntite) LIKE LOWER(:type) ORDER BY e.dateDebut DESC", 
                EstComptablePour.class
            );
            query.setParameter("type", "%" + typeEntite + "%");
            liens = query.list();
        } catch (Exception e) {
            System.err.println("❌ Erreur findByTypeEntite(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }
    
    // COUNT - Compter le nombre de liens
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM EstComptablePour e", Long.class);
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }
    
    // AFFICHER TOUS LES LIENS (pour tests)
    public void afficherTousLiens() {
        try {
            List<EstComptablePour> liens = findAll();
            System.out.println("=== LISTE DES LIENS COMPTABLES POUR ===");
            for (EstComptablePour lien : liens) {
                System.out.println("ID: " + lien.getIdComptablePour() + 
                                 " | Personne: " + (lien.getPersonne() != null ? lien.getPersonne().getNomComplet() : "N/A") + 
                                 " | ID Entité: " + lien.getIdEntite() + 
                                 " | Type Entité: " + lien.getTypeEntite() + 
                                 " | Date Début: " + lien.getDateDebut());
            }
            System.out.println("Total: " + liens.size() + " lien(s)");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    // MÉTHODE MAIN DE TEST
    public static void main(String[] args) {
        System.out.println("🧪 TEST ESTCOMPTABLEPOURDAO");
        System.out.println("===========================");
        
        EstComptablePourDAO dao = new EstComptablePourDAO();
        
        // Test connexion et comptage
        System.out.println("\n1. 🔗 TEST CONNEXION ET COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de liens: " + total);
        
        // Afficher tous les liens
        System.out.println("\n2. 📋 TEST FIND ALL:");
        dao.afficherTousLiens();
        
        // Test recherche
        System.out.println("\n3. 🔍 TEST RECHERCHE:");
        List<EstComptablePour> resultats = dao.search("agence");
        System.out.println("Résultats recherche 'agence': " + resultats.size());
        
        System.out.println("\n✅ TESTS TERMINÉS");
    }
}