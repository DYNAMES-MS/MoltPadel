package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.UsuariRepository;
import cat.moltpadel.backend.dto.UsuariResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import java.util.Optional;

@Service
public class UsuariService {
    private final UsuariRepository usuariRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public UsuariService(UsuariRepository usuariRepository, PasswordEncoder passwordEncoder) {
        this.usuariRepository = usuariRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrarUsuari(String nom, String email, String contrasenya) {

        if (nom == null || nom.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }

        if (contrasenya == null || contrasenya.length()<10) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya ha de tenir almenys 10 caràcters");
        }

        String emailCorregit = email.trim().toLowerCase(Locale.ROOT);

        if (!emailCorregit.matches(EMAIL_REGEX)) {
            throw new ParametreIncorrecteException("Paràmetre: Format incorrecte d'Email ");
        }

        if (usuariRepository.findByEmail(emailCorregit).isPresent()) {
            throw new ParametreIncorrecteException("Paràmetre: Email ja existeix");
        }

        Usuari nouUsuari = new Usuari();
        nouUsuari.setNom(nom);
        nouUsuari.setEmail(emailCorregit);
        nouUsuari.setSecret(passwordEncoder.encode(contrasenya));
        nouUsuari.setRol(Usuari.Rol.USER);
        nouUsuari.setSoci(false);

        usuariRepository.save(nouUsuari);
    }

    public List<UsuariResponse> getUsuaris() {
        List<UsuariResponse> usuaris = new ArrayList<>();

        for (Usuari usuari : usuariRepository.findAll()) {
            UsuariResponse resposta = new UsuariResponse(usuari.getId(), usuari.getNom(), usuari.getEmail(), usuari.isSoci(), usuari.getRol());
            usuaris.add(resposta);
        }
        return usuaris;
    }

    public void editarUsuari(Long id, String nom, String email) {
        Usuari usuari = usuariRepository.findById(id).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));

        if (nom == null || nom.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }

        String emailCorregit = email.trim().toLowerCase(Locale.ROOT);

        if (!emailCorregit.matches(EMAIL_REGEX)) {
            throw new ParametreIncorrecteException("Paràmetre: Format incorrecte d'Email");
        }

        Optional<Usuari> existent = usuariRepository.findByEmail(emailCorregit);
        if (existent.isPresent() && !existent.get().getId().equals(id)) {
            throw new ParametreIncorrecteException("Paràmetre: Email ja existeix");
        }

        usuari.setNom(nom.trim());
        usuari.setEmail(emailCorregit);
        usuariRepository.save(usuari);
    }

    public void editarSoci(Long id, boolean soci) {
        Usuari usuari = usuariRepository.findById(id).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));
        usuari.setSoci(soci);
        usuariRepository.save(usuari);
    }
}