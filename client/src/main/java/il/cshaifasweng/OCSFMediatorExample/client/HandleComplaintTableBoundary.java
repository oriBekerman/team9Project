package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ReceivedAllComplaintsEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.event.ActionEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.BRANCH;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.COMPLAINT;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.*;

public class HandleComplaintTableBoundary {
    public AnchorPane root;
    public Label complaintsTitle;
    public Button returnBtn;
    public TableView<Complaint> complaintTable;
    public TableColumn<Complaint, String> numColumn;
    public TableColumn<Complaint, String> dateColumn;
    public TableColumn<Complaint, String> branchColumn;
    public TableColumn<Complaint, String> descriptionColumn;
    public TableColumn<Complaint, String> statusColumn;
    public TableColumn<Complaint, String> customerNameColumn;
    public TableColumn<Complaint, String> compensationColumn;

    @FXML
    private Button BackBTN;

    private CustomerServiceEmployee employee;
    public boolean pageIsSet = false;
    private boolean complaintsAreSet = false;
    private List<Complaint> complaints = new ArrayList<>();

    public HandleComplaintTableBoundary() {}

    @FXML
    void initialize() {
        System.out.println("Initializing HandleComplaintTableBoundary...");


        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        setColumns();
        try {
            SimpleClient.getClient().getAllComplaints();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to request complaints from server.");
        }


        complaintTable.setOnMouseClicked(this::openComplaintPage);
    }
    public void setPage() {
        System.out.println("Page is being set...");
        synchronized (this) {
            this.pageIsSet = false;
            this.complaintsAreSet = false;
            waitForComplaints();
            InitTableAfterAllComplaintEvent();
        }
    }

    private void waitForComplaints() {
        System.out.println("Waiting for complaints from the server...");
        SimpleClient.getClient().getAllComplaints();

        try {
            while (!complaintsAreSet) {
                wait();
            }
            System.out.println("Complaints received from the server!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted while waiting for complaints.");
        }
    }

    private void setColumns() {
        numColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplaintId().toString()));

        dateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplaintDate()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        customerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomer().getName()));

        branchColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBranch().getName()));

        descriptionColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplaintText()));

        statusColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));

        compensationColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getStatus() == ComplaintStatus.RESOLVED_WITH_COMPENSATION
                                ? String.valueOf(cellData.getValue().getCompensation())
                                : "-"
                ));
    }

    public void openComplaintPage(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            Complaint selectedComplaint = complaintTable.getSelectionModel().getSelectedItem();
            if (selectedComplaint != null) {
                showEditComplaintDialog(selectedComplaint);
            }
        }
    }

    private void showEditComplaintDialog(Complaint complaint) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Edit Complaint");

        Label statusLabel = new Label("Status:");
        ComboBox<ComplaintStatus> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll(ComplaintStatus.values());
        statusComboBox.setValue(complaint.getStatus());

        Label compensationLabel = new Label("Compensation:");
        TextField compensationField = new TextField();
        compensationField.setPromptText("Enter compensation amount");
        compensationField.setDisable(complaint.getStatus() != ComplaintStatus.RESOLVED_WITH_COMPENSATION);

        if (complaint.getStatus() == ComplaintStatus.RESOLVED_WITH_COMPENSATION) {
            compensationField.setText(String.valueOf(complaint.getCompensation()));
        }

        statusComboBox.setOnAction(e -> {
            boolean isComp = statusComboBox.getValue() == ComplaintStatus.RESOLVED_WITH_COMPENSATION;
            compensationField.setDisable(!isComp);
            if (!isComp) compensationField.clear();
        });

        VBox content = new VBox(10, statusLabel, statusComboBox, compensationLabel, compensationField);
        dialog.getDialogPane().setContent(content);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {
                complaint.setStatus(statusComboBox.getValue());
                if (statusComboBox.getValue() == ComplaintStatus.RESOLVED_WITH_COMPENSATION) {
                    try {
                        long comp = Long.parseLong(compensationField.getText());
                        complaint.setCompensation(comp);
                    } catch (NumberFormatException e) {
                        showError("Invalid compensation amount. Please enter a number.");
                        return null;
                    }
                } else {
                    complaint.setCompensation(0);
                }
                complaintTable.refresh();

                try {
                    Request<List<Complaint>> request = new Request<>(COMPLAINT,HANDLE_COMPLAINT_TABLE,complaints );
                    SimpleClient.getClient().sendToServer(request);
                } catch (Exception e) {
                    showError("Failed to send updated complaint to server.");
                    e.printStackTrace();
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Subscribe
    public void onAllComplaintEvent(ReceivedAllComplaintsEvent event) {
        synchronized (this) {
            if (event.getComplaintList() == null) {
                System.out.println(event.getMessage());
                pageIsSet = true;
                notifyAll();
                return;
            }
            complaints = event.getComplaintList();
            complaintsAreSet = true;
            System.out.println("Complaints received! Notifying all waiting threads...");
            notifyAll();
            InitTableAfterAllComplaintEvent();
        }
    }

    private void InitTableAfterAllComplaintEvent() {
        Platform.runLater(() -> {
            synchronized (this) {
                complaintTable.getItems().clear();
                complaintTable.getItems().setAll(complaints);
                setColumns();
                pageIsSet = true;
                System.out.println("Complaints loaded into table.");
                this.notifyAll();
            }
        });
    }

    @FXML
    void OnBackAct(ActionEvent event) {
        EventBus.getDefault().unregister(this);
        switchScreen("Home Page");
    }
}
