package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
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
import cat.moltpadel.desktop.model.Disponibilitat;
import java.time.LocalDate;
import java.time.LocalTime;

public class PistaService {

    private static final String URL = "https://moltpadel-api-gucya9dzcva9fjfx.spaincentral-01.azurewebsites.net/api/pistes";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<List<Pista>> getPistes() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL)).header("Authorization", "Bearer " + SessioActual.getToken()).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

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
            GestorErrorsApi.validar(response, 201);
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
            GestorErrorsApi.validar(response, 204);
        });
    }

    public CompletableFuture<Void> canviarEstat(Long id, boolean activa) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id + "/estat?activa=" + activa))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            GestorErrorsApi.validar(response, 204);
        });
    }

    public CompletableFuture<List<Disponibilitat>> getDisponibilitat(LocalDate data) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/disponibilitat?data=" + data)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

            JsonNode json = mapper.readTree(response.body());
            List<Disponibilitat> disponibilitat = new ArrayList<>();
            for (JsonNode franjaJson : json) {
                LocalDate dataFranja = LocalDate.parse(franjaJson.path("data").asString());
                LocalTime hora = LocalTime.parse(franjaJson.path("hora").asString());
                int disponibles = franjaJson.path("disponibles").asInt();
                int totalPistes = franjaJson.path("totalPistes").asInt();
                boolean caducada = franjaJson.path("caducada").asBoolean();

                disponibilitat.add(new Disponibilitat(dataFranja, hora, disponibles, totalPistes, caducada));
            }

            return disponibilitat;
        });
    }
    public CompletableFuture<List<Pista>> getPistesDisponibles(LocalDate data, LocalTime hora) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL + "/disponibilitat/franja?data=" + data + "&hora=" + hora)).GET().build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

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
}
