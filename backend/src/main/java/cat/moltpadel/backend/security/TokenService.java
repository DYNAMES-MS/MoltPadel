package cat.moltpadel.backend.security;

import cat.moltpadel.backend.exception.NoTrobatException;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final UsuariRepository usuariRepository;

    public TokenService(JwtEncoder jwtEncoder, UsuariRepository usuariRepository) {
        this.jwtEncoder = jwtEncoder;
        this.usuariRepository = usuariRepository;
    }

    public String generarToken(Authentication auth) {
        Usuari usuari = usuariRepository.findByEmail(auth.getName()).orElseThrow(() -> new NoTrobatException("Usuari no existeix"));

        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("moltpadel")
                .subject(usuari.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofHours(1)))
                .claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}