package model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "est_comptable_pour")
public class EstComptablePour {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcomptablepour")
    private Long idComptablePour;
    
    @ManyToOne
    @JoinColumn(name = "idpersonne", nullable = false)
    private Personne personne;
    
    @Column(name = "identite", nullable = false)
    private Long idEntite;
    
    @Column(name = "typeentite")
    private String typeEntite;
    
    @Column(name = "datedebut")
    @Temporal(TemporalType.DATE)
    private Date dateDebut;
    
    
    public EstComptablePour() {}

    
    public EstComptablePour(Personne personne, Long idEntite, String typeEntite, Date dateDebut) {
        this.personne = personne;
        this.idEntite = idEntite;
        this.typeEntite = typeEntite;
        this.dateDebut = dateDebut;
    }
    
    
    public Long getIdComptablePour() { return idComptablePour; }
    public void setIdComptablePour(Long idComptablePour) { this.idComptablePour = idComptablePour; }
    
    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }
    
    public Long getIdEntite() { return idEntite; }
    public void setIdEntite(Long idEntite) { this.idEntite = idEntite; }
    
    public String getTypeEntite() { return typeEntite; }
    public void setTypeEntite(String typeEntite) { this.typeEntite = typeEntite; }
    
    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    @Override
    public String toString() {
        return typeEntite + " - " + (personne != null ? personne.getNomComplet() : "Sans personne");
    }
}
