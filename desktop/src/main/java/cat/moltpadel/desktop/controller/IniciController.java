package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.security.SessioActual;
import javafx.event.ActionEvent;
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
    @FXML
    private void tancarSessio(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        Button boto = (Button) event.getSource();
        SessioActual.eliminar();
        boto.getScene().setRoot(root);
    }
}
