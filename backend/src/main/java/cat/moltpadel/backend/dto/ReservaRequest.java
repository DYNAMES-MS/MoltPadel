package cat.moltpadel.backend.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaRequest(Long pistaId, LocalDate data, LocalTime hora) {
}