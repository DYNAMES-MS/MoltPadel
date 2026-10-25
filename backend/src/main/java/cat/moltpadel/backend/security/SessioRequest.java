package cat.moltpadel.backend.security;

public record SessioRequest(
        String email,
        String contrasenya
) {
}