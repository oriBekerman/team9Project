package il.cshaifasweng.OCSFMediatorExample.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import il.cshaifasweng.OCSFMediatorExample.client.App;


public class CancelReservationEnterEmailController {
    @FXML
    private TextField emailField;

    @FXML
    private void handleContinue(ActionEvent event) {
        String email = emailField.getText();
        if (email == null || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setContentText("Please enter a valid email.");
            alert.show();
            return;
        }
        State.setEmail(email); // store globally
        App.switchScreen("CancelReservationChoose");
    }
}
