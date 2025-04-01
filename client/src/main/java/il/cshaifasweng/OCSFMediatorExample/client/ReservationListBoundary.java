package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.RESERVATION;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.CANCEL_RESERVATION;

public class ReservationListBoundary {

    public Label pageTitle;
    public AnchorPane root;
    @FXML private TableView<ResInfo> reservationsTable;
    @FXML private TableColumn<ResInfo, Integer> idCol;
    @FXML private TableColumn<ResInfo, String> branchCol;
    @FXML private TableColumn<ResInfo, String> timeCol;
    @FXML private TableColumn<ResInfo, Integer> guestsCol;
    @FXML private TableColumn<ResInfo, String> statusCol;

    public Boolean isSet=false;
    public ReservationListBoundary() {}

    @FXML
    void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("resID"));
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("hours"));
        guestsCol.setCellValueFactory(new PropertyValueFactory<>("numOfGuests"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        setStyle();
        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }

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
        Request request = new Request(RESERVATION,CANCEL_RESERVATION, selected.getResID());
        SimpleClient.getClient().sendToServer(request);
    }

    @Subscribe
    public void responseCancel(ReservationCancelledEvent event) throws IOException {
        Platform.runLater(() -> {
            ResInfo selected = reservationsTable.getSelectionModel().getSelectedItem();

            if (selected != null) {
                reservationsTable.getItems().remove(selected);

                // Calculate penalty
                int penalty = 0;
                LocalTime now = LocalTime.now();
                LocalTime reservationTime = selected.getHours();
                long minutesBetween = java.time.Duration.between(now, reservationTime).toMinutes();

                if (minutesBetween < 60) {
                    penalty = selected.getNumOfGuests() * 10;
                }

                // Build message
                StringBuilder message = new StringBuilder();
                message.append("Reservation:\nfor ")
                        .append(selected.getBranch().getName())
                        .append("\nat ").append(selected.getHours())
                        .append("\nhas been cancelled.");

                if (penalty > 0) {
                    message.append("\nA cancellation fee of ")
                            .append(penalty).append("₪ applies (10₪ per guest).");
                } else {
                    message.append("\nNo cancellation fee applies.");
                }

                // Show alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText(message.toString());
                alert.getButtonTypes().setAll(ButtonType.OK);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    performAdditionalAction();
                }
            } else {
                showAlert("Could not find the selected reservation. It may have already been removed.");
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
    private void setStyle()
    {
        root.setStyle("-fx-background-color: #fbe9d0;");
        for (Node node : root.getChildrenUnmodifiable())
        {
            if(node instanceof Button)
            {
                node.setStyle(" -fx-font-size: 16px;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-text-fill: white;\n" +
                        "    -fx-background-color: #8a6f48;\n" +
                        "    -fx-alignment: center;\n" +
                        "    -fx-padding: 8px 16px;\n" +
                        "    -fx-border-radius: 6px;\n" +
                        "    -fx-cursor: hand;");
            }
            if (node instanceof TableColumnHeader)
            {
                node.setStyle(" -fx-font-size: 16px;\n" +
                        "    -fx-text-fill: #4e453c;\n" +
                        "    -fx-padding: 8px;\n" +
                        "    -fx-font-weight: bold;;");
            }
        }
    }




}