package cat.moltpadel.desktop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.Objects;

public class IniciController {

    @FXML
    private Button sessioBotoId;

    @FXML
    private void obrirSessio() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sessio.fxml")));
        sessioBotoId.getScene().setRoot(root);
    }
}
