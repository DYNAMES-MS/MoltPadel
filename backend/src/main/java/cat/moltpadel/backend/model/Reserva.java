package cat.moltpadel.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate data;
    @Column(nullable = false)
    private LocalTime hora;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstatReserva estat;

    @ManyToOne
    @JoinColumn(name = "usuari_id", nullable = false)
    private Usuari usuari;

    @ManyToOne
    @JoinColumn(name = "pista_id", nullable = false)
    private Pista pista;

    public enum EstatReserva {
        ACTIVA,
        CANCELADA
    }

    public Long getId() {
        return id;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public Pista getPista() {
        return pista;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public EstatReserva getEstat() {
        return estat;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public void setPista(Pista pista) {
        this.pista = pista;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setEstat(EstatReserva estat) {
        this.estat = estat;
    }
}
