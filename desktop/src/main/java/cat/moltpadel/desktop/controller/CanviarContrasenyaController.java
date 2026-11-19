package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.service.UsuariService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class CanviarContrasenyaController {

    @FXML
    private VBox formulariId;

    @FXML
    private PasswordField actualId;

    @FXML
    private PasswordField novaId;

    @FXML
    private PasswordField confirmacioId;

    @FXML
    private Label missatgeId;

    private final UsuariService usuariService = new UsuariService();

    @FXML
    private void canviarContrasenya() {
        if (actualId.getText().isBlank() || novaId.getText().isBlank() || confirmacioId.getText().isBlank()) {
            missatgeId.setText("Empleneu correctament tots els camps");
            return;
        }
        if (!novaId.getText().equals(confirmacioId.getText())) {
            missatgeId.setText("Les contrasenyes noves no coincideixen");
            return;
        }

        missatgeId.setText("");
        formulariId.setDisable(true);

        usuariService.canviarContrasenya(actualId.getText(), novaId.getText()).whenComplete((v, error) -> Platform.runLater(() -> {
            formulariId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut canviar la contrasenya"));
                return;
            }

            actualId.clear();
            novaId.clear();
            confirmacioId.clear();
            missatgeId.setText("Contrasenya canviada correctament");
        }));
    }
}