package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.dto.DisponibilitatResponse;
import cat.moltpadel.backend.service.DisponibilitatService;
import cat.moltpadel.backend.service.PistaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cat.moltpadel.backend.model.Pista;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/pistes")
public class PistaController {
    private final PistaService pistaService;
    private final DisponibilitatService disponibilitatService;

    public PistaController(PistaService pistaService, DisponibilitatService disponibilitatService) {
        this.pistaService = pistaService;
        this.disponibilitatService = disponibilitatService;
    }

    @GetMapping
    public List<Pista> getPistes(){
        return pistaService.getPistes();
    }

    @PostMapping
    public ResponseEntity<Pista> crearPista(@RequestBody Pista pista) {
        Pista novaPista = pistaService.crearPista(pista);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaPista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editarPista(@PathVariable Long id, @RequestBody Pista nova) {
        pistaService.editarPista(nova, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estat")
    public ResponseEntity<Void> canviarEstat(@PathVariable Long id, @RequestParam boolean activa) {
        pistaService.canviarEstat(id, activa);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibilitat")
    public List<DisponibilitatResponse> getDisponibilitat(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        if (data == null) {
            data = LocalDate.now(ZoneId.of("Europe/Madrid"));
        }
        return disponibilitatService.getDisponibilitatSetmanal(data);
    }

    @GetMapping("/disponibilitat/franja")
    public List<Pista> getPistesDisponibles(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        return disponibilitatService.getPistesDisponibles(data, hora);
    }
}
