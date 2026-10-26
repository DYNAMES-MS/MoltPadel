package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Pista;
import cat.moltpadel.backend.model.Reserva;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.PistaRepository;
import cat.moltpadel.backend.repository.ReservaRepository;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PistaRepository pistaRepository;
    private final UsuariRepository usuariRepository;

    public ReservaService(ReservaRepository reservaRepository, PistaRepository pistaRepository, UsuariRepository usuariRepository) {
        this.reservaRepository = reservaRepository;
        this.pistaRepository = pistaRepository;
        this.usuariRepository = usuariRepository;
    }

    @Transactional
    public Reserva crearReserva(Long pistaId, LocalDate data, LocalTime hora, String usuariEmail) {
        if (pistaId == null) {
            throw new ParametreIncorrecteException("Paràmetre: Pista és obligatori");
        }

        ValidarHorari.validar(data, hora);

        Usuari usuari = usuariRepository.findByEmail(usuariEmail).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));
        Pista pista = pistaRepository.findPerReservaById(pistaId).orElseThrow(() -> new NoTrobatException("Pista no existeix"));

        if (!pista.isActiva()) {
            throw new ParametreIncorrecteException("Aquesta pista no està disponible");
        }

        boolean ocupada = reservaRepository.existsByPistaIdAndDataAndHoraAndEstat(pistaId, data, hora, Reserva.EstatReserva.ACTIVA);
        if (ocupada) {
            throw new ParametreIncorrecteException("Aquesta pista ja està reservada");
        }

        Reserva reserva = new Reserva();

        reserva.setUsuari(usuari);
        reserva.setPista(pista);
        reserva.setData(data);
        reserva.setHora(hora);
        reserva.setEstat(Reserva.EstatReserva.ACTIVA);

        return reservaRepository.save(reserva);
    }

    public Reserva cancelarReserva(Long id, String usuariEmail) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new NoTrobatException("Reserva amb ID:" + id + " no existeix"));
        if (!reserva.getUsuari().getEmail().equals(usuariEmail)) {
            throw new NoTrobatException("Reserva no existeix");
        }
        if (reserva.getEstat() == Reserva.EstatReserva.CANCELADA) {
            throw new ParametreIncorrecteException("Aquesta reserva ja està cancel·lada");
        }
        reserva.setEstat(Reserva.EstatReserva.CANCELADA);

        return reservaRepository.save(reserva);
    }

    @Transactional(readOnly = true)
    public List<Reserva> getReservesPropies(String usuariEmail) {
        return reservaRepository.findByUsuari_EmailOrderByDataDescHoraDesc(usuariEmail);
    }
}
