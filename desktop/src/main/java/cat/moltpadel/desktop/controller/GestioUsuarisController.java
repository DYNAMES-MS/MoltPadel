package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
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
import javafx.scene.layout.VBox;

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
    private TableColumn<Usuari, String> estatColumna;

    @FXML
    private Button estatBotoId;

    @FXML
    private Button editarBotoId;

    @FXML
    private Label missatgeId;

    @FXML
    private TextField consultaId;

    @FXML
    private VBox gestioId;

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
        estatColumna.setCellValueFactory(new PropertyValueFactory<>("estatCompte"));
        estatBotoId.disableProperty().bind(taulaUsuarisId.getSelectionModel().selectedItemProperty().isNull());
        editarBotoId.disableProperty().bind(taulaUsuarisId.getSelectionModel().selectedItemProperty().isNull());
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
        gestioId.setDisable(true);
        missatgeId.setText("");

        usuariService.getUsuaris().whenComplete((usuaris, error) -> Platform.runLater(() -> {
            gestioId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar els usuaris"));
                return;
            }

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
    private void canviarEstatCompte() {
        Usuari usuari = taulaUsuarisId.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Estat de l'usuari");

        if (!usuari.isActiu()) {
            alert.setHeaderText("Vols reactivar aquest usuari?");
        } else {
            alert.setHeaderText("Vols donar de baixa aquest usuari?");
        }

        alert.setContentText(usuari.getNom() + " - " + usuari.getEmail());

        Optional<ButtonType> boto = alert.showAndWait();
        if (boto.isEmpty() || boto.get() != ButtonType.OK) {
            return;
        }

        gestioId.setDisable(true);
        missatgeId.setText("");

        usuariService.canviarEstatCompte(usuari.getId(), !usuari.isActiu()).whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                gestioId.setDisable(false);
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut canviar l'estat del compte"));
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
        botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && (contrasenya.getText().isBlank() || contrasenya.getText().length() < 10)));

        nom.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && (contrasenya.getText().isBlank() || contrasenya.getText().length() < 10)));
        });

        email.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && (contrasenya.getText().isBlank() || contrasenya.getText().length() < 10)));
        });

        contrasenya.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(nom.getText().isBlank() || email.getText().isBlank() || (usuari == null && (contrasenya.getText().isBlank() || contrasenya.getText().length() < 10)));
        });

        Optional<ButtonType> boto = dialog.showAndWait();
        if (boto.isEmpty() || boto.get() != ButtonType.OK) {
            return;
        }

        gestioId.setDisable(true);
        missatgeId.setText("");

        CompletableFuture<Void> future;
        if (usuari == null) {
            future = usuariService.crearUsuari(nom.getText().trim(), email.getText().trim(), contrasenya.getText());
        } else {
            future = usuariService.editarUsuari(usuari.getId(), nom.getText().trim(), email.getText().trim());
        }

        future.whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                gestioId.setDisable(false);
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut desar l'usuari"));
                return;
            }
            carregarUsuaris();
        }));
    }
}
