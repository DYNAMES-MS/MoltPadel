package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class SessioService {

    private static final String URL = "http://localhost:8080/api/auth/login";

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<String> iniciarSessio(String email, String contrasenya) {
        ObjectNode json = mapper.createObjectNode();
        json.put("email", email);
        json.put("contrasenya", contrasenya);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

            String token = mapper.readTree(response.body()).path("token").asString("");
            if (token.isBlank()) {
                throw new RuntimeException("Token no existeix");
            }
            return token;
        });
    }
}