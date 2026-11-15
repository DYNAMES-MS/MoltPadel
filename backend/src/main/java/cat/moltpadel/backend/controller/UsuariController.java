package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.model.Usuari;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import cat.moltpadel.backend.dto.RegistrarUsuariRequest;
import cat.moltpadel.backend.dto.EditarUsuariRequest;
import cat.moltpadel.backend.dto.UsuariResponse;
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

    public UsuariController(UsuariService usuariService) {
        this.usuariService = usuariService;
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
}
