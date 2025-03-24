package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.regex.Pattern;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToSummeryDelivery;

public class CancelDeliveryBoundary {

    public SimpleClient client;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private VBox DeliveryDisplay;

    @FXML
    private Label ErrorText;

    @FXML
    private Button cancelBtn;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailText;

    @FXML
    private Button getDelivery;

    @FXML
    private Label headline;

    @FXML
    private Label orderNumberLabel;

    @FXML
    private TextField orderNumberText;

    private Delivery currentDelivery;

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void FindDelivery(ActionEvent event) throws IOException {

        String deliveryNum = orderNumberText.getText();
        String email = emailText.getText();

        //check valid input
        if(CheckDeliveryNum(deliveryNum) && isValidEmail(email)){

            // Send request to the server to find the delivery
            Request<Integer> findDeliveryRequest = new Request<>(
                    ReqCategory.DELIVERY,
                    RequestType.GET_DELIVERY,
                    Integer.parseInt(deliveryNum.trim())
            );

            SimpleClient.getClient().sendToServer(findDeliveryRequest);

        }

    }

    @Subscribe
    public void onDeliveryReceived(Delivery delivery) {
        if (delivery != null) {
            System.out.println("Received Delivery: " + delivery);
            currentDelivery = delivery;  // Update the current delivery object

            // Ensure UI updates happen on the JavaFX thread
            Platform.runLater(this::displayDelivery);
        } else {
            Platform.runLater(() -> {
                System.out.println("here");
                DeliveryDisplay.getChildren().add(new Label("Order not exist"));
            });
        }
    }


    private boolean CheckDeliveryNum(String order) {
        try {
            int orderNumber = Integer.parseInt(order.trim()); // Try parsing to an integer
            if (orderNumber > 0) {
                orderNumberText.setStyle(""); // Reset style if valid
                ErrorText.setText(""); // Clear error message
                return true;
            } else {
                throw new NumberFormatException(); // Handle non-positive numbers
            }
        } catch (NumberFormatException e) {
            orderNumberText.setStyle("-fx-border-color: red;"); // Highlight field in red
            ErrorText.setText("Please enter a valid positive order number.");
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        if (Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(email).matches()) {
            ErrorText.setText(""); // Clear error message if valid
            return true;
        } else {
            ErrorText.setText("Please enter a valid email address.");
            return false;
        }
    }


    @FXML
    void cancelDelivery(ActionEvent event) throws IOException {
        String deliveryNum = orderNumberText.getText();
        // Send request to the server to delete the delivery
        Request<Integer> deleteDeliveryRequest = new Request<>(
                ReqCategory.DELIVERY,
                RequestType.CANCEL_DELIVERY,
                Integer.parseInt(deliveryNum.trim())
        );
        SimpleClient.getClient().sendToServer(deleteDeliveryRequest);
    }

    // In your EventBus subscriber class

    @Subscribe
    public void onDeliveryDeleted(String responseMessage) {
        if ("delivery deleted".equals(responseMessage)) {
            Platform.runLater(() -> {
                orderNumberText.clear();
                emailText.clear();
                ErrorText.setText("");
                DeliveryDisplay.getChildren().clear();
                DeliveryDisplay.getChildren().add(new Label("Order canceled"));
                cancelBtn.setDisable(true);
            });
        }
    }


    @FXML
    public void initialize() {
        assert headline != null : "fx:id=\"headline\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert orderNumberText != null : "fx:id=\"orderNumberText\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert emailText != null : "fx:id=\"emailText\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert orderNumberLabel != null : "fx:id=\"orderNumberLabel\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert emailLabel != null : "fx:id=\"emailLabel\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert cancelBtn != null : "fx:id=\"cancelBtn\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert getDelivery != null : "fx:id=\"getDelivery\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert DeliveryDisplay != null : "fx:id=\"DeliveryDisplay\" was not injected: check your FXML file 'cancelDelivery.fxml'.";
        assert ErrorText != null : "fx:id=\"ErrorText\" was not injected: check your FXML file 'cancelDelivery.fxml'.";

        //set default values
        cancelBtn.setDisable(true);
        // Register to EventBus
        EventBus.getDefault().register(this);
    }


    private void displayDelivery() {
        // Clear previous content
        DeliveryDisplay.getChildren().clear();

        // Ensure delivery data is not null
        if (currentDelivery != null && !currentDelivery.isCanceled()) {
            if(currentDelivery.getCustomer().getEmail().equals(emailText.getText())) {
                // Add order details as Labels to the VBox
                DeliveryDisplay.getChildren().add(new Label("Order Number: " + currentDelivery.getOrderNumber()));
                DeliveryDisplay.getChildren().add(new Label("Delivery Method: " + currentDelivery.getDeliveryMethod()));
                DeliveryDisplay.getChildren().add(new Label("Total Price: " + currentDelivery.getTotalPrice()));
                DeliveryDisplay.getChildren().add(new Label("Delivery/Pickup Time: " + currentDelivery.getTime()));
                DeliveryDisplay.getChildren().add(new Label("Order Items:"));

                // Display order items if they exist
                if (currentDelivery.getOrderItems() != null && !currentDelivery.getOrderItems().isEmpty()) {
                    for (OrderItem item : currentDelivery.getOrderItems()) {
                        MenuItem menuItem = item.getMenuItem();
                        String itemDetails = String.format(
                                "%s\n Ingredients: %s\n Price: %.2f\n Preferences: %s\n Quantity: %d",
                                menuItem.getName(),
                                menuItem.getIngredients(),
                                menuItem.getPrice(),
                                item.getPreferences(),
                                item.getQuantity()
                        );
                        Label itemLabel = new Label(itemDetails);
                        itemLabel.setStyle("-fx-padding: 5; -fx-border-color: black; -fx-border-width: 0 0 1 0;");
                        DeliveryDisplay.getChildren().add(itemLabel);

                        //enable the user to cancel order
                        cancelBtn.setDisable(false);
                    }
                } else {
                    DeliveryDisplay.getChildren().add(new Label("No items in the order."));
                }
            }
            else{
                DeliveryDisplay.getChildren().add(new Label("Delivery email or Order number is not correct."));
            }
        } else {
            DeliveryDisplay.getChildren().add(new Label("No delivery found."));
        }
    }

}
