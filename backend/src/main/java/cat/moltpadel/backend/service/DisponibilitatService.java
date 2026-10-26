package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Pista;
import cat.moltpadel.backend.model.Reserva;
import cat.moltpadel.backend.repository.PistaRepository;
import cat.moltpadel.backend.repository.ReservaRepository;
import cat.moltpadel.backend.security.DisponibilitatResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;


@Service
public class DisponibilitatService {
    private final PistaRepository pistaRepository;
    private final ReservaRepository reservaRepository;

    public DisponibilitatService(PistaRepository pistaRepository, ReservaRepository reservaRepository) {
        this.pistaRepository = pistaRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional(readOnly = true)
    public List<DisponibilitatResponse> getDisponibilitatSetmanal(LocalDate data) {
        if (data == null) {
            throw new ParametreIncorrecteException("Paràmetre: Data és obligatori");
        }

        LocalDate dilluns = data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate diumenge = dilluns.plusDays(6);

        List<Pista> pistes = pistaRepository.findByActivaTrue();
        List<Long> idsPistesActives = new ArrayList<>();

        for (Pista pista : pistes) {
            idsPistesActives.add(pista.getId());
        }

        List<Reserva> reserves = reservaRepository.findByDataBetweenAndEstat(dilluns, diumenge, Reserva.EstatReserva.ACTIVA);
        List<DisponibilitatResponse> resultat = new ArrayList<>();
        LocalDateTime ara = LocalDateTime.now(ZoneId.of("Europe/Madrid"));

        for (int dia = 0; dia < 7; dia++) {

            LocalDate dataFranja = dilluns.plusDays(dia);

            for (int hora = 8; hora <= 22; hora++) {

                LocalTime horaFranja = LocalTime.of(hora, 0);
                List<Long> pistesOcupades = new ArrayList<>();

                for (Reserva reserva : reserves) {
                    if (reserva.getData().equals(dataFranja) && reserva.getHora().equals(horaFranja) && idsPistesActives.contains(reserva.getPista().getId())) {
                        pistesOcupades.add(reserva.getPista().getId());
                    }
                }

                int disponibles = pistes.size() - pistesOcupades.size();
                boolean caducada = !LocalDateTime.of(dataFranja, horaFranja).isAfter(ara);

                resultat.add(new DisponibilitatResponse(dataFranja, horaFranja, disponibles, pistes.size(), caducada));
            }
        }
        return resultat;
    }

    @Transactional(readOnly = true)
    public List<Pista> getPistesDisponibles(LocalDate data, LocalTime hora) {
        ValidarHorari.validar(data, hora);

        List<Pista> pistes = pistaRepository.findByActivaTrue();
        List<Pista> disponibles = new ArrayList<>();

        for (Pista pista : pistes) {
            boolean ocupada = reservaRepository.existsByPistaIdAndDataAndHoraAndEstat(pista.getId(), data, hora, Reserva.EstatReserva.ACTIVA);
            if (!ocupada) {
                disponibles.add(pista);
            }
        }
        return disponibles;
    }
}
