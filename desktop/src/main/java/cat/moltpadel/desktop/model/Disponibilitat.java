package cat.moltpadel.desktop.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Disponibilitat {

    private final LocalDate data;
    private final LocalTime hora;
    private final int disponibles;
    private final int totalPistes;
    private final boolean caducada;

    public Disponibilitat(LocalDate data, LocalTime hora, int disponibles, int totalPistes, boolean caducada) {
        this.data = data;
        this.hora = hora;
        this.disponibles = disponibles;
        this.totalPistes = totalPistes;
        this.caducada = caducada;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public int getTotalPistes() {
        return totalPistes;
    }

    public boolean isCaducada() {
        return caducada;
    }
}
