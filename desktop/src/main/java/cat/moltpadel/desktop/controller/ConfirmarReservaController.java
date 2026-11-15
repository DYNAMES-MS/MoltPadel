package cat.moltpadel.desktop.controller;

import cat.moltpadel.desktop.exception.GestorErrorsApi;
import cat.moltpadel.desktop.model.ConfirmarReserva;
import cat.moltpadel.desktop.model.Disponibilitat;
import cat.moltpadel.desktop.model.Pista;
import cat.moltpadel.desktop.service.PistaService;
import cat.moltpadel.desktop.service.ReservaService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ConfirmarReservaController {

    @FXML
    private Label franjaId;

    @FXML
    private FlowPane interiorsId;

    @FXML
    private FlowPane exteriorsId;

    @FXML
    private Label pistaId;

    @FXML
    private Label tipusId;

    @FXML
    private Label preuId;

    @FXML
    private Label missatgeId;

    @FXML
    private Button tornarBotoId;

    @FXML
    private Button confirmarBotoId;

    private final PistaService pistaService = new PistaService();
    private final ReservaService reservaService = new ReservaService();

    private Disponibilitat franja;
    private ConfirmarReserva seleccio;

    public void setFranja(Disponibilitat franja) {
        this.franja = franja;

        franjaId.setText(franja.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "  " + franja.getHora() + " - " + franja.getHora().plusHours(1));
        carregarPistes();
    }

    private void carregarPistes() {
        pistaService.getPistesDisponibles(franja.getData(), franja.getHora()).whenComplete((pistes, error) -> Platform.runLater(() -> {
            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut carregar les pistes"));
                return;
            }

            missatgeId.setText("");

            for (Pista pista : pistes) {
                Button boto = new Button(pista.getNom());
                boto.setOnAction(event -> seleccionarPista(pista));

                if ("INTERIOR".equals(pista.getTipus())) {
                    interiorsId.getChildren().add(boto);
                } else {
                    exteriorsId.getChildren().add(boto);
                }
            }

            if (interiorsId.getChildren().isEmpty()) {
                interiorsId.getChildren().add(new Label("Sense pistes disponibles"));
            }

            if (exteriorsId.getChildren().isEmpty()) {
                exteriorsId.getChildren().add(new Label("Sense pistes disponibles"));
            }
        }));
    }

    private void seleccionarPista(Pista pista) {
        seleccio = null;
        confirmarBotoId.setDisable(true);
        interiorsId.setDisable(true);
        exteriorsId.setDisable(true);
        pistaId.setText(pista.getNom());
        tipusId.setText("");
        preuId.setText("");
        missatgeId.setText("");

        reservaService.getConfirmarReserva(pista.getId(), franja.getData(), franja.getHora()).whenComplete((resposta, error) -> Platform.runLater(() -> {
            interiorsId.setDisable(false);
            exteriorsId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'han pogut obtenir les reserves"));
                return;
            }

            seleccio = resposta;
            pistaId.setText(resposta.getNomPista());

            if ("INTERIOR".equals(resposta.getTipus())) {
                tipusId.setText("Interior");
            } else {
                tipusId.setText("Exterior");
            }

            preuId.setText("Preu: " + resposta.getPreu().toPlainString() + " €");
            missatgeId.setText("");
            confirmarBotoId.setDisable(false);
        }));
    }

    @FXML
    private void confirmarReserva() {
        confirmarBotoId.setDisable(true);
        tornarBotoId.setDisable(true);
        interiorsId.setDisable(true);
        exteriorsId.setDisable(true);

        reservaService.crearReserva(seleccio.getPistaId(), seleccio.getData(), seleccio.getHora()).whenComplete((v, error) -> Platform.runLater(() -> {
            tornarBotoId.setDisable(false);

            if (error != null) {
                missatgeId.setText(GestorErrorsApi.getMissatge(error, "Error: No s'ha pogut confirmar la reserva"));
                return;
            }

            missatgeId.setText("Reserva creada correctament");
        }));
    }

    @FXML
    private void tornarDisponibilitat() throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/disponibilitat.fxml")));
        BorderPane panell = (BorderPane) franjaId.getParent().getParent();
        panell.setCenter(root);
    }
}