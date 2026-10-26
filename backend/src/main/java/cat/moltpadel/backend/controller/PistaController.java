package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.security.DisponibilitatResponse;
import cat.moltpadel.backend.service.DisponibilitatService;
import cat.moltpadel.backend.service.PistaService;
import org.springframework.format.annotation.DateTimeFormat;
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
    public Pista crearPista(@RequestBody Pista pista) {
        return pistaService.crearPista(pista);
    }

    @PutMapping("/{id}")
    public Pista editarPista(@PathVariable Long id, @RequestBody Pista nova) {
        return pistaService.editarPista(nova, id);
    }

    @PatchMapping("/{id}/estat")
    public Pista estatPista(@PathVariable Long id, @RequestParam boolean activa) {
        return pistaService.estatPista(id, activa);
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
