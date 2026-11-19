package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.dto.*;
import cat.moltpadel.backend.exception.ParametreIncorrecteException;
import cat.moltpadel.backend.model.Usuari;
import cat.moltpadel.backend.service.RecuperacioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import cat.moltpadel.backend.service.UsuariService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {
    private final UsuariService usuariService;
    private final RecuperacioService recuperacioService;

    public UsuariController(UsuariService usuariService, RecuperacioService recuperacioService) {
        this.usuariService = usuariService;
        this.recuperacioService = recuperacioService;
    }

    @GetMapping
    public List<UsuariResponse> getUsuaris() {
        return usuariService.getUsuaris();
    }

    @PostMapping("/registre")
    public ResponseEntity<Void> registrarUsuari(@RequestBody RegistrarUsuariRequest request) {
        usuariService.registrarUsuari(request.nom(), request.email(), request.contrasenya());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editarUsuari(@PathVariable Long id, @RequestBody EditarUsuariRequest request) {
        usuariService.editarUsuari(id, request.nom(), request.email());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/soci")
    public ResponseEntity<UsuariResponse> getSoci(@AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        UsuariResponse resposta = usuariService.getSoci(usuariId);

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/soci")
    public ResponseEntity<Void> activarSoci(@RequestParam Usuari.TipusSoci tipusSoci, @AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        usuariService.activarSoci(usuariId, tipusSoci);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estat")
    public ResponseEntity<Void> canviarEstatCompte(@PathVariable Long id, @RequestParam boolean actiu, @AuthenticationPrincipal Jwt jwt) {
        Long administradorId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        usuariService.canviarEstatCompte(id, actiu, administradorId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/contrasenya")
    public ResponseEntity<Void> canviarContrasenya(@RequestBody CanviarContrasenyaRequest request, @AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        usuariService.canviarContrasenya(usuariId, request.contrasenyaActual(), request.contrasenyaNova());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recuperacio/codi")
    public ResponseEntity<Void> demanarCodi(@RequestBody CodiRecuperacioRequest request) {
        recuperacioService.demanarCodi(request.email());
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/recuperacio/restablir")
    public ResponseEntity<Void> restablirContrasenya(@RequestBody RestablirContrasenyaRequest request) {
        recuperacioService.restablirContrasenya(request.email(), request.codi(), request.contrasenyaNova());
        return ResponseEntity.noContent().build();
    }
}
