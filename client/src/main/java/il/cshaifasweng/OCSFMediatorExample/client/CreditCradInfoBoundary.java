package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import il.cshaifasweng.OCSFMediatorExample.client.Events.CreditCardInfoSet;
import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class CreditCradInfoBoundary {

    public TextField cardNumText;
    public TextField expDateText;
    public TextField cvvText;
    public Label errorLabel;
    public AnchorPane root;
    public Label cardNumLabel;
    public Label expDateLabel;
    public Label cvvLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backBtn;

    @FXML
    private Button paymentBtn;
    private String type;
    public boolean typeIsSet=false;

    public CreditCradInfoBoundary() {}

    public CreditCradInfoBoundary(String type) {
        this.type = type;
        this.typeIsSet=true;
    }

    @FXML
    void backToPersonalD(ActionEvent event) {
        openPersonalDetailsPage();
    }

    @FXML
    void checkPayment(ActionEvent event) {
        String cardNum = cardNumText.getText();
        String expDate = expDateText.getText();
        String cvv = cvvText.getText();
        if(cardNum.isEmpty() || expDate.isEmpty() || cvv.isEmpty()) {
            errorLabel.setText("Please fill all the fields");
        }
        else if(!isValidCreditCard(cardNum))
        {
            errorLabel.setText("Invalid Card Number");
        }
        else if(!isValidExpDate(expDate))
        {
            errorLabel.setText("Invalid Expiry Date");
        }
        else if (!isValidCVV(cvv))
        {
            errorLabel.setText("Invalid CVV");
        }
        else {
            if(type=="reservation")
            {
                SimpleClient.getClient().mapReservation.put("cardNum", cardNum);
                SimpleClient.getClient().mapReservation.put("expDate", expDate);
                SimpleClient.getClient().mapReservation.put("cvv", cvv);
                Customer customer=SimpleClient.getClient().resInfo.getCustomer();
                customer.setCreditCardNumber(cardNum);
                customer.setCvv(cvv);
                customer.setExpirationDate(expDate);
                SimpleClient.getClient().resInfo.setCustomer(customer);

                // stop reservation timer since pay is pressed and reservation will be saved
                TimerManager.getInstance().cancelTimer("reservationTimeout");

                CreditCardInfoSet events = new CreditCardInfoSet();
                EventBus.getDefault().post(events);
            }

        }

    }

    @FXML
    void initialize() {
        assert backBtn != null : "fx:id=\"backBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        assert paymentBtn != null : "fx:id=\"paymentBtn\" was not injected: check your FXML file 'creditCardInfo.fxml'.";
        setStyle();

    }
    public void setType(String type) {
        System.out.println("in set type before sync");
        synchronized (this) {
            if (this.type == null || !this.type.equals(type)) {
                System.out.println("in type map after sync");
                this.type = type;
                this.typeIsSet = true;
                notifyAll();
            }
        }
    }


    //validate card number
    public boolean isValidCreditCard(String cardNumber) {
        if (!Pattern.compile("^[0-9]{13,19}$").matcher(cardNumber).matches()) {
            return false;
        }
        return luhnCheck(cardNumber);
    }
    //used to validate card number
    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
    public static boolean isValidCVV(String cvv) {
        return Pattern.compile("^[0-9]{3,4}$").matcher(cvv).matches();
    }

    //check expiration date is a valid date
    public static boolean isValidExpDate(String expDate) {
        if (!Pattern.compile("^(0[1-9]|1[0-2])/(\\d{2}|\\d{4})$").matcher(expDate).matches()) {
            return false;
        }
        return isNotExpired(expDate);
    }
    //check exp date has not passed
    private static boolean isNotExpired(String expDate) {
        try {
            DateTimeFormatter formatter = expDate.length() == 5 ? DateTimeFormatter.ofPattern("MM/yy")
                    : DateTimeFormatter.ofPattern("MM/yyyy");
            YearMonth cardExpiry = YearMonth.parse(expDate, formatter);
            return cardExpiry.isAfter(YearMonth.now()); // Expiration must be in the future
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    public void setFields()
    {
        if(SimpleClient.getClient().resInfo != null)
        {
           cardNumText.setText(SimpleClient.getClient().resInfo.getCustomer().getCreditCardNumber());
           expDateText.setText(SimpleClient.getClient().resInfo.getCustomer().getExpirationDate());
           cvvText.setText(SimpleClient.getClient().resInfo.getCustomer().getCvv());
        }
    }
    private void setStyle() {
        root.setStyle("-fx-background-color: #fbe9d0;");
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof Button)
            {
                node.setStyle("-fx-background-color: #8a6f48;\n" +
                        "    -fx-text-fill: white;");
            }
            if (node instanceof Label)
            {
                node.setStyle("-fx-font-size: 18px;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-text-fill: #6c5339;\n" +
                        "    -fx-padding: 10px 0;\n" +
                        "    -fx-font-family: \"Serif\";");
            }
        }

    }
    public void openPersonalDetailsPage()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("personalDetailsFilling.fxml"));
            Parent PersonalInfoPageRoot = loader.load();
            // Get the controller and set the type before waiting
            PersonalDetailsFillingBoundary boundary = loader.getController();
            boundary.setType("reservation");  // This should be set before waiting
            synchronized (boundary) {
                while (!boundary.typeIsSet) {
                    System.out.println("Waiting for type to be set...");
                    boundary.wait();
                }
            }
            Platform.runLater(() ->
            {
                try
                {
                    App.setContent(PersonalInfoPageRoot);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();  // Restore interrupted state
        }
    }


}