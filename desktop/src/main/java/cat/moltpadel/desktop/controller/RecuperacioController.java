package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.service.UsuariService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class RecuperacioController {

    @FXML
    private VBox formulariId;

    @FXML
    private VBox peticioId;

    @FXML
    private VBox restablimentId;

    @FXML
    private TextField emailId;

    @FXML
    private TextField codiId;

    @FXML
    private PasswordField novaId;

    @FXML
    private PasswordField confirmacioId;

    @FXML
    private Label missatgeId;

    private final UsuariService usuariService = new UsuariService();

    @FXML
    private void demanarCodi() {
        String email = emailId.getText().trim();

        if (email.isBlank()) {
            missatgeId.setText("Introdueix el correu electrònic");
            return;
        }

        missatgeId.setText("");
        formulariId.setDisable(true);

        usuariService.demanarCodi(email).whenComplete((v, error) -> Platform.runLater(() -> {
            formulariId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error,"Error: No s'ha pogut demanar el codi de recuperació"));
                return;
            }

            peticioId.setVisible(false);
            peticioId.setManaged(false);
            restablimentId.setVisible(true);
            restablimentId.setManaged(true);

            missatgeId.setText("Si hi ha un compte actiu amb aquest correu, rebràs un codi per restablir la contrasenya");

            codiId.requestFocus();
        }));
    }

    @FXML
    private void restablirContrasenya() {
        String email = emailId.getText().trim();
        String codi = codiId.getText().trim();
        String nova = novaId.getText();
        String confirmacio = confirmacioId.getText();

        if (codi.isBlank() || nova.isBlank() || confirmacio.isBlank()) {
            missatgeId.setText("Omple tots els camps");
            return;
        }

        if (!nova.equals(confirmacio)) {
            missatgeId.setText("Les contrasenyes noves no coincideixen");
            return;
        }

        missatgeId.setText("");
        formulariId.setDisable(true);

        usuariService.restablirContrasenya(email, codi, nova).whenComplete((v, error) -> Platform.runLater(() -> {
            formulariId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error,"Error: No s'ha pogut restablir la contrasenya"));
                return;
            }

            codiId.clear();
            novaId.clear();
            confirmacioId.clear();
            restablimentId.setVisible(false);
            restablimentId.setManaged(false);
            missatgeId.setText("Contrasenya restablerta correctament");
        }));
    }

    @FXML
    private void tornarSessio() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sessio.fxml")));
        formulariId.getScene().setRoot(root);
    }
}