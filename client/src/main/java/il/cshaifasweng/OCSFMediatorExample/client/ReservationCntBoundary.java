package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import org.greenrobot.eventbus.Subscribe;




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
        switchScreen("Home Page");
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

    void setHoursList() throws IOException {
        Request<String> request = new Request<>(BRANCH,FETCH_BRANCH_TABLES, SimpleClient.getClient().mapReservation.get("Branch"));
        SimpleClient.getClient().sendToServer(request);

    }

    @FXML
    void initialize() throws IOException {
        EventBus.getDefault().register(this);
        setHoursList();
    }

    @Subscribe
    public void onBranchTablesReceived(BranchTablesReceivedEvent event) {
        hoursList.getItems().clear();
        for (RestTable table : event.getTables()) {
            if(SimpleClient.getClient().mapReservation.get("Area")==table.getArea())
            hoursList.getItems().add(table.getAvailableFromTimes().toString());  // Assuming RestTable has a method getAvailableTime()
        }
    }

}
