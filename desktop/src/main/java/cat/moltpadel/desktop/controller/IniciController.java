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
    private void consultarPistes() throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/disponibilitat.fxml")));
        Parent root = loader.load();
        DisponibilitatController controller = loader.getController();
        controller.setConsultaPublica(true);
        sessioBotoId.getScene().setRoot(root);
    }

    @FXML
    private void obrirSessio() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/sessio.fxml")));
        sessioBotoId.getScene().setRoot(root);
    }
}
