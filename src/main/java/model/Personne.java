package model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "personnes", schema = "public")
public class Personne implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpersonne")
    private Integer idPersonne;
    
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;
    
    @Column(name = "datenaissance", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateNaissance;
    
    @Column(name = "telephone", length = 20)
    private String telephone;
    
    
    public Personne() {
        
    }
    
    public Personne(String nom, String prenom, Date dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }
    
    public Personne(String nom, String prenom, Date dateNaissance, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.telephone = telephone;
    }
    
    
    public Integer getIdPersonne() {
        return idPersonne;
    }
    
    public void setIdPersonne(Integer idPersonne) {
        this.idPersonne = idPersonne;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public Date getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    
    
    public String getNomComplet() {
        return prenom + " " + nom;
    }
    
    @Override
    public String toString() {
        return "Personne [id=" + idPersonne + 
               ", nom=" + nom + 
               ", prénom=" + prenom + 
               ", téléphone=" + telephone + "]";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Personne personne = (Personne) obj;
        return idPersonne != null && idPersonne.equals(personne.idPersonne);
    }
    
    @Override
    public int hashCode() {
        return idPersonne != null ? idPersonne.hashCode() : 0;
    }
}