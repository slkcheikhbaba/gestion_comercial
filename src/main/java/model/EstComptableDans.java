package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "est_comptable_dans")
public class EstComptableDans {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomptabledans")
    private Long idComptableDans;
    
    @ManyToOne
    @JoinColumn(name = "idpersonne", nullable = false)
    private Personne personne;
    
    @ManyToOne
    @JoinColumn(name = "idagence", nullable = false)
    private Agence agence;
    
    @Column(name = "datedebut")
    @Temporal(TemporalType.DATE)
    private Date dateDebut;
    
    @Column(name = "poste")
    private String poste;
    
    // Constructeur par défaut
    public EstComptableDans() {}

    // Constructeur complet (optionnel mais utile)
    public EstComptableDans(Personne personne, Agence agence, Date dateDebut, String poste) {
        this.personne = personne;
        this.agence = agence;
        this.dateDebut = dateDebut;
        this.poste = poste;
    }
    
    // Getters et Setters
    public Long getIdComptableDans() { return idComptableDans; }
    public void setIdComptableDans(Long idComptableDans) { this.idComptableDans = idComptableDans; }
    
    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }
    
    public Agence getAgence() { return agence; }
    public void setAgence(Agence agence) { this.agence = agence; }
    
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    
    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    @Override
    public String toString() {
        return poste + " - " + (personne != null ? personne.getNomComplet() : "Sans personne");
    }
}
