package dao;

import model.Dirige;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class DirigeDAO {

   
    public boolean save(Dirige dirige) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(dirige);
            transaction.commit();
            success = true;
            System.out.println(" Direction ajoutée: " + dirige.getFonction());
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

    
    public List<Dirige> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Dirige> directions = new ArrayList<>();
        
        try {
            Query<Dirige> query = session.createQuery(
                    "FROM Dirige d ORDER BY d.dateDebut DESC",
                    Dirige.class
            );
            directions = query.list();
            System.out.println(" " + directions.size() + " direction(s) trouvée(s)");
        } catch (Exception e) {
            System.err.println(" Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return directions;
    }

   
    public Dirige findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Dirige dirige = null;
        try {
            dirige = session.get(Dirige.class, id);
            if (dirige != null) {
                System.out.println(" Direction trouvée ID " + id + ": " + dirige.getFonction());
            }
        } catch (Exception e) {
            System.err.println(" Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return dirige;
    }

    
    public boolean update(Dirige dirige) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(dirige);
            transaction.commit();
            success = true;
            System.out.println(" Direction mise à jour ID " + dirige.getIdDirige());
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

  
    public boolean delete(Dirige dirige) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(dirige);
            transaction.commit();
            success = true;
            System.out.println(" Direction supprimée ID " + dirige.getIdDirige());
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
        Dirige dirige = findById(id);
        if (dirige != null) {
            return delete(dirige);
        }
        System.err.println(" Direction ID " + id + " non trouvée pour suppression");
        return false;
    }

    
    public List<Dirige> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Dirige> directions = new ArrayList<>();
        
        try {
            Query<Dirige> query = session.createQuery(
                    "FROM Dirige d " +
                    "WHERE LOWER(d.fonction) LIKE LOWER(:search) " +
                    "   OR (d.personne IS NOT NULL AND LOWER(d.personne.nom) LIKE LOWER(:search)) " +
                    "   OR (d.personne IS NOT NULL AND LOWER(d.personne.prenom) LIKE LOWER(:search)) " +
                    "ORDER BY d.dateDebut DESC",
                    Dirige.class
            );
            query.setParameter("search", "%" + searchText + "%");
            directions = query.list();
            System.out.println(" " + directions.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println(" Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return directions;
    }

    
    public List<Dirige> findByPersonneId(Long idPersonne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Dirige> directions = new ArrayList<>();
        
        try {
            Query<Dirige> query = session.createQuery(
                    "FROM Dirige d " +
                    "WHERE d.personne.idPersonne = :idPersonne " +
                    "ORDER BY d.dateDebut DESC",
                    Dirige.class
            );
            query.setParameter("idPersonne", idPersonne);
            directions = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByPersonneId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return directions;
    }

    
    public List<Dirige> findByAgenceId(Long idAgence) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Dirige> directions = new ArrayList<>();
        
        try {
            Query<Dirige> query = session.createQuery(
                    "FROM Dirige d " +
                    "WHERE d.agence.idAgence = :idAgence " +
                    "ORDER BY d.dateDebut DESC",
                    Dirige.class
            );
            query.setParameter("idAgence", idAgence);
            directions = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByAgenceId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return directions;
    }

   
    public List<Dirige> findByExploitationId(Long idExploitation) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Dirige> directions = new ArrayList<>();
        
        try {
            Query<Dirige> query = session.createQuery(
                    "FROM Dirige d " +
                    "WHERE d.exploitation.idExploitation = :idExploitation " +
                    "ORDER BY d.dateDebut DESC",
                    Dirige.class
            );
            query.setParameter("idExploitation", idExploitation);
            directions = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByExploitationId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return directions;
    }

    
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(d) FROM Dirige d",
                    Long.class
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

   
    public void afficherToutesDirections() {
        try {
            List<Dirige> directions = findAll();
            System.out.println("=== LISTE DES DIRECTIONS ===");
            for (Dirige dirige : directions) {
                System.out.println(
                        "ID: " + dirige.getIdDirige() +
                        "  Personne: " + (dirige.getPersonne() != null ? dirige.getPersonne().getNomComplet() : "N/A") +
                        "  Agence: " + (dirige.getAgence() != null ? dirige.getAgence().getNomAgence() : "N/A") +
                        "  Exploitation: " + (dirige.getExploitation() != null ? dirige.getExploitation().getNomExploitation() : "N/A") +
                        "  Date Début: " + dirige.getDateDebut() +
                        "  Fonction: " + dirige.getFonction()
                );
            }
            System.out.println("Total: " + directions.size() + " direction(s)");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
   
    public static void main(String[] args) {
        System.out.println(" TEST DIRIGEDAO");
        
        
        DirigeDAO dao = new DirigeDAO();
        
        
        System.out.println("\n1.  TEST CONNEXION ET COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de directions: " + total);
        
       
        System.out.println("\n2.  TEST FIND ALL:");
        dao.afficherToutesDirections();
        
        
        System.out.println("\n3.  TEST RECHERCHE:");
        List<Dirige> resultats = dao.search("directeur");
        System.out.println("Résultats recherche 'directeur': " + resultats.size());
        
        System.out.println("\n TESTS TERMINÉS");
    }
}