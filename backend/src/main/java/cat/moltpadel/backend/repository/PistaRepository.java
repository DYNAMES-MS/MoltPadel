package cat.moltpadel.backend.repository;

import cat.moltpadel.backend.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PistaRepository extends JpaRepository<Pista,Long> {
    List<Pista> findByActivaTrue();
}
