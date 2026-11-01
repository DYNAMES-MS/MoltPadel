package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.service.SessioService;
import cat.moltpadel.desktop.security.SessioActual;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Objects;
import javafx.event.ActionEvent;

public class SessioController {

    @FXML
    private TextField emailId;

    @FXML
    private PasswordField contrasenyaId;

    @FXML
    private Label errorId;

    @FXML
    private Button entraBotoId;

    private final SessioService sessioService = new SessioService();

    @FXML
    private void iniciarSessio() {
        String email = emailId.getText().trim();
        String contrasenya = contrasenyaId.getText();

        if (email.isBlank() || contrasenya.isBlank()) {
            errorId.setText("Correu i contrasenya són obligatoris");
            return;
        }

        entraBotoId.setDisable(true);

        sessioService.iniciarSessio(email, contrasenya).whenComplete((token, error) -> Platform.runLater(() -> {
            entraBotoId.setDisable(false);

            if (error != null) {
                errorId.setText("Correu o contrasenya incorrectes");
                return;
            }

            SessioActual.guardar(token);
            String rol = SessioActual.getRol();

            if (rol == null) {
                SessioActual.eliminar();
                return;
            }

            String rutaFxml;
            if (rol.equals("ADMIN")) {
                rutaFxml = "/admin.fxml";
            } else {
                rutaFxml = "/usuari.fxml";
            }

            try {
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(rutaFxml)));
                entraBotoId.getScene().setRoot(root);
            } catch (IOException e) {
                SessioActual.eliminar();
            }
        }));
    }

    @FXML
    private void tornarInici(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        Button boto = (Button) event.getSource();
        boto.getScene().setRoot(root);
    }
}