package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;

@Service
public class RecuperacioService {
    private final SecureRandom random = new SecureRandom();
    private final UsuariRepository usuariRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String remitent;

    public RecuperacioService(UsuariRepository usuariRepository, PasswordEncoder passwordEncoder, JavaMailSender mailSender, @Value("${spring.mail.username}") String remitent) {
        this.usuariRepository = usuariRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.remitent = remitent;
    }

    @Async
    @Transactional
    public void demanarCodi(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<Usuari> resultat = usuariRepository.findEditarByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (resultat.isEmpty()) {
            return;
        }

        Usuari usuari = resultat.get();
        if (!usuari.isActiu()) {
            return;
        }
        if (usuari.getCaducitatRecuperacio() != null && usuari.getCaducitatRecuperacio().isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
            return;
        }

        String codi = generarCodi();
        usuari.setCodiRecuperacio(passwordEncoder.encode(codi));
        usuari.setCaducitatRecuperacio(LocalDateTime.now(ZoneId.of("Europe/Madrid")).plusMinutes(10));
        usuari.setIntentsRecuperacio(0);
        usuariRepository.save(usuari);

        SimpleMailMessage missatge = new SimpleMailMessage();
        missatge.setFrom(remitent);
        missatge.setTo(usuari.getEmail());
        missatge.setSubject("Restablir contrasenya de MoltPadel");
        missatge.setText("El teu codi de recuperació és " + codi + ". Caduca en 10 minuts.");

        mailSender.send(missatge);
    }

    private String generarCodi() {
        StringBuilder codi = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            codi.append(random.nextInt(10));
        }

        return codi.toString();
    }

    @Transactional(noRollbackFor = ParametreIncorrecteException.class)
    public void restablirContrasenya(String email, String codi, String contrasenyaNova) {
        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }
        if (codi == null || codi.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Codi és obligatori");
        }
        if (contrasenyaNova == null || contrasenyaNova.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya és obligatori");
        }
        if (contrasenyaNova.length() < 10) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya ha de tenir almenys 10 caràcters");
        }

        Optional<Usuari> resultat = usuariRepository.findEditarByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (resultat.isEmpty()) {
            throw new ParametreIncorrecteException("El codi de recuperació no és vàlid o ha caducat");
        }

        Usuari usuari = resultat.get();
        if (!usuari.isActiu() || usuari.getCodiRecuperacio() == null) {
            throw new ParametreIncorrecteException("El codi de recuperació no és vàlid o ha caducat");
        }

        if (usuari.getCaducitatRecuperacio() == null || !usuari.getCaducitatRecuperacio().isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
            throw new ParametreIncorrecteException("El codi de recuperació no és vàlid o ha caducat");
        }

        String codiIntroduit = codi.trim();
        boolean codiCorrecte = codiIntroduit.length() == 6 && passwordEncoder.matches(codiIntroduit, usuari.getCodiRecuperacio());
        if (!codiCorrecte) {
            int intents = usuari.getIntentsRecuperacio() + 1;
            usuari.setIntentsRecuperacio(intents);
            if (intents >= 3) {
                usuari.setCodiRecuperacio(null);
            }

            usuariRepository.save(usuari);
            throw new ParametreIncorrecteException("El codi de recuperació no és vàlid o ha caducat");
        }

        usuari.setSecret(passwordEncoder.encode(contrasenyaNova));
        usuari.setCodiRecuperacio(null);
        usuari.setCaducitatRecuperacio(null);
        usuari.setIntentsRecuperacio(0);
        usuariRepository.save(usuari);
    }
}