package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.model.Pista;
import cat.moltpadel.desktop.security.SessioActual;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PistaService {

    private static final String URL = "http://localhost:8080/api/pistes";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<List<Pista>> getPistes() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error de servidor: " + response.statusCode());
            }

            JsonNode json = mapper.readTree(response.body());
            List<Pista> pistes = new ArrayList<>();
            for (JsonNode pistaJson : json) {
                Long id = pistaJson.path("id").asLong();
                String nom = pistaJson.path("nom").asString();
                String tipus = pistaJson.path("tipus").asString();
                boolean activa = pistaJson.path("activa").asBoolean();

                pistes.add(new Pista(id, nom, tipus, activa));
            }
            return pistes;
        });
    }
    public CompletableFuture<Void> crearPista(String nom, String tipus) {
        ObjectNode json = mapper.createObjectNode();
        json.put("nom", nom);
        json.put("tipus", tipus);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 201) {
                throw new RuntimeException("Error creant la pista: " + response.statusCode());
            }
        });
    }

    public CompletableFuture<Void> editarPista(Long id, String nom, String tipus) {
        ObjectNode json = mapper.createObjectNode();
        json.put("nom", nom);
        json.put("tipus", tipus);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 204) {
                throw new RuntimeException("Error editant la pista: " + response.statusCode());
            }
        });
    }

    public CompletableFuture<Void> canviarEstat(Long id, boolean activa) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id + "/estat?activa=" + activa))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 204) {
                throw new RuntimeException("Error canviant l'estat de la pista: " + response.statusCode());
            }
        });
    }
}
