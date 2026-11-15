package cat.moltpadel.backend.service;

import cat.moltpadel.backend.dto.ConfirmarReservaResponse;
import cat.moltpadel.backend.dto.ReservaResponse;
import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Pista;
import cat.moltpadel.backend.model.Reserva;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.PistaRepository;
import cat.moltpadel.backend.repository.ReservaRepository;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PistaRepository pistaRepository;
    private final UsuariRepository usuariRepository;
    private final BigDecimal preuInterior;
    private final BigDecimal preuExterior;
    private final int limitReservesSoci;

    public ReservaService(ReservaRepository reservaRepository, PistaRepository pistaRepository, UsuariRepository usuariRepository, @Value("${moltpadel.preus.interior}") BigDecimal preuInterior, @Value("${moltpadel.preus.exterior}") BigDecimal preuExterior, @Value("${moltpadel.reserves.limit-soci}") int limitReservesSoci) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
        this.usuariRepository = usuariRepository;
        this.preuInterior = preuInterior;
        this.preuExterior = preuExterior;
        this.limitReservesSoci = limitReservesSoci;
    }

    @Transactional
    public ReservaResponse crearReserva(Long pistaId, LocalDate data, LocalTime hora, Long usuariId) {
        if (pistaId == null) {
            throw new ParametreIncorrecteException("Paràmetre: Pista és obligatori");
        }

        ValidarHorari.validar(data, hora);

        Usuari usuari = usuariRepository.findEditarById(usuariId).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));
        if (!usuari.isActiu()) {
            throw new ParametreIncorrecteException("El compte està inactiu");
        }

        List<Reserva> reserves = reservaRepository.findByUsuari_IdOrderByDataDescHoraDesc(usuariId);
        int reservesSoci = 0;
        for (Reserva existent : reserves) {
            if (existent.getEstat() == Reserva.EstatReserva.ACTIVA) {
                if (existent.getData().equals(data) && existent.getHora().equals(hora)) {
                    throw new ParametreIncorrecteException("Ja tens una reserva en aquesta franja");
                }

                LocalDateTime inici = LocalDateTime.of(existent.getData(), existent.getHora());

                if (existent.getTarifa() == Reserva.Tarifa.SOCI && inici.isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
                    reservesSoci++;
                }
            }
        }

        if (usuari.sociVigent(data) && reservesSoci >= limitReservesSoci) {
            throw new ParametreIncorrecteException("Ja tens el màxim de " + limitReservesSoci + " reserves futures amb tarifa de soci");
        }

        Pista pista = pistaRepository.findEditarById(pistaId).orElseThrow(() -> new NoTrobatException("Pista no existeix"));
        if (!pista.isActiva()) {
            throw new ParametreIncorrecteException("Aquesta pista no està disponible");
        }

        boolean ocupada = reservaRepository.existsByPistaIdAndDataAndHoraAndEstat(pistaId, data, hora, Reserva.EstatReserva.ACTIVA);

        if (ocupada) {
            throw new ParametreIncorrecteException("Aquesta pista ja està reservada");
        }

        BigDecimal preu;
        Reserva.Tarifa tarifa;

        if (usuari.sociVigent(data)) {
            preu = BigDecimal.ZERO;
            tarifa = Reserva.Tarifa.SOCI;
        } else {
            tarifa = Reserva.Tarifa.GENERAL;

            if (pista.getTipus() == Pista.TipusPista.INTERIOR) {
                preu = preuInterior;
            } else {
                preu = preuExterior;
            }
        }

        Reserva reserva = new Reserva();

        reserva.setUsuari(usuari);
        reserva.setPista(pista);
        reserva.setData(data);
        reserva.setHora(hora);
        reserva.setEstat(Reserva.EstatReserva.ACTIVA);
        reserva.setPreu(preu);
        reserva.setTarifa(tarifa);

        Reserva reservaCreada = reservaRepository.save(reserva);

        return new ReservaResponse(reservaCreada.getId(), reservaCreada.getPista().getId(), reservaCreada.getPista().getNom(), reservaCreada.getUsuari().getNom(), reservaCreada.getData(), reservaCreada.getHora(), reservaCreada.getEstat(), reservaCreada.getPreu(), reservaCreada.getTarifa());
    }

    @Transactional
    public ReservaResponse cancelarReserva(Long id, Long usuariId) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new NoTrobatException("Reserva no existeix"));

        if (!reserva.getUsuari().getId().equals(usuariId)) {
            throw new NoTrobatException("Reserva no existeix");
        }

        if (reserva.getEstat() == Reserva.EstatReserva.CANCELADA) {
            throw new ParametreIncorrecteException("Aquesta reserva ja està cancel·lada");
        }

        if (!LocalDateTime.of(reserva.getData(), reserva.getHora()).isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
            throw new ParametreIncorrecteException("No es pot cancel·lar una reserva que ja ha començat");
        }

        reserva.setEstat(Reserva.EstatReserva.CANCELADA);
        Reserva reservaCancelada = reservaRepository.save(reserva);

        return new ReservaResponse(reservaCancelada.getId(), reservaCancelada.getPista().getId(), reservaCancelada.getPista().getNom(), reservaCancelada.getUsuari().getNom(), reservaCancelada.getData(), reservaCancelada.getHora(), reservaCancelada.getEstat(), reservaCancelada.getPreu(), reservaCancelada.getTarifa());
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> getReservesPropies(Long usuariId) {
        List<Reserva> reserves = reservaRepository.findByUsuari_IdOrderByDataDescHoraDesc(usuariId);
        List<ReservaResponse> resposta = new ArrayList<>();
        for (Reserva reserva : reserves) {
            ReservaResponse reservaResponse = new ReservaResponse(reserva.getId(), reserva.getPista().getId(), reserva.getPista().getNom(), reserva.getUsuari().getNom(), reserva.getData(), reserva.getHora(), reserva.getEstat(), reserva.getPreu(), reserva.getTarifa());
            resposta.add(reservaResponse);
        }

        return resposta;
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> getReservesPerData(LocalDate data) {
        List<Reserva> reserves = reservaRepository.findByDataOrderByHoraAsc(data);
        List<ReservaResponse> resposta = new ArrayList<>();
        for (Reserva reserva : reserves) {
            ReservaResponse reservaResponse = new ReservaResponse(reserva.getId(), reserva.getPista().getId(), reserva.getPista().getNom(), reserva.getUsuari().getNom(), reserva.getData(), reserva.getHora(), reserva.getEstat(), reserva.getPreu(), reserva.getTarifa());
            resposta.add(reservaResponse);
        }

        return resposta;
    }

    @Transactional(readOnly = true)
    public ConfirmarReservaResponse getConfirmarReserva(Long pistaId, LocalDate data, LocalTime hora, Long usuariId) {
        ValidarHorari.validar(data, hora);
        Usuari usuari = usuariRepository.findById(usuariId).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));
        Pista pista = pistaRepository.findById(pistaId).orElseThrow(() -> new NoTrobatException("Pista no existeix"));
        if (!pista.isActiva()) {
            throw new ParametreIncorrecteException("Aquesta pista no està disponible");
        }

        boolean ocupada = reservaRepository.existsByPistaIdAndDataAndHoraAndEstat(pistaId, data, hora, Reserva.EstatReserva.ACTIVA);
        if (ocupada) {
            throw new ParametreIncorrecteException("Aquesta pista ja està reservada");
        }

        BigDecimal preu;
        if (usuari.sociVigent(data)) {
            preu = BigDecimal.ZERO;
        } else if (pista.getTipus() == Pista.TipusPista.INTERIOR) {
            preu = preuInterior;
        } else {
            preu = preuExterior;
        }

        return new ConfirmarReservaResponse(pista.getId(), pista.getNom(), pista.getTipus(), data, hora, preu);
    }
}
