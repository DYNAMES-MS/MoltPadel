package cat.moltpadel.backend.model;

import jakarta.persistence.*;

@Entity
public class Usuari {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Column(nullable = false, unique = true)
    private String email;
    private String secret;
    private boolean soci;
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public enum Rol {
        ADMIN,
        USER
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public String getSecret() {
        return secret;
    }

    public boolean isSoci() {
        return soci;
    }

    public Rol getRol() {
        return rol;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setSoci(boolean soci) {
        this.soci = soci;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
