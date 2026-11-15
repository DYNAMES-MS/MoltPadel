package cat.moltpadel.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ConfirmarReserva {

    private final Long pistaId;
    private final String nomPista;
    private final String tipus;
    private final LocalDate data;
    private final LocalTime hora;
    private final BigDecimal preu;

    public ConfirmarReserva(Long pistaId, String nomPista, String tipus, LocalDate data, LocalTime hora, BigDecimal preu) {
        this.pistaId = pistaId;
        this.nomPista = nomPista;
        this.tipus = tipus;
        this.data = data;
        this.hora = hora;
        this.preu = preu;
    }

    public Long getPistaId() {
        return pistaId;
    }

    public String getNomPista() {
        return nomPista;
    }

    public String getTipus() {
        return tipus;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public BigDecimal getPreu() {
        return preu;
    }
}
