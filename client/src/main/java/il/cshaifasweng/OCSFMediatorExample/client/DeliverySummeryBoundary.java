package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class DeliverySummeryBoundary {

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Label orderText;  // This label will hold the full order summary text

    @FXML
    private Label headline;

    private Delivery currentDelivery;

    public void setDelivery(Delivery delivery) {
        this.currentDelivery = delivery;
        updateUI(); // Update the UI once the delivery is set
    }

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    public void initialize() {
        headline.setText("Order Summary");
        // Initially set the order text to "No delivery data available."
        orderText.setText("No delivery data available.");
    }

    private void updateUI() {
        // Update UI only if the delivery data is not null
        if (currentDelivery != null && !currentDelivery.isCanceled()) {
            // Create a StringBuilder to concatenate all the information
            StringBuilder orderDetails = new StringBuilder();

            orderDetails.append("Order number: ").append(currentDelivery.getOrderNumber()).append("\n");
            orderDetails.append("Delivery Method: ").append(currentDelivery.getDeliveryMethod()).append("\n");
            orderDetails.append("Total Price: ").append(currentDelivery.getTotalPrice()).append("\n");
            orderDetails.append("Delivery/Pickup Time: ").append(currentDelivery.getTime()).append("\n");
            orderDetails.append("Order Items:\n");

            // Display order items if they exist
            if (currentDelivery.getOrderItems() != null && !currentDelivery.getOrderItems().isEmpty()) {
                for (OrderItem item : currentDelivery.getOrderItems()) {
                    MenuItem menuItem = item.getMenuItem();
                    String text = menuItem.getName() + "\n Ingredients:"+menuItem.getIngredients()+"\n Price: "+ menuItem.getPrice()+"\n Preferences: "+ item.getPreferences()+"\n Quantity: "+ item.getQuantity();
                    orderDetails.append("- ").append(text).append("\n");
                }
            } else {
                orderDetails.append("No items in the order.");
            }

            // Set the full order details to the orderText label
            orderText.setText(orderDetails.toString());
        }
        else{
            orderText.setText("order not exist");
        }
    }
}
