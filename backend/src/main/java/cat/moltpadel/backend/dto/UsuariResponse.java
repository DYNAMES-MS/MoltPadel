package cat.moltpadel.backend.dto;

import cat.moltpadel.backend.model.Usuari;

public record UsuariResponse(Long id, String nom, String email, boolean soci, Usuari.Rol rol) {
}
