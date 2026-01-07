package model;

import jakarta.persistence.*;

@Entity
@Table(name = "villes")
public class Ville {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idville")
    private Long idVille;

    @Column(name = "nomville", nullable = false)
    private String nomVille;

    @Column(name = "codepostal", nullable = false)
    private String codePostal;

    
    public Ville() {}

    
    public Ville(String nomVille, String codePostal) {
        this.nomVille = nomVille;
        this.codePostal = codePostal;
    }

    
    public Long getIdVille() {
        return idVille;
    }

    public void setIdVille(Long idVille) {
        this.idVille = idVille;
    }

    public String getNomVille() {
        return nomVille;
    }

    public void setNomVille(String nomVille) {
        this.nomVille = nomVille;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    @Override
    public String toString() {
        return nomVille + " (" + codePostal + ")";
    }
}
