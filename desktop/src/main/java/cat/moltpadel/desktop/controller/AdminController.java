package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Pista;
import cat.moltpadel.desktop.service.PistaService;
import cat.moltpadel.desktop.security.SessioActual;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Objects;
import cat.moltpadel.desktop.model.Usuari;
import cat.moltpadel.desktop.model.Reserva;
import cat.moltpadel.desktop.service.UsuariService;
import cat.moltpadel.desktop.service.ReservaService;
import java.time.LocalDate;
import java.time.ZoneId;

public class AdminController {

    @FXML
    private BorderPane borderId;

    @FXML
    private VBox boxId;

    @FXML
    private Label pistesId;

    @FXML
    private Label jugadorsId;

    @FXML
    private Label socisId;

    @FXML
    private Label reservesId;

    @FXML
    private Label missatgeId;

    private final UsuariService usuariService = new UsuariService();
    private final ReservaService reservaService = new ReservaService();
    private final PistaService pistaService = new PistaService();

    @FXML
    private void initialize() {
        carregarResum();
    }

    @FXML
    private void inici() {
        borderId.setCenter(boxId);
        carregarResum();
    }

    private void carregarResum() {
        missatgeId.setText("");
        carregarUsuaris();
        carregarPistes();
        carregarReserves();
    }

    @FXML
    private void pistes() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gestio_pistes.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void usuaris() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gestio_usuaris.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void reserves() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/gestio_reserves.fxml")));
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

    private void carregarPistes() {
        pistaService.getPistes().whenComplete((pistes, error) -> Platform.runLater(() -> {
            if (error != null) {
                pistesId.setText("-");
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les pistes"));
                return;
            }

            int actives = 0;
            for (Pista pista : pistes) {
                if (pista.isActiva()) {
                    actives++;
                }
            }

            pistesId.setText(actives + " de " + pistes.size());
        }));
    }

    private void carregarUsuaris() {
        usuariService.getUsuaris().whenComplete((usuaris, error) -> Platform.runLater(() -> {
            if (error != null) {
                jugadorsId.setText("-");
                socisId.setText("-");
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar els usuaris"));
                return;
            }

            int jugadors = 0;
            int socis = 0;
            for (Usuari usuari : usuaris) {
                if ("ADMIN".equals(usuari.getRol())) {
                    continue;
                }
                jugadors++;

                if (usuari.isActiu() && usuari.isSoci()) {
                    socis++;
                }
            }

            jugadorsId.setText(String.valueOf(jugadors));
            socisId.setText(String.valueOf(socis));
        }));
    }

    private void carregarReserves() {
        reservaService.getReservesPerData(LocalDate.now(ZoneId.of("Europe/Madrid"))).whenComplete((reserves, error) -> Platform.runLater(() -> {
            if (error != null) {
                reservesId.setText("-");
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les reserves d'avui"));
                return;
            }

            int actives = 0;
            for (Reserva reserva : reserves) {
                if ("ACTIVA".equals(reserva.getEstat())) {
                    actives++;
                }
            }

            reservesId.setText(String.valueOf(actives));
        }));
    }
}