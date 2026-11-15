package cat.moltpadel.desktop.exception;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletionException;

public class GestorErrorsApi {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void validar(HttpResponse<String> resposta, int codiCorrecte) {
        int codi = resposta.statusCode();

        if (codi == codiCorrecte) {
            return;
        }

        if (codi == 403) {
            throw new ApiException("No tens permisos per fer aquesta operació");
        }

        if (codi == 400 || codi == 401 || codi == 404) {
            JsonNode json;

            try {
                json = mapper.readTree(resposta.body());
            } catch (RuntimeException e) {
                json = null;
            }

            if (json != null) {
                String missatge = json.path("missatge").asString("");

                if (!missatge.isBlank()) {
                    throw new ApiException(missatge);
                }
            }
        }

        if (codi == 401) {
            throw new ApiException("La sessió no és vàlida. Tanca la sessió i torna a iniciar-la.");
        }

        throw new RuntimeException();
    }

    public static String getMissatge(Throwable error, String missatgeGeneric) {
        Throwable causa = error;

        while (causa instanceof CompletionException && causa.getCause() != null) {
            causa = causa.getCause();
        }

        if (causa instanceof ApiException) {
            return causa.getMessage();
        }

        return missatgeGeneric;
    }
}