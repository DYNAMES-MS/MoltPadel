package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.model.Pista;
import cat.moltpadel.desktop.service.PistaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class GestioPistesController {

    @FXML
    private TableView<Pista> taulaPistesId;

    @FXML
    private TableColumn<Pista, Long> idColumna;

    @FXML
    private TableColumn<Pista, String> nomColumna;

    @FXML
    private TableColumn<Pista, String> tipusColumna;

    @FXML
    private TableColumn<Pista, String> estatColumna;

    @FXML
    private Button editarBotoId;

    @FXML
    private Button estatBotoId;

    @FXML
    private Label missatgeId;

    private final PistaService pistaService = new PistaService();

    @FXML
    private void initialize() {
        idColumna.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumna.setCellValueFactory(new PropertyValueFactory<>("nom"));
        tipusColumna.setCellValueFactory(new PropertyValueFactory<>("tipus"));
        estatColumna.setCellValueFactory(new PropertyValueFactory<>("estat"));
        editarBotoId.disableProperty().bind(taulaPistesId.getSelectionModel().selectedItemProperty().isNull());
        estatBotoId.disableProperty().bind(taulaPistesId.getSelectionModel().selectedItemProperty().isNull());
        carregarPistes();
    }

    private void carregarPistes() {
        pistaService.getPistes().whenComplete((pistes, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'han pogut carregar les pistes");
                return;
            }
            missatgeId.setText("");
            taulaPistesId.getItems().setAll(pistes);
        }));
    }

    @FXML
    private void crearPista() {
        formulari(null);
    }

    @FXML
    private void editarPista() {
        Pista pista = taulaPistesId.getSelectionModel().getSelectedItem();
        formulari(pista);
    }

    @FXML
    private void canviarEstat() {
        Pista pista = taulaPistesId.getSelectionModel().getSelectedItem();
        boolean estat = !pista.isActiva();

        pistaService.canviarEstat(pista.getId(), estat).whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'ha pogut canviar l'estat de la pista");
                return;
            }
            carregarPistes();
        }));
    }

    private void formulari(Pista pista) {
        TextField nom = new TextField();
        ComboBox<String> tipus = new ComboBox<>();
        tipus.getItems().addAll("INTERIOR", "EXTERIOR");
        tipus.setValue("EXTERIOR");

        Dialog<ButtonType> dialog = new Dialog<>();
        if (pista == null) {
            dialog.setTitle("Nova pista");
        } else {
            dialog.setTitle("Editar pista");
            nom.setText(pista.getNom());
            tipus.setValue(pista.getTipus());
        }

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.addRow(0, new Label("Nom:"), nom);
        gridPane.addRow(1, new Label("Tipus:"), tipus);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button botoOK = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        botoOK.setDisable(nom.getText().isBlank());
        nom.textProperty().addListener((obs, oldValue, newValue) -> {
            botoOK.setDisable(newValue.isBlank());
        });

        Optional<ButtonType> boto = dialog.showAndWait();
        if (boto.isEmpty() || boto.get() != ButtonType.OK) {
            return;
        }

        CompletableFuture<Void> future;
        if (pista == null) {
            future = pistaService.crearPista(nom.getText().trim(), tipus.getValue());
        } else {
            future = pistaService.editarPista(pista.getId(), nom.getText().trim(), tipus.getValue());
        }

        future.whenComplete((v, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText("Error: No s'ha pogut desar la pista");
                return;
            }
            carregarPistes();
        }));
    }
}