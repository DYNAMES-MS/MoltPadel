package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Reserva;
import cat.moltpadel.desktop.service.ReservaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class GestioReservesController {

    @FXML
    private DatePicker dataId;

    @FXML
    private TableView<Reserva> taulaReservesId;

    @FXML
    private TableColumn<Reserva, LocalTime> horaColumna;

    @FXML
    private TableColumn<Reserva, String> pistaColumna;

    @FXML
    private TableColumn<Reserva, String> usuariColumna;

    @FXML
    private TableColumn<Reserva, BigDecimal> preuColumna;

    @FXML
    private TableColumn<Reserva, String> estatColumna;

    @FXML
    private TableColumn<Reserva, String> tarifaColumna;

    @FXML
    private Label missatgeId;

    private final ReservaService reservaService = new ReservaService();

    @FXML
    private void initialize() {
        horaColumna.setCellValueFactory(new PropertyValueFactory<>("hora"));
        pistaColumna.setCellValueFactory(new PropertyValueFactory<>("nomPista"));
        usuariColumna.setCellValueFactory(new PropertyValueFactory<>("nomUsuari"));
        preuColumna.setCellValueFactory(new PropertyValueFactory<>("preu"));
        estatColumna.setCellValueFactory(new PropertyValueFactory<>("estat"));
        tarifaColumna.setCellValueFactory(new PropertyValueFactory<>("tarifa"));
        dataId.setValue(LocalDate.now(ZoneId.of("Europe/Madrid")));
        dataId.setOnAction(event -> carregarReserves());
        carregarReserves();
    }

    @FXML
    private void carregarReserves() {
        if (dataId.getValue() == null) {
            taulaReservesId.getItems().clear();
            missatgeId.setText("Selecciona una data");
            return;
        }

        dataId.setDisable(true);
        taulaReservesId.getItems().clear();

        reservaService.getReservesPerData(dataId.getValue()).whenComplete((reserves, error) -> Platform.runLater(() -> {
            dataId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les reserves"));
                return;
            }

            missatgeId.setText("");
            taulaReservesId.getItems().setAll(reserves);
        }));
    }
}