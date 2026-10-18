package cat.moltpadel.backend.service;

import cat.moltpadel.backend.repository.UsuariRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuariService {
    private final UsuariRepository usuariRepository;

    public UsuariService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

}