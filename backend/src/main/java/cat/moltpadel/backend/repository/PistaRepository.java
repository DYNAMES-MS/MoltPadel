package cat.moltpadel.backend.repository;

import cat.moltpadel.backend.model.Pista;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import java.util.List;
import java.util.Optional;

public interface PistaRepository extends JpaRepository<Pista,Long> {
    List<Pista> findByActivaTrue();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Pista> findPerReservaById(Long id);
}
