package dao;

import model.Ville;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class VilleDAO {
    
    
    public boolean save(Ville ville) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(ville);
            transaction.commit();
            success = true;
            System.out.println(" Ville ajoutée: " + ville.getNomVille() + " (" + ville.getCodePostal() + ")");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur save(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    
    public List<Ville> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ville> villes = new ArrayList<>();
        
        try {
            Query<Ville> query = session.createQuery("FROM Ville ORDER BY nomVille", Ville.class);
            villes = query.list();
            System.out.println(" " + villes.size() + " ville(s) trouvée(s)");
        } catch (Exception e) {
            System.err.println(" Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return villes;
    }
    
    
    public Ville findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Ville ville = null;
        try {
            ville = session.get(Ville.class, id);
            if (ville != null) {
                System.out.println(" Ville trouvée ID " + id + ": " + ville.getNomVille());
            }
        } catch (Exception e) {
            System.err.println(" Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ville;
    }
    
    
    public boolean update(Ville ville) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(ville);
            transaction.commit();
            success = true;
            System.out.println(" Ville mise à jour ID " + ville.getIdVille());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur update(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    
    public boolean delete(Ville ville) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(ville);
            transaction.commit();
            success = true;
            System.out.println(" Ville supprimée ID " + ville.getIdVille());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur delete(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }
    
    
    public boolean deleteById(Long id) {
        Ville ville = findById(id);
        if (ville != null) {
            return delete(ville);
        }
        System.err.println("Ville ID " + id + " non trouvée pour suppression");
        return false;
    }
    
   
    public List<Ville> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ville> villes = new ArrayList<>();
        
        try {
            Query<Ville> query = session.createQuery(
                "FROM Ville v WHERE LOWER(v.nomVille) LIKE LOWER(:search) " +
                "OR v.codePostal LIKE :search " +
                "ORDER BY v.nomVille", 
                Ville.class
            );
            query.setParameter("search", "%" + searchText + "%");
            villes = query.list();
            System.out.println(" " + villes.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println(" Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return villes;
    }
    
    
    public List<Ville> findByCodePostal(String codePostal) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Ville> villes = new ArrayList<>();
        
        try {
            Query<Ville> query = session.createQuery(
                "FROM Ville v WHERE v.codePostal = :code ORDER BY v.nomVille", 
                Ville.class
            );
            query.setParameter("code", codePostal);
            villes = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByCodePostal(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return villes;
    }
    
    
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(v) FROM Ville v", Long.class);
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println(" Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }
    
    
    public void afficherToutesVilles() {
        try {
            List<Ville> villes = findAll();
            System.out.println("=== LISTE DES VILLES ===");
            for (Ville ville : villes) {
                System.out.println("ID: " + ville.getIdVille() + 
                                 " | Ville: " + ville.getNomVille() + 
                                 " | Code Postal: " + ville.getCodePostal());
            }
            System.out.println("Total: " + villes.size() + " ville(s)");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
        public static void main(String[] args) {
        System.out.println(" TEST VILLEDAO");
        
        
        VilleDAO dao = new VilleDAO();
        
       
        System.out.println("\n1.  TEST CONNEXION ET COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de villes: " + total);
        
        
        System.out.println("\n2.  TEST FIND ALL:");
        dao.afficherToutesVilles();
        
        
        System.out.println("\n3.  TEST RECHERCHE:");
        List<Ville> resultats = dao.search("nouak");
        System.out.println("Résultats recherche 'nouak': " + resultats.size());
        
        System.out.println("\n TESTS TERMINÉS");
    }
}