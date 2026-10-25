package cat.moltpadel.backend.security;

public record RegistrarUsuariRequest(
        String nom,
        String email,
        String contrasenya
) {
}