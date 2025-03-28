package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.SentActiveReservationsEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.EventObject;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory.RESERVATION;
import static il.cshaifasweng.OCSFMediatorExample.entities.RequestType.GET_ACTIVE_RESERVATIONS;

public class EnterEmailController {

    @FXML private TextField emailField;
    @FXML private Button checkBtn;
    @FXML private Button backBtn;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
    }
    @FXML
    void handleCheckReservations() {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Email field cannot be empty");
            return;
        }
        Request request=new Request(RESERVATION,GET_ACTIVE_RESERVATIONS,null);
        try {
            SimpleClient.getClient().sendToServer(request);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void handleBack() {
        EventBus.getDefault().unregister(this);
        App.switchScreen("Reservation");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @Subscribe
    public void onReservationSentEvent(SentActiveReservationsEvent event)
    {
        openReservationListPage(event.getResInfos());
    }
    public void openReservationListPage(List<ResInfo> reservations)
    {
        System.out.println("in open reserv list");
        for(ResInfo resInfo:reservations)
        {
            if (resInfo.getIsCancelled()==true)
            {
                reservations.remove(resInfo);
            }
        }
        boolean hasReservations = reservations.stream()
                .anyMatch(res -> res.getCustomer() != null && res.getCustomer().getEmail().equals(emailField.getText().trim()));

        if (!hasReservations) {
            showAlert("No reservations found with this email.");
            System.out.println("in open reserv list no reservations");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reservationList.fxml"));
            Parent resListRoot = loader.load();
            // Get the controller and pass the branch
            ReservationListBoundary boundary = loader.getController();
            boundary.setPage(reservations,emailField.getText());
            synchronized (boundary)
            {
                while (!boundary.isSet) {
                    System.out.println("Waiting for page to be set...");
                    boundary.wait();  // Waits until notifyAll() is called
                }
            }
            Platform.runLater(() -> {
                try {
                    App.setContent(resListRoot);
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
}
