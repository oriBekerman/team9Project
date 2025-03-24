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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.time.Duration;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

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
    private Label cancelFee;

    @FXML
    private TextField orderNumberText;

    private Delivery currentDelivery;
    private double refund = 0.0;

    @FXML
    void navToHP(ActionEvent event) {
        onExit(); // Unregister before switching
        switchScreen("Home Page");
    }

    @FXML
    void FindDelivery(ActionEvent event) throws IOException {

        ErrorText.setText("");
        DeliveryDisplay.getChildren().clear();

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

            // check if email and order number allains
            if(emailText.getText().equals(currentDelivery.getCustomer().getEmail())) {
                Platform.runLater(this::displayDelivery);
            }
            else{
                Platform.runLater(() -> {
                    ErrorText.setText("Order does not match the email");
                });
            }
        } else {
            Platform.runLater(() -> {
                ErrorText.setText("Order not exist");
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

        checkRewards();
        String deliveryNum = orderNumberText.getText();
        // Send request to the server to delete the delivery
        Request<Integer> deleteDeliveryRequest = new Request<>(
                ReqCategory.DELIVERY,
                RequestType.CANCEL_DELIVERY,
                Integer.parseInt(deliveryNum.trim())
        );
        SimpleClient.getClient().sendToServer(deleteDeliveryRequest);
    }

    @FXML
    void checkRewards() throws IOException {
        if (currentDelivery == null) {
            System.out.println("No delivery found.");
            refund = -1;
        }

        String deliveryTimeStr = currentDelivery.getTime(); // Example: "10:30"
        double cal_refund = 0.0;

        // Parse the delivery time string into LocalTime
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime deliveryTime = LocalTime.parse(deliveryTimeStr, formatter);

        // Get the current time
        LocalTime now = LocalTime.now();

        // Calculate the duration between now and the delivery time
        Duration duration = Duration.between(now, deliveryTime);
        long minutesUntilDelivery = duration.toMinutes();

        System.out.println("Minutes until delivery: " + minutesUntilDelivery);

        // Cancel before 3 hours (180 minutes)  Full refund
        if (minutesUntilDelivery >= 180) {
            cal_refund = currentDelivery.getTotalPrice();
        }
        // Cancel between 3 hours (180 min) and 1 hour (60 min)  50% refund
        else if (minutesUntilDelivery >= 60) {
            cal_refund = currentDelivery.getTotalPrice() / 2;
        }
        // Cancel within 1 hour  No refund
        else {
            cal_refund = 0.0;
        }

        System.out.println("Refund amount: " + refund);
        refund= cal_refund;
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
                DeliveryDisplay.getChildren().add(new Label("Order canceled, your refound is "+ refund));
                cancelBtn.setDisable(true);

                // Send email confirmation
                sendCancellationEmail(currentDelivery.getCustomer().getEmail(), refund);
            });
        }
    }

    private void sendCancellationEmail(String customerEmail, double refund) {
        String host = "smtp.gmail.com"; // Gmail's SMTP server
        final String from = "galalpert5@gmail.com"; // Sender's email
        final String password = "jmqf srzs ruqx yayg"; // Sender's email password

        // Set up properties for the SMTP server
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "465");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");

        // Create a session
        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create a MimeMessage
            MimeMessage message = new MimeMessage(session);

            // Set sender's email
            message.setFrom(new InternetAddress(from));

            // Set recipient's email
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(customerEmail));

            // Set subject of the email
            message.setSubject("Order Canceled and Refund Processed");

            // Set email body content
            String content = "Dear Customer,\n\n" +
                    "We are writing to confirm that your order has been successfully canceled. " +
                    "The refund amount of " + refund + " has been processed.\n\n" +
                    "Thank you for using our service!\n\n" +
                    "Best regards,\n" +
                    "Mama's kitchen";

            message.setText(content);

            // Send the email
            Transport.send(message);
            System.out.println("Email sent successfully to " + customerEmail);
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    @Subscribe
    public void onDeliveryNotFound(String responseMessage) {
        if ("delivery not found".equals(responseMessage)) {
            Platform.runLater(() -> {
                ErrorText.setText("");
                DeliveryDisplay.getChildren().clear();
                ErrorText.setText("delivery not found");
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
        assert cancelFee != null : "fx:id=\"ErrorText\" was not injected: check your FXML file 'cancelDelivery.fxml'.";

        cancelFee.setText("Delivery Cancellation Fees: \n Up to 3 hours - full refund \n 3-1 hours - 50% refund \n 1 hour or less - no refund");
        cancelFee.setWrapText(true); // Optional: wrap text in the label if the content overflows


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
        } else {
            ErrorText.setText("delivery not found");
        }
    }

    public void onExit() {
        EventBus.getDefault().unregister(this);
        System.out.println("Unregistered from EventBus: CancelDeliveryBoundary");
    }

}
