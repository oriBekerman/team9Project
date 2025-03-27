package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class ReservationListBoundary {

    @FXML private TableView<ResInfo> reservationsTable;
    @FXML private TableColumn<ResInfo, Integer> idCol;
    @FXML private TableColumn<ResInfo, String> branchCol;
    @FXML private TableColumn<ResInfo, String> timeCol;
    @FXML private TableColumn<ResInfo, Integer> guestsCol;
    @FXML private TableColumn<ResInfo, String> statusCol;

    public Boolean isSet=false;
    public ReservationListBoundary()
    {
        EventBus.getDefault().register(this);
    }

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("resID"));
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("hours"));
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numOfGuests"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    }

    public void setPage(List<ResInfo> reservations,String email)
    {
        synchronized (this) {
            initialize();
            loadReservations(reservations,email);
        }
    }
    //CHANGE HERE
    private void loadReservations(List<ResInfo> reservations,String email) {
        List<ResInfo> filtered = reservations.stream()
                .filter(res -> res.getCustomer() != null && !res.getIsCancelled() &&res.getCustomer().getEmail().equals(email))
                .collect(Collectors.toList());

        reservationsTable.setItems(FXCollections.observableList(filtered));
        isSet = true;
        notifyAll();
    }




    @FXML
    void handleCancel() throws IOException {
        ResInfo selected = reservationsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Please select a reservation to cancel.");
            return;
        }

        // send request to cancel
        Request request = new Request(ReqCategory.CANCEL_RESERVATION, selected.getResID());
        SimpleClient.getClient().sendToServer(request);
    }

    @Subscribe
    public void responseCancel(ReservationCancelledEvent event) throws IOException {
        ResInfo selected = reservationsTable.getSelectionModel().getSelectedItem();
        reservationsTable.getItems().remove(selected);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText(" Reservation:\n"+"for "+selected.getBranch().getName()+"\n"+"at "+selected.getHours()+"\n"+"has been cancelled");
            alert.getButtonTypes().setAll(ButtonType.OK);
            Optional<ButtonType> result = alert.showAndWait();
            // on OK
            if (result.isPresent() && result.get() == ButtonType.OK) {
                performAdditionalAction();
            }
        });
    }
    private void performAdditionalAction() {
        System.out.println("in preform addi");
        switchScreen("Home Page");
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void handleBack() {
        App.switchScreen("enterEmail");
        EventBus.getDefault().unregister(this);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}
