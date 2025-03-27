package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.List;

public class EnterEmailController {

    @FXML private TextField emailField;
    @FXML private Button checkBtn;
    @FXML private Button backBtn;

    @FXML
    void handleCheckReservations() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Email field cannot be empty");
            return;
        }

        List<ResInfo> reservations = SimpleClient.getClient().getAllReservations();

        boolean hasReservations = reservations.stream()
                .anyMatch(res -> res.getCustomer() != null && res.getCustomer().getEmail().equals(email));

        if (!hasReservations) {
            showAlert("No reservations found with this email.");
            return;
        }

        //SimpleClient.getClient().userEmail = email; ///CHECK THIS
        App.switchScreen("reservationList");
    }


    @FXML
    void handleBack() {
        App.switchScreen("Reservation");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
