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
import javafx.util.Pair;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.BRANCH;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.RESERVATION;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.ADD_RESERVATION;
import static java.lang.Math.min;

public class TableMapBoundary {

    public Pane outsideAreaPane;
    public Pane insideAreaPane;
    public Branch branch;
    public GridPane insideGridPane;
    public ComboBox<String> timesBox;
    public AnchorPane root;
    public Button backBtn;
    public Label reservationLabel;
    public GridPane outsideGridPane;
    public Button selectTablesBtn;
    public Button doneBtn;


    public boolean mapIsSet=false;
//    public Button In00Btn;
//    public Button In01Btn;
//    public Button In02Btn;
//    public Button In10Btn;
//    public Button In11Btn;
//    public Button In12Btn;
//    public Button Out00Btn;
//    public Button Out10Btn;
//    public Button Out01Btn;
//    public Button Out11Btn;
    //    public boolean mapIsUpdated=false;
    private boolean selectionEnabled = false;
    private List<Button> buttons=new ArrayList<>();
    private Map<Integer,RestTable>idMap=new HashMap<>();
    private Map<RestTable,Button>tablesMap=new HashMap<>();
    private Map<Button,RestTable>buttonsMap=new HashMap<>();
    private Set<Button> selectedButtons = new HashSet<>();
    private final Object tableSyncLock=new Object();


