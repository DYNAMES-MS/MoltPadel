package cat.moltpadel.backend.repository;

import cat.moltpadel.backend.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    Optional<Usuari> findByEmail(String email);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuari> findEditarById(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Usuari> findEditarByEmail(String email);
}
