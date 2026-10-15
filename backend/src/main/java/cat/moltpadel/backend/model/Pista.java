package cat.moltpadel.backend.model;

import jakarta.persistence.*;

@Entity
public class Pista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private boolean activa;
    @Enumerated(EnumType.STRING)
    private TipusPista tipus;

    public enum TipusPista {
        INTERIOR,
        EXTERIOR
    }

    public TipusPista getTipus() {
        return tipus;
    }

    public boolean isActiva() {
        return activa;
    }

    public String getNom() {
        return nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public void setTipus(TipusPista tipus) {
        this.tipus = tipus;
    }
}