    public TableMapBoundary()
    {
        EventBus.getDefault().register(this);
        mapIsSet=false;
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
        if (branch == null) return;

        synchronized (this) {
            System.out.println("in setMap() - starting setup for branch: " + branch.getName());
            this.branch = branch;
            this.branch.tablesAreSet = false;

            waitForTables(branch);
            initializeUIAfterTablesAreReady(branch);
        }
    }
    //call loadBranch but wait until the tables are set in the branch to init the data in TableMap
    private void waitForTables(Branch branch) {
        System.out.println("Waiting for tables to be fetched...");

        loadBranchTables(); // Send request to server

        try {
            while (!branch.tablesAreSet) {
                wait();  // Wait until tablesAreSet becomes true (from EventBus)
            }
            System.out.println("Tables received!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted while waiting for tables.");
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

    //get the branch tables from the event client posted (wake setMap thread after wait for loadTables)
    @Subscribe
    public void onBranchTablesEvent(BranchTablesReceivedEvent event) {
        synchronized (this) {
            Set<RestTable> tables = event.getTables();
            List<RestTable> newTables=tables.stream().toList();
            branch.setRestTables(newTables);
            branch.tablesAreSet = true;

            System.out.println("Tables received! Notifying all waiting threads...");
            notifyAll();  // Wake up threads waiting for tables
        }
    }

    private void initializeUIAfterTablesAreReady(Branch branch) {
        System.out.println("Initializing UI with fetched tables...");

        List<RestTable> tableList = branch.getTablesSortedByID();

        buttons.clear();
        idMap.clear();
        tablesMap.clear();
        buttonsMap.clear();

        int insideColCount = 2;
        int outsideColCount = 2;

        int insideIndex = 0;
        int outsideIndex = 0;

        for (RestTable table : tableList) {
            Button button = new Button();
            int id = table.getId();

            setDefaultButton(button,id+","+table.getCapacity());

            buttons.add(button);
            idMap.put(id, table);
            tablesMap.put(table, button);
            buttonsMap.put(button, table);

            Pair<Integer, String> pair = new Pair<>(id, "");
            button.setUserData(pair);

            if ("inside".equalsIgnoreCase(table.getArea())) {
                int row = insideIndex / insideColCount;
                int col = insideIndex % insideColCount;
                insideGridPane.add(button, col, row);
                insideIndex++;
            } else if ("outside".equalsIgnoreCase(table.getArea())) {
                int row = outsideIndex / outsideColCount;
                int col = outsideIndex % outsideColCount;
                outsideGridPane.add(button, col, row);
                outsideIndex++;
                if (row >= 2) {
                    System.out.println("⚠ Too many outside tables for 2x2 grid!");
                }
            } else {
                System.out.println("Unknown area for table ID " + id + ": " + table.getArea());
            }
        }
        setTimesBox();
        setupStyles();
        reservationLabel.setVisible(false);
        mapIsSet = true;
        System.out.println("UI initialized, map is set.");
        notifyAll();
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
    private void setDefaultButton(Button button,String num)
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

    //user selected time from timesBox
    public void chooseTime(ActionEvent actionEvent) {
        System.out.println("in choose time");
        String chosen = timesBox.getSelectionModel().getSelectedItem();
        LocalTime localTime = LocalTime.parse(chosen);
        displayMapAt(localTime);
    }

    //given the chosen time set the map button according to the availability of the tables matching the button
    private void displayMapAt(LocalTime localTime) {
        Set<RestTable> branchAvailableTables = branch.getAvailableTablesAt(localTime);
        Set<RestTable>branchTables=branch.getTables();
        for(RestTable branchTable:branchTables)
        {
//            RestTable table=checkTableInMap(branchTable);
            Button btn=tablesMap.get(branchTable);
            if ((btn == null))
            {
                System.out.println("button is null -> table not found");
            }
            System.out.println(btn.getText());
            if(btn!=null)
            {
                if(branchAvailableTables.contains(branchTable))
                {
                    setTableButtonAvailable(btn);
                    System.out.println(btn.getText() + "is available");
                }
                else if(!(branchAvailableTables.contains(branchTable)))
                {
                    setTableButtonsUnavailable(btn);
                    System.out.println(btn.getText() + "is unavailable");
                }
                else {
                    System.out.println("table on in map or not in branch tables");
                    return;
                }
            }
        }
    }
    private void updatePage(ResInfo resInfo) {
        System.out.println("In update page");

        updateBranchReference(resInfo);
        remapTables();
        updateTableAvailabilityUI(resInfo);
    }

    private void updateBranchReference(ResInfo resInfo) {
        this.branch = resInfo.getBranch();
    }

    private void remapTables() {
        Set<RestTable> updatedTables = branch.getTables();
        Map<Integer, RestTable> updatedById = new HashMap<>();
        for (RestTable table : updatedTables) {
            updatedById.put(table.getId(), table);
        }

        for (Button button : buttons) {
            RestTable oldTable = buttonsMap.get(button);
            if (oldTable != null) {
                int id = oldTable.getId();
                RestTable newTable = updatedById.get(id);
                if (newTable != null) {
                    idMap.put(id, newTable);
                    tablesMap.put(newTable, button);
                    buttonsMap.put(button, newTable);
                }
            }
        }
    }

    private void updateTableAvailabilityUI(ResInfo resInfo) {
        String selected = timesBox.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        LocalTime selectedTime = LocalTime.parse(selected);
        if (!resInfo.getHours().equals(selectedTime)) return;

        for (RestTable t : resInfo.getTable()) {
            RestTable updated = idMap.get(t.getId());
            if (updated != null) {
                Button btn = tablesMap.get(updated);
                if (btn != null) {
                    if (resInfo.getIsCancelled()) {
                        setTableButtonAvailable(btn);
                        System.out.println("Marked table available (cancelled): " + btn.getText());
                    } else {
                        setTableButtonsUnavailable(btn);
                        System.out.println("Marked table unavailable: " + btn.getText());
                    }
                }
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
        Pair<Integer,String>pair= (Pair<Integer, String>) button.getUserData();
        Integer id=pair.getKey();
        pair = new Pair<>(id, "available");
        button.setUserData(pair);
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
        Pair<Integer,String>pair= (Pair<Integer, String>) button.getUserData();
        Integer id=pair.getKey();
        pair = new Pair<>(id, "unavailable");
        button.setUserData(pair);
    }

    //when return is clicked
    public void BackToBranch(ActionEvent actionEvent) {
        openBranchPage(branch);

    }

    //open  branch page after return
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

    // Triggered when a reservation is made or updated, indicating that the availability of tables has changed
    @Subscribe
    public void onUpdatesBranchTablesEvent(UpdateBranchTablesEvent event) {
        ResInfo reservation = event.getReservation();
        if (reservation == null) return;
        Branch updatedBranch = reservation.getBranch();
        if (!this.branch.getName().equals(updatedBranch.getName()))
            return;
        // Only replace branch if new one has updated tables
        if (updatedBranch.tablesAreSet) {
            this.branch = updatedBranch;
            System.out.println("Branch updated tableAreSet= " + updatedBranch.tablesAreSet);
        }
        updatePage(reservation);
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

    public void markSelected(MouseEvent mouseEvent) {
    }
    //set selection mode
    public void enableSelection(ActionEvent actionEvent) {
        selectedButtons.clear();
        selectionEnabled = true;
        for (Button button : tablesMap.values()) {
            Pair<Integer, String> pair = (Pair<Integer, String>) button.getUserData();
            if ("available".equals(pair.getValue())) {
                button.setDisable(false);
                button.setOnAction(this::handleSelectionClick);  // attach listener
            } else {
                button.setDisable(true); // prevent clicking unavailable tables
            }
        }
        reservationLabel.setText("Selection mode enabled. Click tables to select.");

    }


    public void disableSelection() {
        selectionEnabled = false;
        for (Button button : tablesMap.values()) {
            Pair<Integer, String> pair = (Pair<Integer, String>) button.getUserData();
            if ("unavailable".equals(pair.getValue())) {
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
        for(Button button: selectedButtons)
        {
            setTableButtonsUnavailable(button);
        }
        disableSelection();
    }
    private void reserveTables(LocalTime time)
    {
        System.out.println("reserve tables");
        int numOfGuests=0;
        Set<RestTable> tables=new HashSet<>();
        for(Button button: selectedButtons)
        {
            RestTable t=buttonsMap.get(button);
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
        updatePage(reservation);
    }
    private void setupStyles() {
        root.setStyle("-fx-background-color: #fbe9d0;");
        outsideAreaPane.setStyle("-fx-background-color: #f6d7b0;\n" +
                "-fx-border-color: #8a6f48;\n" +
                "-fx-border-width: 2px;\n" +
                "-fx-border-radius: 8px;\n" +
                "-fx-padding: 12px;");
        insideAreaPane.setStyle("-fx-background-color: #e4c5a2;\n" +
                "-fx-border-color: #8a6f48;\n" +
                "-fx-border-width: 2px;\n" +
                "-fx-border-radius: 8px;\n" +
                "-fx-padding: 12px;");
    }
}
