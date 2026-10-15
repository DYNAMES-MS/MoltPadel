package cat.moltpadel.backend.service;

import cat.moltpadel.backend.model.Pista;
import cat.moltpadel.backend.repository.PistaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PistaService {
    private final PistaRepository pistaRepository;
    public PistaService(PistaRepository pistaRepository) {
        this.pistaRepository = pistaRepository;
    }

    public List<Pista> getPistes(){
        return pistaRepository.findAll();
    }
    public List<Pista> getPistesActives(){
        return pistaRepository.findByActivaTrue();
    }
    public Pista crearPista(Pista pista) {
        pista.setActiva(false);
        return pistaRepository.save(pista);
    }
}
