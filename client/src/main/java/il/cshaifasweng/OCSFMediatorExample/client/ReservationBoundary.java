package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

//get reservation details from user and stores in map in simpleClient -> on continue go to ReservationCntPage

public class ReservationBoundary {
    public SimpleClient client;
    public AnchorPane root;
    public Label titleLabel;


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane MapPane;

    @FXML
    private Button backBtn;

    @FXML
    private ComboBox<String> branchesList;

    @FXML
    private ComboBox<String> hoursList;

    @FXML
    private Button continueBtn;

    @FXML
    private ComboBox<String> InOutdoorList;


    @FXML
    private ComboBox<String> numpeopleList;

    @FXML
    void backToHP(ActionEvent event) {

        switchScreen("Home Page");
    }
    @FXML
    void chooseBranch(ActionEvent event) {
        String chosen = branchesList.getSelectionModel().getSelectedItem();
        client = SimpleClient.getClient();
        client.mapReservation.put("Branch",chosen);
    }

    @FXML
    void chooseHour(ActionEvent event) {
        String chosen = hoursList.getSelectionModel().getSelectedItem();
        client = SimpleClient.getClient();
        client.mapReservation.put("Hours",chosen);
    }


    @FXML
    void chooseInOutdoor(ActionEvent event) {
        String chosen = InOutdoorList.getSelectionModel().getSelectedItem();
        client = SimpleClient.getClient();
        client.mapReservation.put("Area",chosen);

        SimpleClient.getClient().resInfo.setInOrOut(chosen);
    }

    @FXML
    void chooseNumPeople(ActionEvent event) {
        String chosen = numpeopleList.getSelectionModel().getSelectedItem();
        client = SimpleClient.getClient();
        client.mapReservation.put("num",chosen);

        SimpleClient.getClient().resInfo.setNumOfGuests(Integer.parseInt(chosen));
    }

    @FXML
    void navToPersonalDetailsFilling(ActionEvent event) {
        switchScreen("reservationCnt");
    }

    void setHoursList(){
        hoursList.getItems().add("10:00");
        hoursList.getItems().add("10:15");
        hoursList.getItems().add("10:30");
        hoursList.getItems().add("10:45");

        hoursList.getItems().add("11:00");
        hoursList.getItems().add("11:15");
        hoursList.getItems().add("11:30");
        hoursList.getItems().add("11:45");

        hoursList.getItems().add("12:00");
        hoursList.getItems().add("12:15");
        hoursList.getItems().add("12:30");
        hoursList.getItems().add("12:45");

        hoursList.getItems().add("13:00");
        hoursList.getItems().add("13:15");
        hoursList.getItems().add("13:30");
        hoursList.getItems().add("13:45");

        hoursList.getItems().add("14:00");
        hoursList.getItems().add("14:15");
        hoursList.getItems().add("14:30");
        hoursList.getItems().add("14:45");

        hoursList.getItems().add("15:00");

        hoursList.getItems().add("15:15");
        hoursList.getItems().add("15:30");
        hoursList.getItems().add("15:45");
        hoursList.getItems().add("16:00");
        hoursList.getItems().add("16:15");
        hoursList.getItems().add("16:30");
        hoursList.getItems().add("16:45");
        hoursList.getItems().add("17:00");
        hoursList.getItems().add("17:15");
        hoursList.getItems().add("17:30");
        hoursList.getItems().add("17:45");
        hoursList.getItems().add("18:00");
        hoursList.getItems().add("18:15");
        hoursList.getItems().add("18:30");
        hoursList.getItems().add("18:45");
        hoursList.getItems().add("19:00");
        hoursList.getItems().add("19:15");
        hoursList.getItems().add("19:30");
        hoursList.getItems().add("19:45");
        hoursList.getItems().add("20:00");
        hoursList.getItems().add("20:15");
        hoursList.getItems().add("20:30");

    }
    void setBranchesList(){
        branchesList.getItems().add("Haifa");
        branchesList.getItems().add("Tel Aviv");
        branchesList.getItems().add("Jerusalem");
        branchesList.getItems().add("Zikhron Ya'akov");
    }

    void setInOutdoorList()
    {
        InOutdoorList.getItems().add("inside");
        InOutdoorList.getItems().add("outside");
    }

    void setNumpeopleList()
    {
        numpeopleList.getItems().add("1");
        numpeopleList.getItems().add("2");
        numpeopleList.getItems().add("3");
        numpeopleList.getItems().add("4");
        numpeopleList.getItems().add("5");
        numpeopleList.getItems().add("6");
        numpeopleList.getItems().add("7");
        numpeopleList.getItems().add("8");
        numpeopleList.getItems().add("9");
    }


   // need to modify it according to the as needed
//    private void updateUIBasedOnUserRole() {
//        String role = SessionManager.getInstance().getAuthorization();
//        if (!"Admin".equals(role)) {
//            continueBtn.setDisable(true);  // Example: Disable the continue button if not an admin
//        }
//    }



    @FXML
    void initialize() {
        assert MapPane != null : "fx:id=\"MapPane\" was not injected: check your FXML file 'reservation.fxml'.";
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'reservation.fxml'.";
        assert branchesList != null : "fx:id=\"branchesList\" was not injected: check your FXML file 'reservation.fxml'.";
        assert continueBtn != null : "fx:id=\"continueBtn\" was not injected: check your FXML file 'reservation.fxml'.";
        assert hoursList != null : "fx:id=\"hoursList\" was not injected: check your FXML file 'reservation.fxml'.";
        //updateUIBasedOnUserRole();
        setHoursList();
        setBranchesList();
        setInOutdoorList();
        setNumpeopleList();
        setStyle();
    }
    public void setStyle()
    {
        root.setStyle("-fx-background-color: #fbe9d0;");
        titleLabel.setStyle(
                "-fx-font-size: 24px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: #4e453c;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 20px 0;\n" +
                "    -fx-font-family: \"Serif\";");
        // Buttons Styling
        String buttonStyle = "-fx-background-color: #8a6f48;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px 16px;" +
                "-fx-border-radius: 5px;";

        backBtn.setStyle(buttonStyle);


    }

}
