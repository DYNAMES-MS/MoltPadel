package cat.moltpadel.backend.security;

import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Optional;

@NullMarked
@Component
public class ValidarUsuariActiu implements OAuth2TokenValidator<Jwt> {
    private final UsuariRepository usuariRepository;

    public ValidarUsuariActiu(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        OAuth2Error error = new OAuth2Error("invalid_token");
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));

        Optional<Usuari> usuari = usuariRepository.findById(usuariId);
        if (usuari.isEmpty() || !usuari.get().isActiu()) {
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}