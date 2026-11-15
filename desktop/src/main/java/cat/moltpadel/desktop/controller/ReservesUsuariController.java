package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Reserva;
import cat.moltpadel.desktop.service.ReservaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class ReservesUsuariController {

    @FXML
    private TilePane reservesId;

    @FXML
    private Label missatgeId;

    private final ReservaService reservaService = new ReservaService();

    @FXML
    private void initialize() {
        carregarReserves();
    }

    private void carregarReserves() {
        reservesId.setDisable(true);

        reservaService.getReservesPropies().whenComplete((reserves, error) -> Platform.runLater(() -> {
            reservesId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les reserves"));
                return;
            }

            reservesId.getChildren().clear();
            missatgeId.setText("");

            if (reserves.isEmpty()) {
                missatgeId.setText("No tens reserves");
                return;
            }

            for (Reserva reserva : reserves) {
                reservesId.getChildren().add(crearTargeta(reserva));
            }
        }));
    }

    private VBox crearTargeta(Reserva reserva) {
        Label pista = new Label(reserva.getNomPista());
        Label data = new Label(reserva.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        Label hora = new Label(reserva.getHora() + " - " + reserva.getHora().plusHours(1));
        Label preu = new Label(reserva.getPreu().setScale(2, RoundingMode.HALF_UP).toPlainString().replace('.', ',') + " €");
        Label estat = new Label();

        LocalDateTime iniciReserva = LocalDateTime.of(reserva.getData(), reserva.getHora());
        boolean futura = iniciReserva.isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")));
        boolean activa = "ACTIVA".equals(reserva.getEstat());

        if (activa) {
            estat.setText("Activa");
        } else {
            estat.setText("Cancel·lada");
        }

        VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(20));
        vBox.getChildren().addAll(pista, data, hora, preu, estat);
        vBox.setPrefWidth(250);
        vBox.setMaxWidth(250);

        if (activa && futura) {
            Button cancelar = new Button("Cancel·lar reserva");
            cancelar.setOnAction(event -> cancelarReserva(reserva));
            vBox.getChildren().add(cancelar);
        }

        return vBox;
    }

    private void cancelarReserva(Reserva reserva) {
        reservesId.setDisable(true);

        reservaService.cancelarReserva(reserva.getId()).whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                reservesId.setDisable(false);
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut cancel·lar la reserva"));
                return;
            }

            carregarReserves();
        }));
    }
}
