package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Reserva;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.ReservaRepository;
import cat.moltpadel.backend.repository.UsuariRepository;
import cat.moltpadel.backend.dto.UsuariResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
import java.util.Optional;


@Service
public class UsuariService {
    private final UsuariRepository usuariRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public UsuariService(UsuariRepository usuariRepository, ReservaRepository reservaRepository, PasswordEncoder passwordEncoder) {
        this.usuariRepository = usuariRepository;
        this.reservaRepository = reservaRepository;
        this.passwordEncoder = passwordEncoder;

    }

    public void registrarUsuari(String nom, String email, String contrasenya) {

        if (nom == null || nom.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }

        if (contrasenya == null || contrasenya.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya és obligatori");
        }

        if (contrasenya.length() < 10) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya ha de tenir almenys 10 caràcters");
        }

        String emailCorregit = email.trim().toLowerCase(Locale.ROOT);

        if (!emailCorregit.matches(EMAIL_REGEX)) {
            throw new ParametreIncorrecteException("Format incorrecte d'Email");
        }

        if (usuariRepository.findByEmail(emailCorregit).isPresent()) {
            throw new ParametreIncorrecteException("Paràmetre: Email ja existeix");
        }

        Usuari nouUsuari = new Usuari();
        nouUsuari.setNom(nom.trim());
        nouUsuari.setEmail(emailCorregit);
        nouUsuari.setSecret(passwordEncoder.encode(contrasenya));
        nouUsuari.setRol(Usuari.Rol.USER);
        nouUsuari.setActiu(true);

        usuariRepository.save(nouUsuari);
    }

    public List<UsuariResponse> getUsuaris() {
        List<UsuariResponse> usuaris = new ArrayList<>();

        for (Usuari usuari : usuariRepository.findAll()) {
            UsuariResponse resposta = new UsuariResponse(usuari.getId(), usuari.getNom(), usuari.getEmail(), usuari.isSoci(), usuari.getRol(), usuari.isActiu(), usuari.getTipusSoci(), usuari.getIniciSoci(), usuari.getFiSoci());
            usuaris.add(resposta);
        }
        return usuaris;
    }

    @Transactional
    public void editarUsuari(Long id, String nom, String email) {
        Usuari usuari = usuariRepository.findEditarById(id).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));

        if (nom == null || nom.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Nom és obligatori");
        }

        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }

        String emailCorregit = email.trim().toLowerCase(Locale.ROOT);

        if (!emailCorregit.matches(EMAIL_REGEX)) {
            throw new ParametreIncorrecteException("Format incorrecte d'Email");
        }

        Optional<Usuari> existent = usuariRepository.findByEmail(emailCorregit);
        if (existent.isPresent() && !existent.get().getId().equals(id)) {
            throw new ParametreIncorrecteException("Paràmetre: Email ja existeix");
        }

        usuari.setNom(nom.trim());
        usuari.setEmail(emailCorregit);
        usuariRepository.save(usuari);
    }

    @Transactional(readOnly = true)
    public UsuariResponse getSoci(Long usuariId) {
        Usuari usuari = usuariRepository.findById(usuariId).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));

        return new UsuariResponse(usuari.getId(), usuari.getNom(), usuari.getEmail(), usuari.isSoci(), usuari.getRol(), usuari.isActiu(), usuari.getTipusSoci(), usuari.getIniciSoci(), usuari.getFiSoci());
    }

    @Transactional
    public void activarSoci(Long usuariId, Usuari.TipusSoci tipusSoci) {
        Usuari usuari = usuariRepository.findEditarById(usuariId).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));
        if (!usuari.isActiu()) {
            throw new ParametreIncorrecteException("El compte està inactiu");
        }

        LocalDate ara = LocalDate.now(ZoneId.of("Europe/Madrid"));
        if (usuari.sociVigent(ara)) {
            throw new ParametreIncorrecteException("Ja tens una quota de soci vigent");
        }

        LocalDate fiSoci;
        if (tipusSoci == Usuari.TipusSoci.MENSUAL) {
            fiSoci = ara.plusMonths(1);
        } else {
            fiSoci = ara.plusYears(1);
        }

        usuari.setTipusSoci(tipusSoci);
        usuari.setIniciSoci(ara);
        usuari.setFiSoci(fiSoci);

        usuariRepository.save(usuari);
    }

    @Transactional
    public void canviarEstatCompte(Long id, boolean actiu, Long administradorId) {
        Usuari usuari = usuariRepository.findEditarById(id).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));

        if (!actiu) {
            if (id.equals(administradorId)) {
                throw new ParametreIncorrecteException("No et pots donar de baixa a tu mateix");
            }

            List<Reserva> reserves = reservaRepository.findByUsuari_IdOrderByDataDescHoraDesc(id);
            for (Reserva reserva : reserves) {
                LocalDateTime inici = LocalDateTime.of(reserva.getData(), reserva.getHora());

                if (reserva.getEstat() == Reserva.EstatReserva.ACTIVA && inici.isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
                    throw new ParametreIncorrecteException("L'usuari té reserves actives futures");
                }
            }
        }

        usuari.setActiu(actiu);
        usuariRepository.save(usuari);
    }
}