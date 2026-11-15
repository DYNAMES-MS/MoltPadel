package cat.moltpadel.desktop.model;

import java.time.LocalDate;

public class Usuari {

    private final Long id;
    private final String nom;
    private final String email;
    private final String rol;
    private final boolean soci;
    private final String tipusSoci;
    private final LocalDate iniciSoci;
    private final LocalDate fiSoci;
    private final boolean actiu;

    public Usuari(Long id, String nom, String email, String rol, boolean soci, boolean actiu, String tipusSoci, LocalDate iniciSoci, LocalDate fiSoci) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.rol = rol;
        this.soci = soci;
        this.actiu = actiu;
        this.tipusSoci = tipusSoci;
        this.iniciSoci = iniciSoci;
        this.fiSoci = fiSoci;
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

    public String getRol() {
        return rol;
    }

    public boolean isSoci() {
        return soci;
    }

    public String getEstatSoci() {
        if (soci) {
            return "Sí";
        } else {
            return "No";
        }
    }

    public boolean isActiu() {
        return actiu;
    }

    public String getEstatCompte() {
        if (actiu) {
            return "Actiu";
        } else {
            return "Inactiu";
        }
    }

    public String getTipusSoci() {
        return tipusSoci;
    }

    public LocalDate getIniciSoci() {
        return iniciSoci;
    }

    public LocalDate getFiSoci() {
        return fiSoci;
    }
}
