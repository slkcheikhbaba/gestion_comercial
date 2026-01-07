package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class GenericDAO<T> {
    private Class<T> entityClass;

    public GenericDAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    
    public boolean create(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
            success = true;
            System.out.println("" + entityClass.getSimpleName() + " créé avec succès");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur création " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }

    
    public List<T> findAll() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<T> entities = new ArrayList<>();
        
        try {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            entities = query.list();
            System.out.println(" " + entities.size() + " " + entityClass.getSimpleName() + "(s) trouvé(s)");
        } catch (Exception e) {
            System.err.println(" Erreur findAll " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return entities;
    }

   
    public T findById(Object id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        T entity = null;
        try {
            entity = session.get(entityClass, id);
            if (entity != null) {
                System.out.println(" " + entityClass.getSimpleName() + " trouvé ID: " + id);
            }
        } catch (Exception e) {
            System.err.println(" Erreur findById " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return entity;
    }

    
    public boolean update(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
            success = true;
            System.out.println(" " + entityClass.getSimpleName() + " mis à jour");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur update " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }

    
    public boolean delete(T entity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        boolean success = false;
        
        try {
            transaction = session.beginTransaction();
            session.delete(entity);
            transaction.commit();
            success = true;
            System.out.println(" " + entityClass.getSimpleName() + " supprimé");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println(" Erreur delete " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return success;
    }

    
    public boolean deleteById(Object id) {
        T entity = findById(id);
        if (entity != null) {
            return delete(entity);
        }
        System.err.println(" " + entityClass.getSimpleName() + " ID " + id + " non trouvé pour suppression");
        return false;
    }

   
    public List<T> search(String searchText) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<T> entities = new ArrayList<>();
        
        try {
            String hql = buildSearchQuery(entityClass.getSimpleName());
            Query<T> query = session.createQuery(hql, entityClass);
            query.setParameter("search", "%" + searchText.toLowerCase() + "%");
            entities = query.list();
            System.out.println("🔎 " + entities.size() + " résultat(s) pour '" + searchText + "'");
        } catch (Exception e) {
            System.err.println("❌ Erreur search " + entityClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            session.close();
        }
        return entities;
    }

    private String buildSearchQuery(String name) {
        switch (name) {
            case "Personne":
                return "FROM Personne e WHERE LOWER(e.nom) LIKE LOWER(:search) " +
                       "OR LOWER(e.prenom) LIKE LOWER(:search) " +
                       "OR e.telephone LIKE :search";
            case "Ville":
                return "FROM Ville e WHERE LOWER(e.nomVille) LIKE LOWER(:search) " +
                       "OR e.codePostal LIKE :search";
            case "Agence":
                return "FROM Agence e WHERE LOWER(e.nomAgence) LIKE LOWER(:search) " +
                       "OR LOWER(e.adresse) LIKE LOWER(:search) " +
                       "OR e.telephone LIKE :search " +
                       "OR LOWER(e.email) LIKE LOWER(:search)";
            case "Exploitation":
                return "FROM Exploitation e WHERE LOWER(e.nomExploitation) LIKE LOWER(:search) " +
                       "OR LOWER(e.typeExploitation) LIKE LOWER(:search) " +
                       "OR LOWER(e.adresse) LIKE LOWER(:search)";
            case "Dirige":
                return "FROM Dirige e WHERE LOWER(e.fonction) LIKE LOWER(:search) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.nom) LIKE LOWER(:search)) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.prenom) LIKE LOWER(:search))";
            case "EstComptablePour":
                return "FROM EstComptablePour e WHERE LOWER(e.typeEntite) LIKE LOWER(:search) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.nom) LIKE LOWER(:search)) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.prenom) LIKE LOWER(:search))";
            case "EstComptableDans":
                return "FROM EstComptableDans e WHERE LOWER(e.poste) LIKE LOWER(:search) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.nom) LIKE LOWER(:search)) " +
                       "OR (e.personne IS NOT NULL AND LOWER(e.personne.prenom) LIKE LOWER(:search)) " +
                       "OR (e.agence IS NOT NULL AND LOWER(e.agence.nomAgence) LIKE LOWER(:search))";
            default:
                return "FROM " + name;
        }
    }

    
    public long count() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e", Long.class);
            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            System.err.println(" Erreur count " + entityClass.getSimpleName() + ": " + e.getMessage());
            return 0;
        } finally {
            session.close();
        }
    }

    
    public DefaultTableModel toTableModel(List<?> list) {
        DefaultTableModel model = new DefaultTableModel(getColumnNames(), 0);

        for (Object obj : list) {
            Object[] row = convertEntityToRow(obj);
            if (row != null) model.addRow(row);
        }
        return model;
    }

    private String[] getColumnNames() {
        switch (entityClass.getSimpleName()) {
            case "Personne":
                return new String[]{"ID", "Nom", "Prénom", "Date Naissance", "Téléphone"};
            case "Ville":
                return new String[]{"ID", "Nom Ville", "Code Postal"};
            case "Agence":
                return new String[]{"ID", "Nom Agence", "Adresse", "Téléphone", "Email", "ID Ville"};
            case "Exploitation":
                return new String[]{"ID", "Nom", "Type", "Superficie", "Adresse", "ID Ville", "ID Agence"};
            case "Dirige":
                return new String[]{"ID", "Personne", "Fonction", "Agence", "Exploitation", "Date Début"};
            case "EstComptablePour":
                return new String[]{"ID", "Personne", "ID Entité", "Type Entité", "Date Début"};
            case "EstComptableDans":
                return new String[]{"ID", "Personne", "Agence", "Poste", "Date Début"};
            default:
                return new String[]{"Données"};
        }
    }

    private Object[] convertEntityToRow(Object e) {
        try {
            switch (entityClass.getSimpleName()) {
                case "Personne":
                    model.Personne p = (model.Personne) e;
                    return new Object[]{
                        p.getIdPersonne(), 
                        p.getNom(), 
                        p.getPrenom(), 
                        p.getDateNaissance(), 
                        p.getTelephone()
                    };

                case "Ville":
                    model.Ville v = (model.Ville) e;
                    return new Object[]{
                        v.getIdVille(), 
                        v.getNomVille(), 
                        v.getCodePostal()
                    };

                case "Agence":
                    model.Agence a = (model.Agence) e;
                    return new Object[]{
                        a.getIdAgence(), 
                        a.getNomAgence(), 
                        a.getAdresse(), 
                        a.getTelephone(), 
                        a.getEmail(), 
                        a.getIdVille()
                    };

                case "Exploitation":
                    model.Exploitation x = (model.Exploitation) e;
                    return new Object[]{
                        x.getIdExploitation(), 
                        x.getNomExploitation(), 
                        x.getTypeExploitation(),
                        x.getSuperficie(), 
                        x.getAdresse(), 
                        x.getIdVille(), 
                        x.getIdAgence()
                    };

                case "Dirige":
                    model.Dirige d = (model.Dirige) e;
                    return new Object[]{
                        d.getIdDirige(),
                        d.getPersonne() != null ? d.getPersonne().getNomComplet() : "N/A",
                        d.getFonction(),
                        d.getAgence() != null ? d.getAgence().getNomAgence() : "N/A",
                        d.getExploitation() != null ? d.getExploitation().getNomExploitation() : "N/A",
                        d.getDateDebut()
                    };

                case "EstComptablePour":
                    model.EstComptablePour cp = (model.EstComptablePour) e;
                    return new Object[]{
                        cp.getIdComptablePour(),
                        cp.getPersonne() != null ? cp.getPersonne().getNomComplet() : "N/A",
                        cp.getIdEntite(), 
                        cp.getTypeEntite(), 
                        cp.getDateDebut()
                    };

                case "EstComptableDans":
                    model.EstComptableDans cd = (model.EstComptableDans) e;
                    return new Object[]{
                        cd.getIdComptableDans(),
                        cd.getPersonne() != null ? cd.getPersonne().getNomComplet() : "N/A",
                        cd.getAgence() != null ? cd.getAgence().getNomAgence() : "N/A",
                        cd.getPoste(), 
                        cd.getDateDebut()
                    };
            }
        } catch (Exception ex) {
            System.err.println(" Erreur conversion " + entityClass.getSimpleName() + " en ligne: " + ex.getMessage());
        }
        return null;
    }

    // TEST CONNEXION
    public boolean testConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery("SELECT 1 FROM " + entityClass.getSimpleName(), Long.class);
            query.setMaxResults(1);
            return true;
        } catch (Exception e) {
            System.err.println(" Erreur connexion " + entityClass.getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

   
    public void debugAll() {
        try {
            List<T> entities = findAll();
            System.out.println("\n DEBUG " + entityClass.getSimpleName().toUpperCase() + " ");
            if (entities.isEmpty()) {
                System.out.println(" Aucun " + entityClass.getSimpleName() + " trouvé");
            } else {
                for (T entity : entities) {
                    System.out.println(entity.toString());
                }
            }
            System.out.println("Total: " + entities.size() + " " + entityClass.getSimpleName() + "(s)");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur debugAll " + entityClass.getSimpleName() + ": " + e.getMessage());
        }
    }
}