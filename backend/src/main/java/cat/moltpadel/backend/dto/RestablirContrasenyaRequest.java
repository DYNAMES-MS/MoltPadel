package cat.moltpadel.backend.dto;

public record RestablirContrasenyaRequest(String email, String codi, String contrasenyaNova) {
}