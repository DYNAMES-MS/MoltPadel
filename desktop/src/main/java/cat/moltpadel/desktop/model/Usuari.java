package cat.moltpadel.desktop.model;

public class Usuari {

    private final Long id;
    private final String nom;
    private final String email;
    private final String rol;
    private final boolean soci;

    public Usuari(Long id, String nom, String email, String rol, boolean soci) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.rol = rol;
        this.soci = soci;
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
}
