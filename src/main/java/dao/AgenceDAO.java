package dao;

import model.Agence;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class AgenceDAO {

    
    public boolean save(Agence agence) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(agence);
            transaction.commit();
            success = true;
            System.out.println("✅ Agence ajoutée: " + agence.getNomAgence());
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

    
    public List<Agence> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Agence> agences = new ArrayList<>();
        
        try {
            Query<Agence> query = session.createQuery(
                "FROM Agence ORDER BY nomAgence", Agence.class
            );
            agences = query.list();
            System.out.println("📊 " + agences.size() + " agence(s) trouvée(s)");
        } catch (Exception e) {
            System.err.println("❌ Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agences;
    }

    
    public Agence findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Agence agence = null;
        try {
            agence = session.get(Agence.class, id);
            if (agence != null) {
                System.out.println("🔍 Agence trouvée ID " + id + ": " + agence.getNomAgence());
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agence;
    }

    
    public boolean update(Agence agence) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(agence);
            transaction.commit();
            success = true;
            System.out.println("✅ Agence mise à jour ID " + agence.getIdAgence());
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

    
    public boolean delete(Agence agence) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(agence);
            transaction.commit();
            success = true;
            System.out.println("✅ Agence supprimée ID " + agence.getIdAgence());
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

    
    public boolean deleteById(Long id) {
        Agence agence = findById(id);
        if (agence != null) {
            return delete(agence);
        }
        System.err.println("⚠️ Agence ID " + id + " non trouvée pour suppression");
        return false;
    }

    
    public List<Agence> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Agence> agences = new ArrayList<>();
        
        try {
            Query<Agence> query = session.createQuery(
                "FROM Agence a WHERE LOWER(a.nomAgence) LIKE LOWER(:search) " +
                "OR LOWER(a.adresse) LIKE LOWER(:search) " +
                "OR a.telephone LIKE :search " +
                "OR LOWER(a.email) LIKE LOWER(:search) " +
                "ORDER BY a.nomAgence",
                Agence.class
            );
            query.setParameter("search", "%" + searchText + "%");
            agences = query.list();
            System.out.println("🔎 " + agences.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println("❌ Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agences;
    }

    
    public List<Agence> findByVilleId(Long idVille) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Agence> agences = new ArrayList<>();
        
        try {
            Query<Agence> query = session.createQuery(
                "FROM Agence a WHERE a.idVille = :idVille ORDER BY a.nomAgence",
                Agence.class
            );
            query.setParameter("idVille", idVille);
            agences = query.list();
        } catch (Exception e) {
            System.err.println("❌ Erreur findByVilleId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return agences;
    }

    
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(a) FROM Agence a", Long.class
            );
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    // COUNT BY VILLE
    public long countByVilleId(Long idVille) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery(
                "SELECT COUNT(a) FROM Agence a WHERE a.idVille = :idVille",
                Long.class
            );
            query.setParameter("idVille", idVille);
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println("❌ Erreur countByVilleId(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    // AFFICHER TOUTES LES AGENCES (pour tests)
    public void afficherToutesAgences() {
        try {
            List<Agence> agences = findAll();
            System.out.println("=== LISTE DES AGENCES ===");
            for (Agence agence : agences) {
                System.out.println("ID: " + agence.getIdAgence() +
                        " | Nom: " + agence.getNomAgence() +
                        " | ID Ville: " + agence.getIdVille() +
                        " | Tél: " + agence.getTelephone());
            }
            System.out.println("Total: " + agences.size() + " agence(s)");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    // MÉTHODE MAIN DE TEST
    public static void main(String[] args) {
        System.out.println(" TEST AGENCEDAO");
        
        
        AgenceDAO dao = new AgenceDAO();
        
        
        System.out.println("\n1.  TEST CONNEXION ET COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total d'agences: " + total);
        
        
        System.out.println("\n2.  TEST FIND ALL:");
        dao.afficherToutesAgences();
        
        
        System.out.println("\n3.  TEST RECHERCHE:");
        List<Agence> resultats = dao.search("central");
        System.out.println("Résultats recherche 'central': " + resultats.size());
        
        System.out.println("\n TESTS TERMINÉS");
    }
}