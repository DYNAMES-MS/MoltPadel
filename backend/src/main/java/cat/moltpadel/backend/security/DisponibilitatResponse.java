package cat.moltpadel.backend.security;

import java.time.LocalDate;
import java.time.LocalTime;

public record DisponibilitatResponse(
        LocalDate data,
        LocalTime hora,
        int disponibles,
        int totalPistes,
        boolean caducada
) {
}