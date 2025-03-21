package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.*;

public class BranchPageBoundary {

    public boolean branchIsSet = false;
    public Button reservationBtn;
    public Button deliveryBtn;
    public Button complaintBtn;
    public Label openingHoursLabel;
    public Label branchTitle;
    public VBox sideBar;
    public Label openHour;
    public Label closeHour;
    public Button menuBtn;
    public Button tableBtn;
    private final Object lock = new Object();
    private boolean branchTablesSet = false;
    private Delivery currentDelivery= new Delivery();

    public BranchPageBoundary() {
        EventBus.getDefault().register(this);
    };
    public Branch branch;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backToHPBtn;

    @FXML
    private Button haifaBBtn;

    @FXML
    private Button jersualemBtn;

    @FXML
    private Button telAvivBtn;

    @FXML
    private Button zikhronBtn;

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void navToHaifaBranch(ActionEvent event) {

    }

    @FXML
    void navToJersualemBranch(ActionEvent event) {

    }

    @FXML
    void navToTelAvivBranch(ActionEvent event) {

    }

    @FXML
    void navToZikhronBranch(ActionEvent event) {

    }

    @FXML
    void initialize() {
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert haifaBBtn != null : "fx:id=\"haifaBBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert jersualemBtn != null : "fx:id=\"jersualemBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert telAvivBtn != null : "fx:id=\"telAvivBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert zikhronBtn != null : "fx:id=\"zikhronBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";


    }
    public void navToReservationPage(ActionEvent actionEvent) {
    }
    public void navToDeliveryPage(ActionEvent actionEvent) {
        switchToDelivery(branch, currentDelivery);
    }
    public void navToComplaintPage(ActionEvent actionEvent) {
    }
    // Method to set the branch data
    public void setBranch(Branch branch) {
        this.branch = branch;
        branchTitle.setText("Branch: " + branch.getName());
        openHour.setText(branch.getOpeningTime());
        closeHour.setText(branch.getClosingTime());
        branchIsSet = true;
        System.out.println("in branch page controller");
        System.out.println("opening: " + branch.getOpeningTime());
    }
    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null) {
            branchTitle.setText("Branch: " + branch.getName());
            openingHoursLabel.setText("opening hours: " + branch.getOpeningTime() + " - " + branch.getClosingTime());
        }
    }
    public void displayMenu(ActionEvent actionEvent) {
        switchScreen("secondary");
        try {
            App.setRoot("secondary");
            SimpleClient.getClient().displayBranchMenu(branch);
//            Menu menu = new Menu(branch.getBranchMenuItems());
//            menu.printMenu();
//            SimpleClient.getClient().showMenu(menu);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadBranchMap(ActionEvent actionEvent){
        openBranchMap();
//        try {
//            SimpleClient.getClient().fetchTables(branch);
//        } catch (IOException e) {
//            System.out.println("fetch failed");
//            throw new RuntimeException(e);
//        }
    }

    public void openBranchMap() {
        System.out.println("in open mao in branch boundary");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableMapPage.fxml"));
            Parent mapPageRoot = loader.load();
            // Get the controller and pass the branch
            TableMapBoundary boundary = loader.getController();
            boundary.setMap(branch);
            synchronized (boundary)
            {
                while (!boundary.mapIsSet) {
                    System.out.println("Waiting for map to be set...");
                    boundary.wait();  // Waits until notifyAll() is called
                }
            }
            Platform.runLater(() -> {
                try {
                    App.setContent(mapPageRoot);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();  // Restore interrupted state
        }
    }

    public void checkTablesList()
    {
        if(branch.getTables() != null)
        {
            System.out.println("branch tables null");
        }
        if (branch.getTables().isEmpty())
        {
            System.out.println("branch tables empty");
        }
    }
    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (lock) {
            System.out.println("Tables received for branch: " + branch.getName());
            Set<RestTable> tables = event.getTables();

            if (tables != null && !tables.isEmpty()) {
                branch.setRestTables(tables);
                branchTablesSet = true;
                lock.notifyAll(); // Wake up any waiting threads
            } else {
                System.out.println("Received empty table list for branch!");
            }
        }
    }

    //open selected branch page
    private void openMap(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableMapPage.fxml"));
            Parent mapPageRoot = loader.load();
            // Get the controller and pass the branch
            TableMapBoundary boundary = loader.getController();
            boundary.setMap(branch);
            if (boundary.mapIsSet) {
                System.out.println("map is already set");
            }
            while (!boundary.mapIsSet) {
                System.out.println("Waiting for map to be set");
            }
            App.setContent(mapPageRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
//change