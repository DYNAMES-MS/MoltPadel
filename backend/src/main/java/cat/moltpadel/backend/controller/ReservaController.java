package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.dto.ConfirmarReservaResponse;
import cat.moltpadel.backend.dto.ReservaRequest;
import cat.moltpadel.backend.dto.ReservaResponse;
import cat.moltpadel.backend.service.ReservaService;
import org.jspecify.annotations.NullMarked;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        ReservaResponse resposta = reservaService.crearReserva(request.pistaId(), request.data(), request.hora(), usuariId);

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelarReserva(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        ReservaResponse resposta = reservaService.cancelarReserva(id, usuariId);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/propies")
    public ResponseEntity<List<ReservaResponse>> getReservesPropies(@AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        List<ReservaResponse> resposta = reservaService.getReservesPropies(usuariId);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/confirmar")
    public ResponseEntity<ConfirmarReservaResponse> getConfirmarReserva(@RequestParam Long pistaId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora, @AuthenticationPrincipal Jwt jwt) {
        Long usuariId = Long.valueOf(Objects.requireNonNull(jwt.getSubject()));
        ConfirmarReservaResponse resposta = reservaService.getConfirmarReserva(pistaId, data, hora, usuariId);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> getReservesPerData(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<ReservaResponse> resposta = reservaService.getReservesPerData(data);

        return ResponseEntity.ok(resposta);
    }
}