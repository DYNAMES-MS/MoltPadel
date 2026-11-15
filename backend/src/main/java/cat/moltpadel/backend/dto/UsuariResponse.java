package cat.moltpadel.backend.dto;

import cat.moltpadel.backend.model.Usuari;
import java.time.LocalDate;

public record UsuariResponse(Long id, String nom, String email, boolean soci, Usuari.Rol rol, boolean actiu, Usuari.TipusSoci tipusSoci, LocalDate iniciSoci, LocalDate fiSoci) {
}
