package cat.moltpadel.desktop.security;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.Base64;

public class SessioActual {
    private static String token;

    public static void guardar(String nouToken) {
        token = nouToken;
    }

    public static void eliminar() {
        token = null;
    }

    public static String getRol() {
        if (token == null) {
            return null;
        }

        try {
            String dividit = token.split("\\.")[1];
            byte[] json = Base64.getUrlDecoder().decode(dividit);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode roles = mapper.readTree(json).path("roles");

            for (JsonNode rol : roles) {
                if (rol.asString().equals("ROLE_ADMIN")) {
                    return "ADMIN";
                }
                if (rol.asString().equals("ROLE_USER")) {
                    return "USER";
                }
            }

        } catch (Exception e) {
            return null;
        }

        return null;
    }
}