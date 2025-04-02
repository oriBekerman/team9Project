package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ClientAddedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.ReqCategory;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.RequestType;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Platform;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

public class ClientConnectingBoundary {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;


    @FXML
    private Label ErrorLabel;


    private SimpleClient client = SimpleClient.getClient();

    @FXML
    public void initialize() {
        assert hostField != null : "fx:id=\"hostField\" was not injected: check your FXML file 'ClientConnectingBoundary.fxml'.";
        assert portField != null : "fx:id=\"portField\" was not injected: check your FXML file 'ClientConnectingBoundary.fxml'.";
        assert ErrorLabel != null : "fx:id=\"ErrorLabel\" was not injected: check your FXML file 'ClientConnectingBoundary.fxml'.";

        EventBus.getDefault().register(this);  // Register this class to listen for events
    }


    @Subscribe
    // Method to handle the event when a client is added
    public void onClientAdded(ClientAddedEvent event) {
        Platform.runLater(() -> {
            // Update the UI with the success message, or switch to another screen
            String message = event.getMessage();
            if ("Client added successfully".equals(message)) {
                App.switchScreen("Home Page");  // Switch to the home page after success
            } else {
                ErrorLabel.setText("Failed to connect to server: " + message);
            }
        });
    }

    public void handleConnect() {
        String host = hostField.getText().trim();
        String portStr = portField.getText().trim();

        if (host.isEmpty() || portStr.isEmpty()) {
            ErrorLabel.setText("Please enter both host and port.");
            return;
        }

        try {
            int port = Integer.parseInt(portStr);
            client.setHost(host);
            client.setPort(port);

            // Prepare the "add client" request data
            String clientInfo = host + ":" + port;

            // Create a request to send to the server with the ADD_CLIENT RequestType and CONNECTION category
            Request<String> request = new Request<>(
                    ReqCategory.CONNECTION,
                    RequestType.ADD_CLIENT,
                    clientInfo
            );

            // Send the request to the server
            client.openConnection();
            client.sendToServer(request);

        } catch (NumberFormatException e) {
            ErrorLabel.setText("Port must be a number.");
        } catch (IOException e) {
            ErrorLabel.setText("Failed to connect to the server.");
            e.printStackTrace();
        }
    }

}
