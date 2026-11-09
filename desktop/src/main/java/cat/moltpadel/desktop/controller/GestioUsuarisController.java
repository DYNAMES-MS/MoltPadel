package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.model.Usuari;
import cat.moltpadel.desktop.service.UsuariService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class GestioUsuarisController {

    @FXML
    private TableView<Usuari> taulaUsuarisId;

    @FXML
    private TableColumn<Usuari, Long> idColumna;

    @FXML
    private TableColumn<Usuari, String> nomColumna;

    @FXML
    private TableColumn<Usuari, String> emailColumna;

    @FXML
    private TableColumn<Usuari, String> rolColumna;

    @FXML
    private TableColumn<Usuari, String> sociColumna;

    @FXML
    private Button editarBotoId;

    @FXML
    private Button sociBotoId;

    @FXML
    private Label missatgeId;

    @FXML
    private TextField consultaId;

    private final UsuariService usuariService = new UsuariService();
    private final ObservableList<Usuari> llistaUsuaris = FXCollections.observableArrayList();
    private final FilteredList<Usuari> usuarisFiltrats = new FilteredList<>(llistaUsuaris);

    @FXML
    private void initialize() {
        idColumna.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumna.setCellValueFactory(new PropertyValueFactory<>("nom"));
        emailColumna.setCellValueFactory(new PropertyValueFactory<>("email"));
        rolColumna.setCellValueFactory(new PropertyValueFactory<>("rol"));
        sociColumna.setCellValueFactory(new PropertyValueFactory<>("estatSoci"));
        editarBotoId.disableProperty().bind(taulaUsuarisId.getSelectionModel().selectedItemProperty().isNull());
        sociBotoId.disableProperty().bind(taulaUsuarisId.getSelectionModel().selectedItemProperty().isNull());
        taulaUsuarisId.setItems(usuarisFiltrats);
        consultaId.textProperty().addListener((obs, oldValue, newValue) -> {
            String consulta = newValue.trim().toLowerCase();
            usuarisFiltrats.setPredicate(usuari -> {
                if (consulta.isEmpty()) {
                    return true;
                }
                return usuari.getNom().toLowerCase().contains(consulta) || usuari.getEmail().toLowerCase().contains(consulta);
            });

        });
        carregarUsuaris();

    }

    private void carregarUsuaris() {
        usuariService.getUsuaris().whenComplete((usuaris, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'han pogut carregar els usuaris");
                return;
            }
            missatgeId.setText("");
            llistaUsuaris.setAll(usuaris);
        }));
    }

    @FXML
    private void crearUsuari() {
        formulari(null);
    }

    @FXML
    private void editarUsuari() {
        Usuari usuari = taulaUsuarisId.getSelectionModel().getSelectedItem();
        formulari(usuari);
    }

    @FXML
    private void editarSoci() {
        Usuari usuari = taulaUsuarisId.getSelectionModel().getSelectedItem();
        boolean estat = !usuari.isSoci();

        usuariService.editarSoci(usuari.getId(), estat).whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'ha pogut editar l'estat de soci");
                return;
            }
            carregarUsuaris();
        }));
    }

    private void formulari(Usuari usuari) {
        TextField nom = new TextField();
        TextField email = new TextField();
        PasswordField contrasenya = new PasswordField();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.addRow(0, new Label("Nom:"), nom);
        gridPane.addRow(1, new Label("Correu:"), email);

        Dialog<ButtonType> dialog = new Dialog<>();
        if (usuari == null) {
            dialog.setTitle("Nou usuari");
            gridPane.addRow(2, new Label("Contrasenya:"), contrasenya);
        } else {
            dialog.setTitle("Editar usuari");
            nom.setText(usuari.getNom());
            email.setText(usuari.getEmail());
        }

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button botoOK = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && contrasenya.getText().length() < 10)
        );

        nom.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && contrasenya.getText().length() < 10));
        });

        email.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && contrasenya.getText().length() < 10));
        });

        contrasenya.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && contrasenya.getText().length() < 10));
        });

        Optional<ButtonType> boto = dialog.showAndWait();
        if (boto.isEmpty() || boto.get() != ButtonType.OK) {
            return;
        }

        CompletableFuture<Void> future;
        if (usuari == null) {
            future = usuariService.crearUsuari(nom.getText().trim(), email.getText().trim(), contrasenya.getText());
        } else {
            future = usuariService.editarUsuari(usuari.getId(), nom.getText().trim(), email.getText().trim());
        }

        future.whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'ha pogut desar l'usuari");
                return;
            }
            carregarUsuaris();
        }));
    }
}
