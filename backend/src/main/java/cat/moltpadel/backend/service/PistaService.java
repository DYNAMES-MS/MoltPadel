package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
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

    public List<Pista> getPistes() {
        return pistaRepository.findAll();
    }

    public List<Pista> getPistesActives() {
        return pistaRepository.findByActivaTrue();
    }

    public Pista crearPista(Pista pista) {
        if (pista.getNom() == null || pista.getNom().isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (pista.getTipus() == null) {
            throw new ParametreIncorrecteException("Paràmetre: Tipus és obligatori");
        }

        Pista novaPista = new Pista();

        novaPista.setNom(pista.getNom());
        novaPista.setTipus(pista.getTipus());
        novaPista.setActiva(false);

        return pistaRepository.save(novaPista);
    }

    public Pista editarPista(Pista nova, Long id) {
        Pista pista = pistaRepository.findById(id).orElseThrow(() -> new NoTrobatException("Pista amb ID:" + id + " no existeix"));

        if (nova.getNom() == null || nova.getNom().isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (nova.getTipus() == null) {
            throw new ParametreIncorrecteException("Paràmetre: Tipus és obligatori");
        }
        pista.setNom(nova.getNom());
        pista.setTipus(nova.getTipus());

        return pistaRepository.save(pista);
    }

    public Pista estatPista(Long id, boolean estat) {
        Pista pista = pistaRepository.findById(id).orElseThrow(() -> new NoTrobatException("Pista amb ID:" + id + " no existeix"));
        pista.setActiva(estat);

        return pistaRepository.save(pista);
    }
}
