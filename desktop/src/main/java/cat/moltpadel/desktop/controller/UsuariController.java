package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Reserva;
import cat.moltpadel.desktop.security.SessioActual;
import cat.moltpadel.desktop.service.ReservaService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class UsuariController {

    @FXML
    private BorderPane borderId;

    @FXML
    private StackPane boxId;

    @FXML
    private Label missatgeId;

    private final ReservaService reservaService = new ReservaService();

    @FXML
    private void initialize() {
        carregarProximaReserva();
    }

    @FXML
    private void inici() {
        borderId.setCenter(boxId);
        carregarProximaReserva();
    }

    @FXML
    private void disponibilitat() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/disponibilitat.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void reservesUsuari() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/reserves_usuari.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void soci() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/soci.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void canviarContrasenya() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/canviar_contrasenya.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void tancarSessio(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        Button boto = (Button) event.getSource();
        SessioActual.eliminar();
        boto.getScene().setRoot(root);
    }

    private void carregarProximaReserva() {
        reservaService.getReservesPropies().whenComplete((reserves, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut carregar la pròxima reserva"));
                return;
            }

            for (int i = reserves.size() - 1; i >= 0; i--) {
                Reserva reserva = reserves.get(i);
                LocalDateTime inici = LocalDateTime.of(reserva.getData(), reserva.getHora());
                if ("ACTIVA".equals(reserva.getEstat()) && inici.isAfter(LocalDateTime.now(ZoneId.of("Europe/Madrid")))) {
                    missatgeId.setText(reserva.getNomPista() + "\n" + reserva.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + reserva.getHora());
                    return;
                }
            }

            missatgeId.setText("Encara no tens cap reserva pròxima");
        }));
    }
}
