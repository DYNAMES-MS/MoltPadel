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


public class UsuariController {

    @FXML
    private BorderPane borderId;

    @FXML
    private VBox boxId;

    @FXML
    private void inici() {
        borderId.setCenter(boxId);
    }

    @FXML
    private void disponibilitat() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/disponibilitat.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void reservesUsuari() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/reserves_usuari.fxml")));
        borderId.setCenter(root);
    }

    @FXML
    private void soci() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/soci.fxml")));
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
