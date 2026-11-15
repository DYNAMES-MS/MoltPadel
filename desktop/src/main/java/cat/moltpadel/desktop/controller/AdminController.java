package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.security.SessioActual;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Objects;

public class AdminController {

    @FXML
    private BorderPane borderId;

    @FXML
    private VBox boxId;

    @FXML
    private void inici() {
        borderId.setCenter(boxId);
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
    private void tancarSessio(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        Button boto = (Button) event.getSource();
        SessioActual.eliminar();
        boto.getScene().setRoot(root);
    }
}