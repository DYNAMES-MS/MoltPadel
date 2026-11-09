package cat.moltpadel.backend.controller;

import cat.moltpadel.backend.dto.SessioRequest;
import cat.moltpadel.backend.dto.SessioResponse;
import cat.moltpadel.backend.security.TokenService;
import cat.moltpadel.backend.service.SessioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class SessioController {

    private final SessioService sessioService;
    private final TokenService tokenService;

    public SessioController(SessioService sessioService, TokenService tokenService) {
        this.sessioService = sessioService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public SessioResponse iniciarSessio(@RequestBody SessioRequest request) {
        Authentication authLogin = sessioService.auth(request.email(), request.contrasenya());
        String token = tokenService.generarToken(authLogin);

        return new SessioResponse(token);
    }
}