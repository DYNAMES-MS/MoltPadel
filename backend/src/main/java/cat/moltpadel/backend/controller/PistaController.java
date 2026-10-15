package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.model.Pista;
import cat.moltpadel.backend.service.PistaService;
import org.springframework.web.bind.annotation.*;

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
}
