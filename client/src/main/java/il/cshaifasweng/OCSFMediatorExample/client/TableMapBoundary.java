package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchTablesReceivedEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.UpdateBranchTablesEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.RESERVATION;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.ADD_RESERVATION;
import static java.lang.Math.min;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Button tableBtn1;
    public Branch branch;
    public boolean mapIsSet=false;
    public GridPane insideGridPane;
    public Button tableBtn2;
    public Button tableBtn3;
    public ComboBox<String> timesBox;
    public Button tableBtn4;
    public Button tableBtn5;
    public Button tableBtn6;
    public AnchorPane root;
    public Button backBtn;
    public Button tableBtn7;
    public Button tableBtn9;
    public Button tableBtn8;
    public Button tableBtn10;
    public Label reservationLabel;
    public GridPane outsideGridPane;
    public Button selectTablesBtn;
    public Button doneBtn;
    private boolean selectionEnabled = false;
    private List<Button> buttons=new ArrayList<>();
    private Map<Integer,RestTable>idMap=new HashMap<>();
    private BiMap<RestTable,Button>biMap=new BiMap<RestTable,Button>();
    private Set<Button> selectedButtons = new HashSet<>();




    public TableMapBoundary()
    {
        EventBus.getDefault().register(this);
    }
    public void initialize() {
        if (branch != null) {
//            buttons=insideGridPane.getChildren();
//           setMap(branch);
        }
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
    }
    // initialize the map before letting the map page be opened
    public void setMap(Branch branch) {
        System.out.println("in set map before sync");
        synchronized (this) {
            if (branch != null)
            {
                System.out.println("in set map after sync");
                this.branch = branch;
                this.branch.tablesAreSet=false;
                System.out.println("after branch = " + this.branch);
                System.out.println("branch tables = " + String.valueOf(this.branch.tablesAreSet));
                System.out.println("Fetching tables for branch...");
                loadBranchTables();
                System.out.println("after load ...");
                try
                {
                    while (!branch.tablesAreSet) //wait for branch tables to be set in branch entity
                    {
                        wait();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread interrupted while waiting for tables.");
                    return;
                }
                // once the list of tables is loaded in branch make table buttons
                System.out.println("in set map after if branchTables:");
                System.out.println("map is set  = " + String.valueOf(mapIsSet));
                Set<RestTable> tables = branch.getTables(); // Assume this fetches the list of tables
                buttons.add(tableBtn1);
                buttons.add(tableBtn2);
                buttons.add(tableBtn3);
                buttons.add(tableBtn4);
                buttons.add(tableBtn5);
                buttons.add(tableBtn6);
                buttons.add(tableBtn7);
                buttons.add(tableBtn9);
                buttons.add(tableBtn8);
                buttons.add(tableBtn10);
                List<RestTable> tableList = new ArrayList<>(tables);// Convert Set to List
                reservationLabel.setVisible(false);
//                List<RestTable> outTables=new ArrayList<>();
//                List<RestTable> inTables=new ArrayList<>();
//                for (RestTable t:tableList)
//                {
//                    if(t.getArea().equals("outside"))
//                    {
//                        outTables.add(t);
//                    }
//                    else
//                    {
//                        inTables.add(t);
//                    }
//                }
                for (int i = 0; i < Math.min(tableList.size(), buttons.size()); i++) {
//                    String num = String.valueOf(tableList.get(i).getId());
//                    map.put(tableList.get(i), buttons.get(i));
                    idMap.put(tableList.get(i).getId(), tableList.get(i));
                    biMap.put(tableList.get(i), buttons.get(i));
                    setButton(buttons.get(i), String.valueOf(i));
                }
                setTimesBox();
                root.setStyle("-fx-background-color: #fbe9d0;");
                outsideAreaPane.setStyle("-fx-background-color: #f6d7b0;\n" +
                        "    -fx-border-color: #8a6f48;\n" +
                        "    -fx-border-width: 2px;\n" +
                        "    -fx-border-radius: 8px;\n" +
                        "    -fx-padding: 12px;");
                insideAreaPane.setStyle(" -fx-background-color: #e4c5a2;\n" +
                        "    -fx-border-color: #8a6f48;\n" +
                        "    -fx-border-width: 2px;\n" +
                        "    -fx-border-radius: 8px;\n" +
                        "    -fx-padding: 12px;");
                this.mapIsSet = true;
                System.out.println("Map is set");
                // Notify all waiting threads in openBranchMap()
                notifyAll();
            }
        }
    }

    private void setTimesBox() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm"); //good for both 09:00 and 9:00
            LocalTime startTime = LocalTime.parse(branch.getOpeningTime(), formatter);
            LocalTime endTime = LocalTime.parse(branch.getClosingTime(), formatter);
            while (startTime.isBefore(endTime)) {//add to comboBox every 15 min
                timesBox.getItems().add(startTime.toString());
                startTime = startTime.plusMinutes(15);
            }
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing time: " + e.getMessage());
        }
    }

    //get branch tables from server
    private void loadBranchTables()
    {
        if (branch != null) {
            try {
                System.out.println("in load branches before fetch...");
                SimpleClient.getClient().fetchTables(branch);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void setButton(Button button,String num)
    {
        button.setText(num);
        button.setWrapText(true);
        button.setStyle(" -fx-font-size: 16px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: #8a6f48;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 8px 16px;\n" +
                "    -fx-border-radius: 6px;\n" +
                "    -fx-cursor: hand;");
    }

    //get the branch tables from the event client posted
    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (this) {
            Set<RestTable> tables = event.getTables();
            branch.setRestTables(tables);
            branch.tablesAreSet = true;

            System.out.println("Tables received! Notifying all waiting threads...");
            notifyAll();  // Wake up threads waiting for tables
        }
    }

    public void chooseTime(ActionEvent actionEvent) {
        String chosen = timesBox.getSelectionModel().getSelectedItem();
        LocalTime localTime = LocalTime.parse(chosen);
        displayMapAt(localTime);
    }

    private void displayMapAt(LocalTime localTime) {
        Set<RestTable> tables = branch.getAvailableTablesAt(localTime);
        for (RestTable table: tables) {
            setTableButtonAvailable(biMap.getValue(table));
        }
        for(RestTable t: branch.getTables())
        {
            if(!tables.contains(t))
            {
                setTableButtonsUnavailable(biMap.getValue(t));
            }
        }
    }
    private void setTableButtonAvailable(Button button)
    {
        button.setStyle(" -fx-font-size: 16px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: #5e8a75;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 8px 16px;\n" +
                "    -fx-border-radius: 6px;\n" +
                "    -fx-cursor: hand;");
        button.setUserData("available");
    }
    private void setTableButtonsUnavailable(Button button) {
        button.setStyle(" -fx-font-size: 16px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: #8a6f48;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 8px 16px;\n" +
                "    -fx-border-radius: 6px;\n" +
                "    -fx-cursor: hand;");
        button.setUserData("unavailable");
    }

    public void BackToBranch(ActionEvent actionEvent) {
        openBranchPage(branch);

    }

    //open selected branch page
    private void openBranchPage(Branch branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
            Parent branchPageRoot = loader.load();
            // Get the controller and pass the branch
            BranchPageBoundary controller = loader.getController();
            controller.setBranch(branch);
            if (controller.branchIsSet) {
                System.out.println("branch is already set");
            }
            while (!controller.branchIsSet) {
                System.out.println("Waiting for branch to be set");
            }
            App.setContent(branchPageRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Subscribe
    public void onUpdatesBranchTablesEvent(UpdateBranchTablesEvent event)
    {
        ResInfo reservation=event.getReservation();
        if(reservation==null) {
            return;
        }
        if(this.branch.getName().equals(reservation.getBranch().getName()))
        {
            updatePage(reservation);
        }


    }
    private void updatePage(ResInfo resInfo) {
        this.branch=resInfo.getBranch();
        if (resInfo.getHours().equals(timesBox.getSelectionModel().getSelectedItem()))
        {
            for(RestTable t: resInfo.getTable())
            {
                RestTable oldTable=idMap.get(t.getId());
                Button button=biMap.getValue(oldTable);
                biMap.removeByKey(oldTable);
                biMap.put(t,button);
            }
        }
        setMap(branch);
    }

    public void tableBtnAction(ActionEvent actionEvent) {
//        LocalTime time=LocalTime.parse(timesBox.getSelectionModel().getSelectedItem());
//        Button bt=(Button)actionEvent.getSource();
//        if(temp.getUserData().equals("unavailable"))
//        {
////            openReservationDetails((Button)actionEvent.getSource(),time);
//        }
        Button bt=(Button)actionEvent.getSource();
        selectedButtons.add(bt);
    }
//    private void openReservationDetails(Button button,LocalTime time) {
//        RestTable t=biMap.getKey(button);
//        System.out.println("reser boundary teble id: "+t.getId());
//        ResInfo res=branch.getReservationByTable(t,time);
//        if(res==null)
//        {
//            System.out.println("reservation not found");
//        }
////        reservationLabel.setText("reservation:" +
////                "name -"+res.getCustomer().getName());
////        showTemporarily(reservationLabel,60);
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Table Reservation:");
//            alert.setHeaderText(null);
//            alert.setContentText("Name: " + res.getCustomer().getName());
//            alert.getButtonTypes().setAll(ButtonType.CLOSE);
//
//            alert.show(); // Non-blocking
//
//            // Close alert after 30 seconds if not manually closed
//            PauseTransition delay = new PauseTransition(Duration.seconds(30));
//            delay.setOnFinished(event -> {
//                // Check if the alert is still showing before closing
//                if (alert.isShowing()) {
//                    alert.close();
//                }
//            });
//            delay.play();
//        });
//
//    }
    public void showTemporarily(Node node, double seconds) {
        node.setVisible(true); // Show the node

        PauseTransition pause = new PauseTransition(Duration.seconds(seconds));
        pause.setOnFinished(event -> node.setVisible(false)); // Hide after duration
        pause.play();
    }

    public void reserveTable(RestTable table,LocalTime time)
    {}

    public void markSelected(MouseEvent mouseEvent) {
    }

    //set selection mode
    public void enableSelection(ActionEvent actionEvent) {
        selectedButtons.clear();
        selectionEnabled = true;

        for (Button button : biMap.values()) {
            if ("available".equals(button.getUserData())) {
                button.setDisable(false);
                button.setOnAction(this::handleSelectionClick);  // attach listener
            } else {
                button.setDisable(true); // prevent clicking unavailable tables
            }
        }
        reservationLabel.setText("Selection mode enabled. Click tables to select.");
//        showTemporarily(reservationLabel, 4);
    }

    public void disableSelection() {
        selectionEnabled = false;
        for (Button button : biMap.values()) {
            if ("unavailable".equals(button.getUserData())) {
                button.setDisable(false);
            }
        }
//        showTemporarily(reservationLabel, 4);
    }
    //get all the table buttons that were clicked when on selection mode
    @FXML
    private void handleSelectionClick(ActionEvent event) {
        if (!selectionEnabled) return;

        Button button = (Button) event.getSource();

        if (selectedButtons.contains(button)) {
            selectedButtons.remove(button);
            setTableButtonAvailable(button);  // reset to available style
        } else {
            selectedButtons.add(button);
            highlightSelectedButton(button);  // visually mark selected
        }
    }
    private void highlightSelectedButton(Button button) {
        button.setStyle(" -fx-font-size: 16px;\n" +
                "    -fx-font-weight: bold;\n" +
                "    -fx-text-fill: white;\n" +
                "    -fx-background-color: #506037;\n" +
                "    -fx-alignment: center;\n" +
                "    -fx-padding: 8px 16px;\n" +
                "    -fx-border-radius: 6px;\n" +
                "    -fx-cursor: hand;");
    }

    public void sendSelection(ActionEvent actionEvent) {
        for(Button button: selectedButtons)
        {
            System.out.println(button.getText());
        }
        LocalTime time =LocalTime.parse(timesBox.getSelectionModel().getSelectedItem());
        reserveTables(time);
        disableSelection();


    }
    private void reserveTables(LocalTime time)
    {
        System.out.println("reserve tables");
        int numOfGuests=0;
        Set<RestTable> tables=new HashSet<>();
        for(Button button: selectedButtons)
        {
            RestTable t=biMap.getKey(button);
            tables.add(t);
            numOfGuests+=t.getCapacity();
            System.out.println("reserve tables get table: "+t.getId() );
        }
        Customer customer=new Customer("","","","","","","");
        ResInfo reservation=new ResInfo(branch,customer,time,numOfGuests,"",tables);
        Request request=new Request<>(RESERVATION,ADD_RESERVATION,reservation);
        try
        {
            SimpleClient.getClient().sendToServer(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
