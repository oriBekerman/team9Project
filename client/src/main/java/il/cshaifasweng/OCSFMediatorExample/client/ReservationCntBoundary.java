package il.cshaifasweng.OCSFMediatorExample.client;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class ReservationCntBoundary {
    public String chosen;
    public SimpleClient client;

    @FXML
    private ComboBox<String> hoursList;

    @FXML
    private Button BackBtn;

    @FXML
    private Button CntBtn;

    @FXML
    void BackAct(ActionEvent event) {
        switchScreen("Reservation");
    }

    @FXML
    void chooseHours(ActionEvent event) {
        chosen = hoursList.getSelectionModel().getSelectedItem();
    }

    @FXML
    void continueAct(ActionEvent event) {


        client = SimpleClient.getClient();
        client.mapReservation.put("Hours",chosen);
        //client.post (let everyone know the hour was taken)
        switchScreen("Personal Details Filling");

    }

}
