package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.service.UsuariService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class RegistreController {

    @FXML
    private VBox formulariId;

    @FXML
    private TextField nomId;

    @FXML
    private TextField emailId;

    @FXML
    private PasswordField contrasenyaId;

    @FXML
    private Button registreBotoId;

    @FXML
    private Button sessioBotoId;

    @FXML
    private Label missatgeId;

    private final UsuariService usuariService = new UsuariService();

    @FXML
    private void initialize() {
        Platform.runLater(() -> formulariId.requestFocus());
    }

    @FXML
    private void registrarUsuari() {
        String nom = nomId.getText().trim();
        String email = emailId.getText().trim();
        String contrasenya = contrasenyaId.getText();

        if (nom.isBlank() || email.isBlank() || contrasenya.isBlank()) {
            missatgeId.setText("Empleneu correctament tots els camps");
            return;
        }

        missatgeId.setText("");
        formulariId.setDisable(true);

        usuariService.crearUsuari(nom, email, contrasenya).whenComplete((v, error) -> Platform.runLater(() -> {
            formulariId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut crear el compte"));
                return;
            }

            contrasenyaId.clear();
            nomId.setDisable(true);
            emailId.setDisable(true);
            contrasenyaId.setDisable(true);
            registreBotoId.setVisible(false);
            registreBotoId.setManaged(false);

            missatgeId.setText("Compte creat correctament");
            sessioBotoId.setVisible(true);
            sessioBotoId.setManaged(true);
        }));
    }

    @FXML
    private void obrirSessio() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sessio.fxml")));
        formulariId.getScene().setRoot(root);
    }

    @FXML
    private void tornarInici() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        formulariId.getScene().setRoot(root);
    }
}