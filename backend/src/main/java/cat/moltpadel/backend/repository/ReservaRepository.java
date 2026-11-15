package cat.moltpadel.backend.repository;

import cat.moltpadel.backend.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva,Long> {
    List<Reserva> findByUsuari_IdOrderByDataDescHoraDesc(Long usuariId);
    boolean existsByPistaIdAndDataAndHoraAndEstat(Long pistaId, LocalDate data, LocalTime hora, Reserva.EstatReserva estat);
    List<Reserva> findByDataBetweenAndEstat(LocalDate dataInici, LocalDate dataFi, Reserva.EstatReserva estat);
    List<Reserva> findByDataOrderByHoraAsc(LocalDate data);
}
