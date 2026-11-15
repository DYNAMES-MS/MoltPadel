package cat.moltpadel.desktop.service;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.ConfirmarReserva;
import cat.moltpadel.desktop.model.Reserva;
import cat.moltpadel.desktop.security.SessioActual;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReservaService {

    private static final String URL = "http://localhost:8080/api/reserves";
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<Void> crearReserva(Long pistaId, LocalDate data, LocalTime hora) {
        ObjectNode json = mapper.createObjectNode();
        json.put("pistaId", pistaId);
        json.put("data", data.toString());
        json.put("hora", hora.toString());

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

    public CompletableFuture<Void> cancelarReserva(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/" + id + "/cancelar"))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenAccept(response -> {
            GestorErrorsApi.validar(response, 200);
        });
    }

    public CompletableFuture<ConfirmarReserva> getConfirmarReserva(Long pistaId, LocalDate data, LocalTime hora) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/confirmar?pistaId=" + pistaId + "&data=" + data + "&hora=" + hora))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

            JsonNode json = mapper.readTree(response.body());

            Long idPista = json.path("pistaId").asLong();
            String nomPista = json.path("nomPista").asString();
            String tipus = json.path("tipus").asString();
            LocalDate dataReserva = LocalDate.parse(json.path("data").asString());
            LocalTime horaReserva = LocalTime.parse(json.path("hora").asString());
            BigDecimal preu = new BigDecimal(json.path("preu").asString());

            return new ConfirmarReserva(idPista, nomPista, tipus, dataReserva, horaReserva, preu);
        });
    }

    public CompletableFuture<List<Reserva>> getReservesPropies() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/propies"))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

            JsonNode json = mapper.readTree(response.body());
            List<Reserva> reserves = new ArrayList<>();

            for (JsonNode node : json) {
                Long id = node.path("id").asLong();
                Long pistaId = node.path("pistaId").asLong();
                String nomPista = node.path("nomPista").asString();
                String nomUsuari = node.path("nomUsuari").asString();
                LocalDate data = LocalDate.parse(node.path("data").asString());
                LocalTime hora = LocalTime.parse(node.path("hora").asString());
                String estat = node.path("estat").asString();
                BigDecimal preu = new BigDecimal(node.path("preu").asString());
                String tarifa = node.path("tarifa").asString();

                reserves.add(new Reserva(id, pistaId, nomPista, nomUsuari, data, hora, estat, preu, tarifa));
            }

            return reserves;
        });
    }

    public CompletableFuture<List<Reserva>> getReservesPerData(LocalDate data) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + "?data=" + data))
                .header("Authorization", "Bearer " + SessioActual.getToken())
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            GestorErrorsApi.validar(response, 200);

            JsonNode json = mapper.readTree(response.body());
            List<Reserva> reserves = new ArrayList<>();

            for (JsonNode node : json) {
                Long id = node.path("id").asLong();
                Long pistaId = node.path("pistaId").asLong();
                String nomPista = node.path("nomPista").asString();
                String nomUsuari = node.path("nomUsuari").asString();
                LocalDate dataReserva = LocalDate.parse(node.path("data").asString());
                LocalTime hora = LocalTime.parse(node.path("hora").asString());
                String estat = node.path("estat").asString();
                BigDecimal preu = new BigDecimal(node.path("preu").asString());
                String tarifa = node.path("tarifa").asString();

                reserves.add(new Reserva(id, pistaId, nomPista, nomUsuari, dataReserva, hora, estat, preu, tarifa));
            }

            return reserves;
        });
    }
}