package cat.moltpadel.backend.dto;

import cat.moltpadel.backend.model.Pista;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ConfirmarReservaResponse(Long pistaId, String nomPista, Pista.TipusPista tipus, LocalDate data, LocalTime hora, BigDecimal preu) {
}
