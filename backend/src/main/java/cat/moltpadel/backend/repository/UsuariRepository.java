package cat.moltpadel.backend.repository;

import cat.moltpadel.backend.model.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuariRepository extends JpaRepository<Usuari, Long> {
    Optional<Usuari> findByEmail(String email);
}
