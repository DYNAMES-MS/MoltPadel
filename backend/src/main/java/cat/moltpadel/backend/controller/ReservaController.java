package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.security.ReservaRequest;
import cat.moltpadel.backend.security.ReservaResponse;
import cat.moltpadel.backend.model.Reserva;
import cat.moltpadel.backend.service.ReservaService;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@NullMarked
@RestController
@RequestMapping("/api/reserves")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(@RequestBody ReservaRequest request, @AuthenticationPrincipal Jwt jwt) {
        Reserva reserva = reservaService.crearReserva(request.pistaId(), request.data(), request.hora(), jwt.getSubject());
        ReservaResponse resposta = new ReservaResponse(reserva.getId(), reserva.getPista().getId(), reserva.getData(), reserva.getHora(), reserva.getEstat());

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Reserva reserva = reservaService.cancelarReserva(id, jwt.getSubject());
        ReservaResponse resposta = new ReservaResponse(reserva.getId(), reserva.getPista().getId(), reserva.getData(), reserva.getHora(), reserva.getEstat());

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/propies")
    public ResponseEntity<List<ReservaResponse>> getReservesPropies(@AuthenticationPrincipal Jwt jwt) {
        List<Reserva> reserves = reservaService.getReservesPropies(jwt.getSubject());
        List<ReservaResponse> resposta = reserves.stream()
                .map(reserva -> new ReservaResponse(
                        reserva.getId(),
                        reserva.getPista().getId(),
                        reserva.getData(),
                        reserva.getHora(),
                        reserva.getEstat()
                )).toList();

        return ResponseEntity.ok(resposta);
    }
}