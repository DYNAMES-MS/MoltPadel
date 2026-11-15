package cat.moltpadel.backend.security;

import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.repository.UsuariRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@NullMarked
public class UsuariDetailsService implements UserDetailsService {
    private final UsuariRepository usuariRepository;

    public UsuariDetailsService(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuari usuari = usuariRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuari no existeix"));

        return User.builder().username(usuari.getEmail()).password(usuari.getSecret()).roles(usuari.getRol().name()).disabled(!usuari.isActiu()).build();
    }
}