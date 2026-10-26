package cat.moltpadel.backend.service;

import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Locale;

@Service
public class SessioService {
    private final AuthenticationManager authenticationManager;

    public SessioService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public Authentication auth (String email, String contrasenya) {
        if (email == null || email.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Email és obligatori");
        }
        if (contrasenya == null || contrasenya.isBlank()) {
            throw new ParametreIncorrecteException("Paràmetre: Contrasenya és obligatori");
        }
        String emailCorregit = email.trim().toLowerCase(Locale.ROOT);

        return authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(emailCorregit, contrasenya));
    }
}