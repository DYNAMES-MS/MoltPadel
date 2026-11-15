package cat.moltpadel.backend.dto;

import cat.moltpadel.backend.model.Reserva;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(Long id, Long pistaId, String nomPista, String nomUsuari, LocalDate data, LocalTime hora, Reserva.EstatReserva estat, BigDecimal preu, Reserva.Tarifa tarifa) {
}