package il.cshaifasweng.OCSFMediatorExample.client;
import java.io.IOException;
import java.time.LocalTime;

import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.*;

import java.util.*;

import javafx.application.Platform;

import java.time.format.DateTimeFormatter;

import org.greenrobot.eventbus.Subscribe;

public class ReservationCntBoundary {
    public String chosen;
    public SimpleClient client;
    public Label noTablesLabel;
    public Label titleLabel;
    public AnchorPane root;
    private Branch branch;
    Set<RestTable> availableTables = new HashSet<>();
    boolean flag=false;
    Map<LocalTime,Set<RestTable>> optionalTablesMap=new HashMap<>();

    @FXML
    private ComboBox<String> hoursList;

    @FXML
    private Button BackBtn;

    @FXML
    private Button CntBtn;

    @FXML
    void initialize() throws IOException {
        System.out.println("finally!!!!!!!!!!!!!!");
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
        setStyle();
        setHoursList(); // trigger initial data fetch
    }

    @FXML
    void BackAct(ActionEvent event) throws IOException {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        switchScreen("Reservation");
    }


    @FXML
    void chooseHours(ActionEvent event) throws IOException {
        flag=true;
        chosen = hoursList.getSelectionModel().getSelectedItem();
        SimpleClient.getClient().mapReservation.put("Hours",chosen);
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");
        if (chosen == null || chosen.isEmpty()) {
            System.out.println("No time selected!");
            return; // or show alert and exit the method
        }
        LocalTime time = LocalTime.parse(chosen);
        time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));
        SimpleClient.getClient().resInfo.setBranch(branch);
        SimpleClient.getClient().resInfo.setHours(time);
        Set<RestTable> tables=optionalTablesMap.get(time);
        SimpleClient.getClient().resInfo.setTable(tables);
    }

    @FXML
    void continueAct(ActionEvent event) {
        client = SimpleClient.getClient();
        client.mapReservation.put("Hours",chosen);
        openPersonalDetailsPage();

    }

    //requests branch instance from server
    @Subscribe
    void setHoursList() throws IOException {
        //getBranch and then set hours list
        Request<String> request2 = new Request<>(BRANCH,GET_BRANCH_BY_NAME, SimpleClient.getClient().mapReservation.get("Branch"));
        SimpleClient.getClient().sendToServer(request2);

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
    public void onBranchSelected(BranchSentEvent event) {
        this.branch = event.getBranch();
        SimpleClient.getClient().resInfo.setBranch(branch);
        updateAvailableTimesAndUI();
    }

    //sets a list of time when there are available tables fitting the user input in the second hours comboBox
    private void updateAvailableTimesAndUI() {
        //initialize reservation data from map in simpleClient (data in map init in reservationBoundaryPage)
        String area = SimpleClient.getClient().mapReservation.get("Area");
        String timeString = SimpleClient.getClient().mapReservation.get("Hours");
        String numPeople = SimpleClient.getClient().mapReservation.get("num");
//        timeString=SimpleClient.getClient().resInfo.getHours().toString();
        Set<RestTable> availableTables = new HashSet<>();
        if (timeString == null || timeString.isEmpty()) {
            System.out.println("timeString is null or empty in updateAvailableTimesAndUI!");
            return;
        }
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
                availableTimes.add(time.toString());// Add the available time to the list
                optionalTablesMap.put(time, availableTables);
            }
            System.out.println("Available tables at " + time + " for " + numPeople + " people: " + availableTables.size());
        }
        // Update the ComboBox on the JavaFX thread
        Platform.runLater(() ->
        {
            hoursList.getItems().clear();
            if (availableTimes.isEmpty())
            {
                noTablesLabel.setText("No available tables match your selected time, area, branch, and guest count. " +
                        "Please go back and adjust your choices to try again.");
            }
            else
            {
                hoursList.getItems().addAll(availableTimes);  // Add all available times
            }
        });
        EventBus.getDefault().unregister(this);
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    public void openPersonalDetailsPage()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalDetailsFilling.fxml"));
            Parent PersonalInfoPageRoot = loader.load();
            // Get the controller and set the type before waiting
            PersonalDetailsFillingBoundary boundary = loader.getController();
            boundary.setType("reservation");  // This should be set before waiting
            if(SimpleClient.getClient().rebookReservation)
            {
                boundary.setFields();
            }
            synchronized (boundary) {
                while (!boundary.typeIsSet) {
                    System.out.println("Waiting for type to be set...");
                    boundary.wait();
                }
            }
            Platform.runLater(() ->
            {
                try
                {
                    App.setContent(PersonalInfoPageRoot);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();  // Restore interrupted state
        }
    }

    @Subscribe
    public void onDetailsSetEvent(CreditCardInfoSet event)
    {
        System.out.println("in on details filled event:" );
        System.out.println("reservation:");
        System.out.println("customer name: " +SimpleClient.getClient().resInfo.getCustomer().getName() );
        System.out.println("customer card: " +SimpleClient.getClient().resInfo.getCustomer().getCreditCardNumber() );
        System.out.println("customer email: "+SimpleClient.getClient().resInfo.getCustomer().getEmail());
        LocalTime time=SimpleClient.getClient().resInfo.getHours();
        SimpleClient.getClient().resInfo.setTable(SimpleClient.getClient().resInfo.getTable());
        SimpleClient.getClient().rebookReservation=false;
        Request request=new Request(RESERVATION,ADD_RESERVATION,SimpleClient.getClient().resInfo);
        try
        {
            SimpleClient.getClient().sendToServer(request);
        } catch (IOException e) {
            System.out.println("fail to send reservation to server");
            throw new RuntimeException(e);
        }
        System.out.println("in on details filled event: sent reservation to server" );

    }

    private void tableNotAvailable() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Table Not Available");
            alert.setHeaderText("Dear customer, unfortunately, the table you selected is no longer available. " +
                    "Please go back and choose a different time slot. " +
                    "We apologize for the inconvenience and appreciate your understanding.");
            alert.getButtonTypes().setAll(ButtonType.PREVIOUS);
            Optional<ButtonType> result = alert.showAndWait();
            // on OK
            if (result.isPresent() && result.get() == ButtonType.PREVIOUS) {
                switchScreen("Reservation");
                EventBus.getDefault().unregister(this);
            };
        });
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
        Customer customer = new Customer(name,"",email,phone,cardNum,expDate,cvv);
        ResInfo reservation=new ResInfo(branch,customer,time,numOfGuests,area,availableTables);
        reservation.setStatus(ResInfo.Status.APPROVED);
        reservation.setCustomer(customer);
        reservation.setBranch(branch);
        if(availableTables.isEmpty() || availableTables==null)
        {
            System.out.println("available tables are null or empty");
        }
        reservation.setTable(availableTables);
        reservation.setHours(time);
        Request request=new Request<>(RESERVATION,ADD_RESERVATION,reservation);
        try{
            SimpleClient.getClient().sendToServer(request);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Subscribe
    public void reservationAddedEvent(ReservationAddedEvent event) {
        String message = event.getMessage();
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.getButtonTypes().setAll(ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
            // on OK
            if (result.isPresent() && result.get() == ButtonType.OK) {
                performAdditionalAction();
            }
        });
    }

    @Subscribe
    public void onTableIsReservedEvent(TableIsReservedEvent event)
    {
        System.out.println("recieved event");
//        resetEventBus();
        tableNotAvailable();
        for(ResInfo resInfo: event.getReservation())
        {
            updatePage(resInfo);
        }

    }
    // return to primary page after OK
    private void performAdditionalAction() {
        System.out.println("in preform addi");
        switchScreen("Home Page");
        EventBus.getDefault().unregister(this);
    }
