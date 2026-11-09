package cat.moltpadel.desktop.model;

public class Pista {

    private final Long id;
    private final String nom;
    private final String tipus;
    private final boolean activa;

    public Pista(Long id, String nom, String tipus, boolean activa) {
        this.id = id;
        this.nom = nom;
        this.tipus = tipus;
        this.activa = activa;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getTipus() {
        return tipus;
    }

    public boolean isActiva() {
        return activa;
    }

    public String getEstat() {
        if (activa) {
            return "Activa";
        } else {
            return "Inactiva";
        }
    }
}