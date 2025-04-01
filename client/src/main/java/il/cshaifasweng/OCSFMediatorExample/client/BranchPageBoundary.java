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
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static il.cshaifasweng.OCSFMediatorExample.client.App.*;

public class BranchPageBoundary
{
    private boolean isMenuLoaded = false;
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
    private SecondaryBoundary secondaryBoundary;

    public BranchPageBoundary()
    {
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

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
    private TableView<MenuItem> menuTableView;
    @FXML
    void navToHP(ActionEvent event)
    {
        onExit();
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
    void initialize()
    {

        try
        {
            SimpleClient.getClient().displayBranchMenu(branch);
            System.out.println("get menu from initialize of branch");
        }
        catch (IOException e)
        {
            System.err.println("Error sending client confirmation: " + e.getMessage());
        }
   //     menuTableView.refresh();
        updateUI();
        assert backToHPBtn != null : "fx:id=\"backToHPBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert haifaBBtn != null : "fx:id=\"haifaBBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert jersualemBtn != null : "fx:id=\"jersualemBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert telAvivBtn != null : "fx:id=\"telAvivBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        assert zikhronBtn != null : "fx:id=\"zikhronBtn\" was not injected: check your FXML file 'BranchPage.fxml'.";
        getUserAuthorizedTools();
    }

    public void navToReservationPage(ActionEvent actionEvent)
    {
        switchScreen("Reservation");
    }
    public void navToDeliveryPage(ActionEvent actionEvent) {
        currentDelivery.setBranch(branch);
        switchToDelivery(currentDelivery);
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
        getUserAuthorizedTools();
    }
    // Method to update UI based on the branch data
    private void updateUI() {
        if (branch != null && branchTitle != null)
        {
            branchTitle.setText("Branch: " + branch.getName());
            openingHoursLabel.setText("opening hours: " + branch.getOpeningTime() + " - " + branch.getClosingTime());
        }
    }

    public void displayMenu(ActionEvent actionEvent)
    {
        switchToBranchMenu(branch);
//        if (!isMenuLoaded)
//        {
//            System.out.println("Requesting menu...");
//            try
//            {
//                SimpleClient.getClient().displayBranchMenu(branch);
//            }
//            catch (Exception e)
//            {
//            e.printStackTrace();
//            }
//            isMenuLoaded = true;
//        }
//        else
//        {
//            System.out.println("Menu already loaded, skipping request.");
//        }
//        switchScreen("secondary");
//        try
//        {
//            App.setRoot("secondary");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void loadBranchMap(ActionEvent actionEvent){
        openBranchMap();
    }
    public void openBranchMap()
    {
        System.out.println("in open mao in branch boundary");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableMapPage.fxml"));
            Parent mapPageRoot = loader.load();
            // Get the controller and pass the branch
            TableMapBoundary boundary = loader.getController();
            boundary.setMap(branch);
            synchronized (boundary)
            {
                while (!boundary.mapIsSet)
                {
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
            Thread.currentThread().interrupt();
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
            List<RestTable> newTables=tables.stream().toList();

            if (tables != null && !tables.isEmpty()) {
                branch.setRestTables(newTables);
                branchTablesSet = true;
                lock.notifyAll(); // Wake up any waiting threads
            } else {
                System.out.println("Received empty table list for branch!");
            }
        }
    }
    public void onExit()
    {
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
            System.out.println("Unregistered from EventBus");
        }

    }
    private void getUserAuthorizedTools() {
        if (SimpleClient.getClient().getActiveUser() != null) {
            tableBtn.setVisible(true);
        }
        else
        {
            tableBtn.setVisible(false);
        }
    }

}
