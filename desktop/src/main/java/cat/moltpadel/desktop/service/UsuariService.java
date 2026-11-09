package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.model.Usuari;
import cat.moltpadel.desktop.security.SessioActual;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import tools.jackson.databind.node.ObjectNode;

public class UsuariService {

    private static final String URL = "http://localhost:8080/api/usuaris";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<List<Usuari>> getUsuaris() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).header("Authorization", "Bearer " + SessioActual.getToken()).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() != 200) {
                throw new RuntimeException("Error del servidor: " + response.statusCode());
            }

            JsonNode json = mapper.readTree(response.body());
            List<Usuari> usuaris = new ArrayList<>();
            for (JsonNode usuariJson : json) {
                Long id = usuariJson.path("id").asLong();
                String nom = usuariJson.path("nom").asString();
                String email = usuariJson.path("email").asString();
                String rol = usuariJson.path("rol").asString();
                boolean soci = usuariJson.path("soci").asBoolean();

                usuaris.add(new Usuari(id, nom, email, rol, soci));
            }
            return usuaris;
        });
    }

    public CompletableFuture<Void> crearUsuari(String nom, String email, String contrasenya) {
        ObjectNode json = mapper.createObjectNode();
        json.put("nom", nom);
        json.put("email", email);
        json.put("contrasenya", contrasenya);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/registre"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 201) {
                throw new RuntimeException("Error creant l'usuari: " + response.statusCode());
            }
        });
    }

    public CompletableFuture<Void> editarUsuari(Long id, String nom, String email) {
        ObjectNode json = mapper.createObjectNode();
        json.put("nom", nom);
        json.put("email", email);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 204) {
                throw new RuntimeException("Error editant l'usuari: " + response.statusCode());
            }
        });
    }

    public CompletableFuture<Void> editarSoci(Long id, boolean soci) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id + "/soci?soci=" + soci))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            if (response.statusCode() != 204) {
                throw new RuntimeException("Error canviant l'estat de soci: " + response.statusCode());
            }
        });
    }

}