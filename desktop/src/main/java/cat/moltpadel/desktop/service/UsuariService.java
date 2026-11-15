package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Usuari;
import cat.moltpadel.desktop.security.SessioActual;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
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
            GestorErrorsApi.validar(response, 200);
            JsonNode json = mapper.readTree(response.body());
            List<Usuari> usuaris = new ArrayList<>();
            for (JsonNode usuariJson : json) {
                Long id = usuariJson.path("id").asLong();
                String nom = usuariJson.path("nom").asString();
                String email = usuariJson.path("email").asString();
                String rol = usuariJson.path("rol").asString();
                boolean soci = usuariJson.path("soci").asBoolean();
                boolean actiu = usuariJson.path("actiu").asBoolean();
                String tipusSoci = null;
                LocalDate iniciSoci = null;
                LocalDate fiSoci = null;
                if (usuariJson.hasNonNull("tipusSoci")) {
                    tipusSoci = usuariJson.path("tipusSoci").asString();
                }
                if (usuariJson.hasNonNull("iniciSoci")) {
                    iniciSoci = LocalDate.parse(usuariJson.path("iniciSoci").asString());
                }
                if (usuariJson.hasNonNull("fiSoci")) {
                    fiSoci = LocalDate.parse(usuariJson.path("fiSoci").asString());
                }

                usuaris.add(new Usuari(id, nom, email, rol, soci, actiu, tipusSoci, iniciSoci, fiSoci));
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
            GestorErrorsApi.validar(response, 201);
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
            GestorErrorsApi.validar(response, 204);
        });
    }

    public CompletableFuture<Void> canviarEstatCompte(Long id, boolean actiu) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id + "/estat?actiu=" + actiu))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            GestorErrorsApi.validar(response, 204);
        });
    }

    public CompletableFuture<Usuari> getSoci() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/soci"))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);
            JsonNode json = mapper.readTree(response.body());

            String tipusSoci = null;
            LocalDate iniciSoci = null;
            LocalDate fiSoci = null;

            Long id = json.path("id").asLong();
            String nom = json.path("nom").asString();
            String email = json.path("email").asString();
            String rol = json.path("rol").asString();
            boolean soci = json.path("soci").asBoolean();
            boolean actiu = json.path("actiu").asBoolean();
            if (json.hasNonNull("tipusSoci")) {
                tipusSoci = json.path("tipusSoci").asString();
            }
            if (json.hasNonNull("iniciSoci")) {
                iniciSoci = LocalDate.parse(json.path("iniciSoci").asString());
            }
            if (json.hasNonNull("fiSoci")) {
                fiSoci = LocalDate.parse(json.path("fiSoci").asString());
            }

            return new Usuari(id, nom, email, rol, soci, actiu, tipusSoci, iniciSoci, fiSoci);
        });
    }

    public CompletableFuture<Void> activarSoci(String tipusSoci) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/soci?tipusSoci=" + tipusSoci))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            GestorErrorsApi.validar(response, 204);
        });
    }
}