package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.dto.RegistrarUsuariRequest;
import cat.moltpadel.backend.dto.EditarUsuariRequest;
import cat.moltpadel.backend.dto.UsuariResponse;
import cat.moltpadel.backend.service.UsuariService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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

    @PatchMapping("/{id}/soci")
    public ResponseEntity<Void> editarSoci(@PathVariable Long id, @RequestParam boolean soci) {
        usuariService.editarSoci(id, soci);
        return ResponseEntity.noContent().build();
    }
}
