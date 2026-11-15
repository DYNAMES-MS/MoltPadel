package cat.moltpadel.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private final Long id;
    private final Long pistaId;
    private final String nomPista;
    private final String nomUsuari;
    private final LocalDate data;
    private final LocalTime hora;
    private final String estat;
    private final BigDecimal preu;
    private final String tarifa;

    public Reserva(Long id, Long pistaId, String nomPista, String nomUsuari, LocalDate data, LocalTime hora, String estat, BigDecimal preu, String tarifa ) {
        this.id = id;
        this.pistaId = pistaId;
        this.nomPista = nomPista;
        this.nomUsuari = nomUsuari;
        this.data = data;
        this.hora = hora;
        this.estat = estat;
        this.preu = preu;
        this.tarifa = tarifa;

    }

    public Long getId() {
        return id;
    }

    public Long getPistaId() {
        return pistaId;
    }

    public String getNomPista() {
        return nomPista;
    }

    public String getNomUsuari() {
        return nomUsuari;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getEstat() {
        return estat;
    }

    public BigDecimal getPreu() {
        return preu;
    }

    public String getTarifa() {
        return tarifa;
    }
}
