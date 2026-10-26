package cat.moltpadel.backend.security;

import cat.moltpadel.backend.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        Long pistaId,
        LocalDate data,
        LocalTime hora,
        Reserva.EstatReserva estat
) {
}