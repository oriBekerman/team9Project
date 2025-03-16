package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.time.LocalTime;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.UpdateBranchResEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import java.util.ArrayList;
import javafx.application.Platform;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.greenrobot.eventbus.Subscribe;




public class ReservationCntBoundary {
    public String chosen;
    public SimpleClient client;
    private Branch branch;
    Set<RestTable> availableTables = new HashSet<>();




    @FXML
    private ComboBox<String> hoursList;

    @FXML
    private Button BackBtn;

    @FXML
    private Button CntBtn;

    public ReservationCntBoundary() {
        EventBus.getDefault().register(this);
    }

    @FXML
    void BackAct(ActionEvent event) {
        LocalTime time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));

        for (RestTable table: availableTables)
            table.removeUnavailableFromTime(time);

        switchScreen("Reservation");
    }

    @FXML
    void chooseHours(ActionEvent event) throws IOException {
        chosen = hoursList.getSelectionModel().getSelectedItem();
        SimpleClient.getClient().mapReservation.put("Hours",chosen);
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");


        // Parse the time from string to LocalTime
        LocalTime time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));
        availableTables = this.branch.getAvailableTablesWithNumPeople(Integer.parseInt(numPeople), time,area);
        for (RestTable table: availableTables)
            table.addUnavailableFromTime(time);
        Request<Branch> request = new Request<>(BRANCH, UPDATE_BRANCH, branch);
        SimpleClient.getClient().sendToServer(request);

    }

    @FXML
    void continueAct(ActionEvent event) {


        client = SimpleClient.getClient();
        client.mapReservation.put("Hours",chosen);
        //client.post (let everyone know the hour was taken)
        switchScreen("Personal Details Filling");

    }
@Subscribe
    void setHoursList() throws IOException {
        //getBranch and then set hours list
        Request<String> request2 = new Request<>(BRANCH,GET_BRANCH_BY_NAME, SimpleClient.getClient().mapReservation.get("Branch"));
       SimpleClient.getClient().sendToServer(request2);

    }

    @FXML
    void initialize() throws IOException {
        System.out.println("finally!!!!!!!!!!!!!!");

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

    @Subscribe
    public void onBranchSelected(BranchSelectedEvent event) {
        this.branch = event.getBranch();
        updateAvailableTimesAndUI();
    }

    @Subscribe
    public void OnUpdateBranchResEvent(UpdateBranchResEvent event) {
        System.out.println("Received UpdateBranchResEvent!!!!!!!!!!!");
        this.branch = event.getBranch();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("hours occupied");
            alert.setHeaderText(null);
            alert.setContentText("Please press OK to refresh to see the latest changes.");
            alert.showAndWait();
            try {
                setHoursList();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void updateAvailableTimesAndUI() {
        // Get values from SimpleClient
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String timeString = SimpleClient.getClient().mapReservation.get("Hours");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");
        Set<RestTable> availableTables = new HashSet<>();

        // Parse the time from string to LocalTime
        LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));

        // Use a List to hold available times
        List<String> availableTimes = new ArrayList<>();

        // Iterate through available time slots (every 15 minutes)
        for (int i = 0; availableTimes.size()<=4 ; i += 15) {

            time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm")).plusMinutes(i);
            if(LocalTime.parse(branch.getClosingTime()).minusMinutes(90).isBefore(time)
            )
                break;
            availableTables = this.branch.getAvailableTablesWithNumPeople(Integer.parseInt(numPeople), time,area);
            if (!availableTables.isEmpty()) {
                availableTimes.add(time.toString());  // Add the available time to the list
            }
            System.out.println("Available tables at " + time + " for " + numPeople + " people: " + availableTables.size());

        }

        // Update the ComboBox on the JavaFX thread
        Platform.runLater(() -> {
            hoursList.getItems().clear();  // Clear existing items
            hoursList.getItems().addAll(availableTimes);  // Add all available times
        });
    }


}
