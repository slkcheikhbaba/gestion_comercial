package dao;

import model.EstComptableDans;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

public class EstComptableDansDAO {

   
    public boolean save(EstComptableDans comptableDans) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(comptableDans);
            transaction.commit();
            success = true;
            System.out.println(" Lien comptable dans ajouté: " + comptableDans.getPoste());
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

   
    public List<EstComptableDans> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptableDans> liens = new ArrayList<>();
        
        try {
            Query<EstComptableDans> query = session.createQuery(
                    "FROM EstComptableDans e ORDER BY e.dateDebut DESC",
                    EstComptableDans.class
            );
            liens = query.list();
            System.out.println(" " + liens.size() + " lien(s) comptable(s) dans trouvé(s)");
        } catch (Exception e) {
            System.err.println(" Erreur findAll(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }

    
    public EstComptableDans findById(Long id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        EstComptableDans lien = null;
        try {
            lien = session.get(EstComptableDans.class, id);
            if (lien != null) {
                System.out.println(" Lien comptable dans trouvé ID " + id + ": " + lien.getPoste());
            }
        } catch (Exception e) {
            System.err.println(" Erreur findById(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return lien;
    }

   
    public boolean update(EstComptableDans comptableDans) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(comptableDans);
            transaction.commit();
            success = true;
            System.out.println(" Lien comptable dans mis à jour ID " + comptableDans.getIdComptableDans());
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

    
    public boolean delete(EstComptableDans comptableDans) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(comptableDans);
            transaction.commit();
            success = true;
            System.out.println(" Lien comptable dans supprimé ID " + comptableDans.getIdComptableDans());
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
        EstComptableDans comptableDans = findById(id);
        if (comptableDans != null) {
            return delete(comptableDans);
        }
        System.err.println(" Lien comptable dans ID " + id + " non trouvé pour suppression");
        return false;
    }

   
    public List<EstComptableDans> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptableDans> liens = new ArrayList<>();
        
        try {
            Query<EstComptableDans> query = session.createQuery(
                    "FROM EstComptableDans e " +
                    "WHERE LOWER(e.poste) LIKE LOWER(:search) " +
                    "   OR (e.personne IS NOT NULL AND LOWER(e.personne.nom) LIKE LOWER(:search)) " +
                    "   OR (e.personne IS NOT NULL AND LOWER(e.personne.prenom) LIKE LOWER(:search)) " +
                    "   OR (e.agence IS NOT NULL AND LOWER(e.agence.nomAgence) LIKE LOWER(:search)) " +
                    "ORDER BY e.dateDebut DESC",
                    EstComptableDans.class
            );
            query.setParameter("search", "%" + searchText + "%");
            liens = query.list();
            System.out.println(" " + liens.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println(" Erreur search(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }

   
    public List<EstComptableDans> findByPersonneId(Long idPersonne) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptableDans> liens = new ArrayList<>();
        
        try {
            Query<EstComptableDans> query = session.createQuery(
                    "FROM EstComptableDans e " +
                    "WHERE e.personne.idPersonne = :idPersonne " +
                    "ORDER BY e.dateDebut DESC",
                    EstComptableDans.class
            );
            query.setParameter("idPersonne", idPersonne);
            liens = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByPersonneId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }

    
    public List<EstComptableDans> findByAgenceId(Long idAgence) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptableDans> liens = new ArrayList<>();
        
        try {
            Query<EstComptableDans> query = session.createQuery(
                    "FROM EstComptableDans e " +
                    "WHERE e.agence.idAgence = :idAgence " +
                    "ORDER BY e.dateDebut DESC",
                    EstComptableDans.class
            );
            query.setParameter("idAgence", idAgence);
            liens = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByAgenceId(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }

   
    public List<EstComptableDans> findByPoste(String poste) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<EstComptableDans> liens = new ArrayList<>();
        
        try {
            Query<EstComptableDans> query = session.createQuery(
                    "FROM EstComptableDans e " +
                    "WHERE LOWER(e.poste) LIKE LOWER(:poste) " +
                    "ORDER BY e.dateDebut DESC",
                    EstComptableDans.class
            );
            query.setParameter("poste", "%" + poste + "%");
            liens = query.list();
        } catch (Exception e) {
            System.err.println(" Erreur findByPoste(): " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return liens;
    }

   
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(e) FROM EstComptableDans e",
                    Long.class
            );
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println(" Erreur count(): " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

   
    public void afficherTousLiens() {
        try {
            List<EstComptableDans> liens = findAll();
            System.out.println("=== LISTE DES LIENS COMPTABLES DANS ===");
            for (EstComptableDans lien : liens) {
                System.out.println(
                        "ID: " + lien.getIdComptableDans() +
                        "  Personne: " + (lien.getPersonne() != null ? lien.getPersonne().getNomComplet() : "N/A") +
                        "  Agence: " + (lien.getAgence() != null ? lien.getAgence().getNomAgence() : "N/A") +
                        "  Date Début: " + lien.getDateDebut() +
                        "  Poste: " + lien.getPoste()
                );
            }
            System.out.println("Total: " + liens.size() + " lien(s)");
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }
    }
    
    
    public static void main(String[] args) {
        System.out.println(" TEST ESTCOMPTABLEDANSDAO");
       
        
        EstComptableDansDAO dao = new EstComptableDansDAO();
        
       
        System.out.println("\n1.  TEST CONNEXION ET COMPTAGE:");
        long total = dao.count();
        System.out.println("Nombre total de liens: " + total);
        
       
        System.out.println("\n2.  TEST FIND ALL:");
        dao.afficherTousLiens();
        
        
        System.out.println("\n3.  TEST RECHERCHE:");
        List<EstComptableDans> resultats = dao.search("comptable");
        System.out.println("Résultats recherche 'comptable': " + resultats.size());
        
        System.out.println("\n TESTS TERMINÉS");
    }
}