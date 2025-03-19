package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
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
    boolean flag=false;






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
    void BackAct(ActionEvent event) throws IOException {
        if (flag==true) {

            chooseCancel();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cancellation in Progress");
            alert.setHeaderText(null);
            alert.setContentText("Reservation has been canceled. Press OK to continue.");

            alert.showAndWait();
            flag=false;
        }
        EventBus.getDefault().unregister(this);
        switchScreen("Reservation");
    }

    @FXML
    void chooseHours(ActionEvent event) throws IOException {

        // Ensure the client is initialized
        if (client == null) {
            client = SimpleClient.getClient();
        }

        flag=true;
        chosen = hoursList.getSelectionModel().getSelectedItem();
        SimpleClient.getClient().mapReservation.put("Hours",chosen);
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");


        // Parse the time from string to LocalTime
        LocalTime time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));
        availableTables = this.branch.getAvailableTablesWithNumPeople(Integer.parseInt(numPeople), time,area);
        String tableIDS="";
        for (RestTable table: availableTables)
        {
            table.addUnavailableFromTime(time);
            if(tableIDS.isEmpty())
            {
                tableIDS=String.valueOf(table.getId());
            }
            else
            {
                tableIDS=tableIDS+","+table.getId();
            }
        }
        client.mapReservation.put("tableIDS",tableIDS);
        Request<Branch> request = new Request<>(BRANCH, UPDATE_BRANCH, branch);
        SimpleClient.getClient().sendToServer(request);

    }

    @FXML
    void continueAct(ActionEvent event) {
        client = SimpleClient.getClient();
        client.mapReservation.put("Hours",chosen);

        // Start a 15-minute timer to ensure the user fills personal details in time
        // if the user does not fill details within 15 min cancel table allocation
        TimerManager.getInstance().startTimer("reservationTimeout", () -> {
            System.out.println("Reservation expired! Taking action...");
            try {
                BackAct(new ActionEvent()); // Automatically cancel table allocation
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 15);
        openPersonalDetailsPage();

    }

    //requests branch instance from server
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

//        Platform.runLater(() -> {
//            hoursList.getScene().getWindow().setOnCloseRequest(event -> {
//                try {
//                    chooseCancel();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//        });
    }

    @Subscribe
    public void onBranchTablesReceived(BranchTablesReceivedEvent event) {
        hoursList.getItems().clear();
        for (RestTable table : event.getTables()) {
            if(SimpleClient.getClient().mapReservation.get("Area")==table.getArea())
            hoursList.getItems().add(table.getAvailableFromTimes().toString());  // Assuming RestTable has a method getAvailableTime()
        }
    }

    //initialize branch received after request in setHours
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
        updateAvailableTimesAndUI();
    }

    //sets a list of time when there are available tables fitting the user input in the second hours comboBox
    private void updateAvailableTimesAndUI() {
        //initialize reservation data from map in simpleClient (data in map init in reservationBoundaryPage)
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

    private void chooseCancel() throws IOException
    {
        LocalTime time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));

        for (RestTable table : availableTables) {
            Set<LocalTime> updatedTimes = new HashSet<>(table.getUnavailableFromTimes());
            updatedTimes.remove(time);
            table.setUnavailableFromTimes(updatedTimes);
        }
        Request<Branch> request = new Request<>(BRANCH, UPDATE_BRANCH, branch);
        SimpleClient.getClient().sendToServer(request);


    }



    public void openPersonalDetailsPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalDetailsFilling.fxml"));
            Parent PersonalInfoPageRoot = loader.load();
            // Get the controller and set the type before waiting
            PersonalDetailsFillingBoundary boundary = loader.getController();
            boundary.setType("reservation");  // This should be set before waiting
            synchronized (boundary) {
                while (!boundary.typeIsSet) {
                    System.out.println("Waiting for type to be set...");
                    boundary.wait();  // Waits until notifyAll() is called
                }
            }
            Platform.runLater(() -> {
                try {
                    App.setContent(PersonalInfoPageRoot);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();  // Restore interrupted state
        }
    }

    @Subscribe
    public void onDetailsSetEvent(CreditCardInfoSet event) {
        makeReservation();
    }

    private void makeReservation() {
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");
        String timeString = SimpleClient.getClient().mapReservation.get("Hours");
        String name=SimpleClient.getClient().mapReservation.get("name");
        String email=SimpleClient.getClient().mapReservation.get("mail");
        String phone=SimpleClient.getClient().mapReservation.get("phone");
        String cardNum=SimpleClient.getClient().mapReservation.get("cardNum");
        String cvv=SimpleClient.getClient().mapReservation.get("cvv");
        String expDate=SimpleClient.getClient().mapReservation.get("expDate");
        LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
        int numOfGuests=Integer.parseInt(numPeople);
        Customer customer = new Customer(name,email,phone,cardNum,expDate,cvv);
        ResInfo reservation=new ResInfo(branch,customer,time,numOfGuests,area,availableTables);
        reservation.setStatus(ResInfo.Status.APPROVED);
        reservation.setCustomer(customer);
        reservation.setBranch(branch);
        reservation.setTable(availableTables);
        Request request=new Request<>(RESERVATION,ADD_RESERVATION,reservation);
        try{
            SimpleClient.getClient().sendToServer(request);
        }
       catch (Exception e){
            e.printStackTrace();
       }

    }

    //display message that the reservation is approved
    @Subscribe
    public void reservationAddedEvent(ReservationAddedEvent event) {
        String message=event.getMessage();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

}
