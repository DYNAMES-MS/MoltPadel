package cat.moltpadel.backend.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
public class Usuari {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    @Column(nullable = false, unique = true)
    private String email;
    private String secret;
    @Enumerated(EnumType.STRING)
    private TipusSoci tipusSoci;
    private LocalDate iniciSoci;
    private LocalDate fiSoci;
    @Column(nullable = false)
    private boolean actiu = true;
    @Enumerated(EnumType.STRING)
    private Rol rol;
    private String codiRecuperacio;
    private LocalDateTime caducitatRecuperacio;
    @Column(nullable = false)
    private int intentsRecuperacio;

    public enum Rol {
        ADMIN,
        USER
    }

    public enum TipusSoci {
        MENSUAL,
        ANUAL
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
        return sociVigent(LocalDate.now(ZoneId.of("Europe/Madrid")));
    }

    public boolean sociVigent(LocalDate data) {
        if (tipusSoci == null || iniciSoci == null || fiSoci == null) {
            return false;
        }

        return !data.isBefore(iniciSoci) && data.isBefore(fiSoci);
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

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean isActiu() {
        return actiu;
    }

    public void setActiu(boolean actiu) {
        this.actiu = actiu;
    }

    public TipusSoci getTipusSoci() {
        return tipusSoci;
    }

    public void setTipusSoci(TipusSoci tipusSoci) {
        this.tipusSoci = tipusSoci;
    }

    public LocalDate getIniciSoci() {
        return iniciSoci;
    }

    public void setIniciSoci(LocalDate iniciSoci) {
        this.iniciSoci = iniciSoci;
    }

    public LocalDate getFiSoci() {
        return fiSoci;
    }

    public void setFiSoci(LocalDate fiSoci) {
        this.fiSoci = fiSoci;
    }

    public String getCodiRecuperacio() {
        return codiRecuperacio;
    }

    public void setCodiRecuperacio(String codiRecuperacio) {
        this.codiRecuperacio = codiRecuperacio;
    }

    public LocalDateTime getCaducitatRecuperacio() {
        return caducitatRecuperacio;
    }

    public void setCaducitatRecuperacio(LocalDateTime caducitatRecuperacio) {
        this.caducitatRecuperacio = caducitatRecuperacio;
    }

    public int getIntentsRecuperacio() {
        return intentsRecuperacio;
    }

    public void setIntentsRecuperacio(int intentsRecuperacio) {
        this.intentsRecuperacio = intentsRecuperacio;
    }
}