//    public void timeViolation()
//    {
//        LocalTime time = LocalTime.parse(chosen, DateTimeFormatter.ofPattern("HH:mm"));
//        // Ensure unavailable times are removed correctly
//        for (RestTable table : availableTables) {
//            Set<LocalTime> updatedTimes = new HashSet<>(table.getUnavailableFromTimes());
//            updatedTimes.remove(time);
//            table.setUnavailableFromTimes(updatedTimes);
//        }
//        Request<Branch> request = new Request<>(BRANCH, UPDATE_BRANCH, branch);
//        try {
//            SimpleClient.getClient().sendToServer(request);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Time violation");
//            alert.setHeaderText(null);
//            alert.setContentText("Your reservation was canceled as personal details and payment were not provided within 15 minutes. You can start over anytime!");
//            alert.getButtonTypes().setAll(ButtonType.OK);
//            Optional<ButtonType> result = alert.showAndWait();
//            // on OK
//            if (result.isPresent() && result.get() == ButtonType.OK) {
//                performAdditionalAction();
//            };
//        });
//
//    }

    @Subscribe
    public void onUpdateBranchTablesEvent(UpdateBranchTablesEvent event) {
        System.out.println("in onUpdateBranchTables");
        ResInfo newReservation = event.getReservation();
        if (newReservation==null)
        {
            System.out.println("reservation is null");
        }

        //update this page if this branch and the new reservation's branch are the same
        if(newReservation.getBranch().getBranchID()==this.branch.getBranchID())
        {
            System.out.println("in onUpdateBranchTables if");
            updatePage(newReservation);
        }
    }
    private void updatePage(ResInfo reservation)
    {
        SimpleClient.getClient().resInfo.setBranch(reservation.getBranch()); //update the branch in reservation to the updated branch
        this.branch=reservation.getBranch(); //update this branch to the updated branch
        updateAvailableTimesAndUI();
    }
    private void setStyle() {
        root.setStyle("-fx-background-color: #fbe9d0;");
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Button)
            {
                node.setStyle("-fx-background-color: #8a6f48;\n" +
                        "    -fx-text-fill: white;");
            }
        }
        titleLabel.setStyle("-fx-font-size: 18px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: #6c5339;\n" +
                "    -fx-padding: 10px 0;\n" +
                "    -fx-font-family: \"Serif\";");
    }

}
