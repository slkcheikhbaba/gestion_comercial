package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "dirige")
public class Dirige {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddirige")
    private Long idDirige;
    
    @ManyToOne
    @JoinColumn(name = "idpersonne", nullable = false)
    private Personne personne;
    
    @ManyToOne
    @JoinColumn(name = "idagence")
    private Agence agence;
    
    @ManyToOne
    @JoinColumn(name = "idexploitation")
    private Exploitation exploitation;
    
    @Column(name = "datedebut")
    @Temporal(TemporalType.DATE)
    private Date dateDebut;
    
    @Column(name = "fonction")
    private String fonction;
    
    // Constructeur par défaut
    public Dirige() {}

    // Constructeur complet (optionnel mais utile)
    public Dirige(Personne personne, Agence agence, Exploitation exploitation, Date dateDebut, String fonction) {
        this.personne = personne;
        this.agence = agence;
        this.exploitation = exploitation;
        this.dateDebut = dateDebut;
        this.fonction = fonction;
    }
    
    // Getters et Setters
    public Long getIdDirige() { return idDirige; }
    public void setIdDirige(Long idDirige) { this.idDirige = idDirige; }
    
    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }
    
    public Agence getAgence() { return agence; }
    public void setAgence(Agence agence) { this.agence = agence; }
    
    public Exploitation getExploitation() { return exploitation; }
    public void setExploitation(Exploitation exploitation) { this.exploitation = exploitation; }
    
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }
    
    public String getFonction() { return fonction; }
    public void setFonction(String fonction) { this.fonction = fonction; }

    @Override
    public String toString() {
        return fonction + " - " + (personne != null ? personne.getNomComplet() : "Sans personne");
    }
}
