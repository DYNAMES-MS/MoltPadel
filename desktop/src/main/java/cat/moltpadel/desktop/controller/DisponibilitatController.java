package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.Disponibilitat;
import cat.moltpadel.desktop.service.PistaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;

public class DisponibilitatController {

    @FXML
    private Label setmanaId;

    @FXML
    private Label missatgeId;

    @FXML
    private GridPane calendariId;

    @FXML
    private VBox disponibilitatId;

    @FXML
    private Button setmanaAnteriorId;

    @FXML
    private Button setmanaSeguentId;

    @FXML
    private HBox consultaPublicaId;

    private final PistaService pistaService = new PistaService();
    private LocalDate dillunsSeleccionat = LocalDate.now(ZoneId.of("Europe/Madrid")).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    private boolean consultaPublica = false;

    @FXML
    private void initialize() {
        carregarDisponibilitat();
    }

    @FXML
    private void setmanaAnterior() {
        dillunsSeleccionat = dillunsSeleccionat.minusWeeks(1);
        carregarDisponibilitat();
    }

    @FXML
    private void setmanaSeguent() {
        dillunsSeleccionat = dillunsSeleccionat.plusWeeks(1);
        carregarDisponibilitat();
    }

    private void carregarDisponibilitat() {
        setmanaAnteriorId.setDisable(true);
        setmanaSeguentId.setDisable(true);
        setmanaId.setText(dillunsSeleccionat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " - " + dillunsSeleccionat.plusDays(6).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        calendariId.getChildren().clear();
        missatgeId.setText("");

        pistaService.getDisponibilitat(dillunsSeleccionat).whenComplete((franges, error) -> Platform.runLater(() -> {
            setmanaAnteriorId.setDisable(false);
            setmanaSeguentId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut carregar la disponibilitat"));
                return;
            }

            missatgeId.setText("");
            mostrarDisponibilitat(franges);
        }));
    }

    private void mostrarDisponibilitat(List<Disponibilitat> franges) {
        String[] dies = {"Dilluns", "Dimarts", "Dimecres", "Dijous", "Divendres", "Dissabte", "Diumenge"};

        calendariId.add(new Label("Hora"), 0, 0);

        for (int dia = 0; dia < 7; dia++) {
            LocalDate data = dillunsSeleccionat.plusDays(dia);
            Label diaLabel = new Label(dies[dia] + "\n" + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            calendariId.add(diaLabel, dia + 1, 0);
        }

        int fila = 1;
        for (int hora = 8; hora <= 22; hora++) {
            Label horaLabel = new Label(String.format("%02d:00", hora));
            calendariId.add(horaLabel, 0, fila);
            fila++;
        }

        for (Disponibilitat franja : franges) {
            if (consultaPublica) {
                Label franjaLabel = new Label();

                if (franja.isCaducada()) {
                    franjaLabel.setText("No disponible");
                } else {
                    franjaLabel.setText(franja.getDisponibles() + "/" + franja.getTotalPistes());
                }

                franjaLabel.setMinWidth(100);
                franjaLabel.setMinHeight(25);
                calendariId.add(franjaLabel, franja.getData().getDayOfWeek().getValue(), franja.getHora().getHour() - 7);
            } else {
                Button boto = new Button(franja.getDisponibles() + "/" + franja.getTotalPistes());
                boto.setMinWidth(100);
                boto.setMinHeight(25);
                boto.setDisable(franja.isCaducada() || franja.getDisponibles() <= 0);
                boto.setOnAction(event -> confirmarReserva(franja));

                calendariId.add(boto, franja.getData().getDayOfWeek().getValue(), franja.getHora().getHour() - 7);
            }
        }
    }

    private void confirmarReserva(Disponibilitat franja) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/confirmar_reserva.fxml")));
            Parent root = loader.load();

            ConfirmarReservaController controller = loader.getController();
            controller.setFranja(franja);

            BorderPane borderPane = (BorderPane) disponibilitatId.getParent();
            borderPane.setCenter(root);
        } catch (IOException e) {
            missatgeId.setText("Error: No s'ha pogut obrir la pantalla de reserva");
        }
    }

    public void setConsultaPublica(boolean consultaPublica) {
        this.consultaPublica = consultaPublica;
        consultaPublicaId.setVisible(consultaPublica);
        consultaPublicaId.setManaged(consultaPublica);
    }

    @FXML
    private void tornarInici() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/inici.fxml")));
        disponibilitatId.getScene().setRoot(root);
    }
}