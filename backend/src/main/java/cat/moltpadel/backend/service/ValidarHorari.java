package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.ParametreIncorrecteException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class ValidarHorari {

    public static void validar(LocalDate data, LocalTime hora) {

        if (data == null || hora == null) {
            throw new ParametreIncorrecteException("Paràmetres: Data-hora són obligatòries");
        }
        if (hora.isBefore(LocalTime.of(8, 0)) || hora.isAfter(LocalTime.of(22, 0)) || hora.getMinute() != 0 || hora.getSecond() != 0 || hora.getNano() != 0) {
            throw new ParametreIncorrecteException("L'horari de reserva és entre 08:00 i 22:00");
        }

        LocalDateTime inici = LocalDateTime.of(data, hora);

        if (!inici.isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
            throw new ParametreIncorrecteException("No es poden reservar dates o hores anteriors");
        }
    }
}
