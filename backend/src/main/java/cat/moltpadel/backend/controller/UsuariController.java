package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.security.RegistrarUsuariRequest;
import cat.moltpadel.backend.service.UsuariService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {
    private final UsuariService usuariService;

    public UsuariController(UsuariService usuariService) {
        this.usuariService = usuariService;
    }
    @PostMapping("/registre")
    public ResponseEntity<?> registrarUsuari(@RequestBody RegistrarUsuariRequest request) {

        usuariService.registrarUsuari(
                request.nom(),
                request.email(),
                request.contrasenya()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
