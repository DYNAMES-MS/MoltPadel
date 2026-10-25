package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.service.PistaService;
import org.springframework.web.bind.annotation.*;
import cat.moltpadel.backend.model.Pista;

import java.util.List;

@RestController
@RequestMapping("/api/pistes")
public class PistaController {
    private final PistaService pistaService;

    public PistaController(PistaService pistaService) {
        this.pistaService = pistaService;
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
}
