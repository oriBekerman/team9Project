package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ReceivedAllComplaintsEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HandleComplaintTableBoundary {
    public AnchorPane root;
    public Label complaintsTitle;
    public Button returnBtn;
    public TableView<Complaint> complaintTable;
    public TableColumn<Complaint,String> numColumn;
    public TableColumn<Complaint, String> DateColumn;
    public TableColumn<Complaint,String> branchColumn;
    public TableColumn<Complaint,String> desColumn;
    public TableColumn<Complaint,String> statusColumn;
    public TableColumn<Complaint,String> customerNameColumn;
    private CustomerServiceEmployee employee;
    public boolean pageIsSet =false;
    private boolean complaintsAreSet=false;
    private List<Complaint> complaints=new ArrayList<>();

    public HandleComplaintTableBoundary() {

    }

    @FXML
    void initialize() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setColumns();
    }
    public void setPage()
    {
        System.out.println("in page is set");
        synchronized (this) {
           this.pageIsSet =false;
           this.complaintsAreSet=false;
           waitForComplaints();
            InitTableAfterAllComplaintEvent();
//            initializeUIAfterTablesAreReady(branch);
        }
    }
    private void waitForComplaints() {
        System.out.println("Waiting for complaints to be fetched...");

        SimpleClient.getClient().getAllComplaints(); // Send request to server

        try {
            while (!complaintsAreSet) {
                wait();  // Wait until tablesAreSet becomes true (from EventBus)
            }
            System.out.println("complaints received!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted while waiting for tables.");
        }
    }
    //set table columns
    private void setColumns()
    {
        numColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplaintId().toString()));

        DateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getComplaintDate().
                        format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        customerNameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCustomer().getName()));
        branchColumn.setCellValueFactory(cellData ->new SimpleStringProperty(cellData.getValue().getBranch().getName()));
        desColumn.setCellValueFactory(cellData ->new SimpleStringProperty(cellData.getValue().getComplaintText()));
        statusColumn.setCellValueFactory(cellData ->new SimpleStringProperty(cellData.getValue().getStatus().toString()));
    }

    public void openComplaintPage(MouseEvent mouseEvent) {
    }

    //set complaints with the complaints received from server
    @Subscribe
    public void onAllComplaintEvent(ReceivedAllComplaintsEvent event) {
        synchronized (this) {
            if(event.getComplaintList()==null)
            {
                System.out.println(event.getMessage());
                pageIsSet =true;
                notifyAll();
            }
            complaints=event.getComplaintList();
            complaintsAreSet=true;
            System.out.println("complaint received! Notifying all waiting threads...");
            notifyAll();  // Wake up threads waiting for tables
        }
    }
    //set the table with the complaints
    private void InitTableAfterAllComplaintEvent() {
        Platform.runLater(() -> {
            synchronized (this) {
                complaintTable.getItems().clear();
                complaintTable.getItems().setAll(complaints);
                setColumns();
                pageIsSet = true;
                System.out.println("Complaints  loaded");
                this.notifyAll();
            }
        });
    }


}
