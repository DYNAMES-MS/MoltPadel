package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.service.UsuariService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class SociController {

    @FXML
    private Label estatId;

    @FXML
    private Label tipusId;

    @FXML
    private Label iniciId;

    @FXML
    private Label fiId;

    @FXML
    private Button mensualBotoId;

    @FXML
    private Button anualBotoId;

    @FXML
    private Label missatgeId;

    private final UsuariService usuariService = new UsuariService();

    @FXML
    private void initialize() {
        carregarSoci();
    }

    private void carregarSoci() {
        mensualBotoId.setDisable(true);
        anualBotoId.setDisable(true);

        usuariService.getSoci().whenComplete((usuari, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les dades de soci"));
                return;
            }

            tipusId.setText("");
            iniciId.setText("");
            fiId.setText("");

            if (usuari.isSoci()) {
                estatId.setText("Quota de soci activa");
            } else if (usuari.getFiSoci() != null) {
                estatId.setText("Quota de soci ha caducat");
            } else {
                estatId.setText("");
            }

            if (usuari.getTipusSoci() != null) {
                if ("MENSUAL".equals(usuari.getTipusSoci())) {
                    tipusId.setText("Quota: Mensual");
                } else {
                    tipusId.setText("Quota: Anual");
                }
            }

            if (usuari.getIniciSoci() != null) {
                iniciId.setText("Inici: " + usuari.getIniciSoci().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            if (usuari.getFiSoci() != null) {
                fiId.setText("Fi : " + usuari.getFiSoci().minusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }

            mensualBotoId.setDisable(usuari.isSoci());
            anualBotoId.setDisable(usuari.isSoci());
            missatgeId.setText("");
        }));
    }


    @FXML
    private void activarMensual() {
        activarSoci("MENSUAL");
    }

    @FXML
    private void activarAnual() {
        activarSoci("ANUAL");
    }

    private void activarSoci(String tipusSoci) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmació de pagament");
        if ("MENSUAL".equals(tipusSoci)) {
            alert.setHeaderText("Processar el pagament de 99€?");
        } else {
            alert.setHeaderText("Processar el pagament de 1.100€?");
        }

        Optional<ButtonType> boto = alert.showAndWait();
        if (boto.isEmpty() || boto.get() != ButtonType.OK) {
            return;
        }

        mensualBotoId.setDisable(true);
        anualBotoId.setDisable(true);
        missatgeId.setText("");

        usuariService.activarSoci(tipusSoci).whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                mensualBotoId.setDisable(false);
                anualBotoId.setDisable(false);
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut confirmar l'activació"));
                return;
            }

            carregarSoci();
        }));
    }
}