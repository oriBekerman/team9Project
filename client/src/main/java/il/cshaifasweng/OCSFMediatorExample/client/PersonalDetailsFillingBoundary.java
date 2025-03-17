package il.cshaifasweng.OCSFMediatorExample.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintCustomerEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class PersonalDetailsFillingBoundary {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button contToCCInfoBtn;

    @FXML
    private TextField mailTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    private String type;
    public boolean typeIsSet=false;

    public PersonalDetailsFillingBoundary(String type) {
        this.type = type;
        this.typeIsSet=true;
    }

    public PersonalDetailsFillingBoundary() {}
    @FXML
    void contToCCinfoFill(ActionEvent event) {
        switchScreen("Credit Card Info");
//        if(!type.isEmpty())
//        {
//            postDetails();
//        }
    }

    @FXML
    void backToReservation(ActionEvent event) {
        switchScreen("Reservation");
    }

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert contToCCInfoBtn != null : "fx:id=\"contToCCinfoBtn\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert mailTextField != null : "fx:id=\"mailTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert nameTextField != null : "fx:id=\"nameTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";
        assert phoneTextField != null : "fx:id=\"phoneTextField\" was not injected: check your FXML file 'personalDetailsFilling.fxml'.";

    }
//    public void setType(String type) {
//        this.type = type;
//        this.typeIsSet=true;
//    }
// initialize the map before letting the map page be opened
    public void setType(String type) {
        System.out.println("in set type before sync");
        synchronized (this) {
            if (this.type == null || !this.type.equals(type)) {
                System.out.println("in type map after sync");
                this.type = type;
                this.typeIsSet = true;
                notifyAll();
            }
        }
    }



}
