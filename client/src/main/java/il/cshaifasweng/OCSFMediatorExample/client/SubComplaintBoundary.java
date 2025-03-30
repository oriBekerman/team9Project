package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintCustomerEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;
import static il.cshaifasweng.OCSFMediatorExample.client.App.switchToSummeryDelivery;

public class SubComplaintBoundary {
    @FXML
    private ComboBox<String> branchComboBox;

    @FXML
    private TextArea complaintTextArea;

    @FXML
    private Button continueBtn;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private TextField emailText;

    @FXML
    private Label errorMessage;

    @FXML
    private TextField nameText;

    @FXML
    private TextField phoneText;

    private Complaint currentComplaint = new Complaint();

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }


    @FXML
    void initialize() {
        assert branchComboBox != null : "fx:id=\"branchComboBox\" was not injected: check your FXML file.";
        assert continueBtn != null : "fx:id=\"continueBtn\" was not injected: check your FXML file.";
        assert nameText != null : "fx:id=\"nameText\" was not injected: check your FXML file.";
        assert emailText != null : "fx:id=\"emailText\" was not injected: check your FXML file.";
        assert phoneText != null : "fx:id=\"phoneText\" was not injected: check your FXML file.";
        assert complaintTextArea != null : "fx:id=\"complaintTextArea\" was not injected: check your FXML file.";
        assert errorMessage != null : "fx:id=\"errorMessage\" was not injected: check your FXML file.";

        setBranchesList();

        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
    }

    private void setBranchesList() {
        if (branchComboBox != null) {
            branchComboBox.getItems().addAll("Haifa", "Tel-Aviv", "Jerusalem", "Zikhron Ya'akov");
        } else {
            System.err.println("branchComboBox is null. Check FXML injection.");
        }
    }

    @FXML
    private void onContinue(ActionEvent event) throws IOException {

        if(validateFields()){
            getCustomer();

            //get branch
            // get the branch by name
            String branchName = branchComboBox.getValue();  // Get the selected branch
            Request<String> getBranchRequest = new Request<>(
                    ReqCategory.BRANCH,
                    RequestType.GET_BRANCH_BY_NAME,
                    branchName
            );

            SimpleClient.getClient().sendToServer(getBranchRequest);
        }

    }

    // Disable the continue button if any field is empty
    private boolean validateFields() {
        // Validate name
        if (nameText.getText().trim().isEmpty()) {
            errorMessage.setText("Name cannot be empty.");
            return false;
        }

        // Validate email
        String email = emailText.getText().trim();
        if (email.isEmpty()) {
            errorMessage.setText("Email cannot be empty.");
            return false;
        } else if (!isValidEmail(email)) {
            errorMessage.setText("Invalid email format.");
            return false;
        }

        // Validate phone
        String phone = phoneText.getText().trim();
        if (phone.isEmpty()) {
            errorMessage.setText("Phone number cannot be empty.");
            return false;
        } else if (!isValidPhone(phone)) {
            errorMessage.setText("Invalid phone number format.");
            return false;
        }

        // Validate complaint text
        if (complaintTextArea.getText().trim().isEmpty()) {
            errorMessage.setText("Complaint text cannot be empty.");
            return false;
        }

        // If all validations pass, clear any previous error message
        errorMessage.setText("");
        return true;
    }

    private boolean isValidEmail(String email) {
        return Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$").matcher(email).matches();
    }

    // Validate phone format using regex
    private boolean isValidPhone(String phone) {
        return Pattern.compile("^(\\+\\d{1,3})?\\d{10,15}$").matcher(phone).matches();
    }

    private boolean areFieldsEmpty() {
        return nameText.getText().trim().isEmpty() ||
                emailText.getText().trim().isEmpty() ||
                phoneText.getText().trim().isEmpty() ||
                complaintTextArea.getText().trim().isEmpty();
    }

    // Handle the complaint submission
    public void getCustomer() {
        if (areFieldsEmpty()) {
            errorMessage.setText("Please fill all the fields.");
            return; // Stop execution if any field is empty
        }

        try {
            // Proceed with complaint submission
            String name = nameText.getText();
            String email = emailText.getText();
            String phone = phoneText.getText();
            String message = complaintTextArea.getText();

            // Create a new Customer object
            Customer customer = new Customer(name, "", email, phone, "", "", "");
            currentComplaint.setCustomer(customer);
            currentComplaint.setComplaintText(message);

        } catch (Exception e) {
            errorMessage.setText("Error: " + e.getMessage());
        }
    }

    @Subscribe
    public void onBrunchReceived(BranchSentEvent event) throws IOException {
        Branch branch = event.getBranch();
        currentComplaint.setBranch(branch);

        //now we have all the complaint info and send a request to the server
        Request<Complaint> createComplaintRequest = new Request<>(
                ReqCategory.COMPLAINT,
                RequestType.SUBMIT_COMPLAINT,
                currentComplaint
        );

        // Send the request to the server
        SimpleClient.getClient().sendToServer(createComplaintRequest);
    }

    @Subscribe
    public void onComplaintCreated(ComplaintCreatedEvent event) {
        // Check if the complaint was created successfully or if an error occurred
        if (event.getComplaint() != null) {
            // Get the complaint data from the event
            Complaint complaint = event.getComplaint();

            // Log the complaint (optional, for debugging purposes)
            System.out.println("Complaint successfully created: " + complaint);

            // Clear the form fields after submission
            Platform.runLater(() -> clearForm());

            // Set the success message in the errorMessage label on the JavaFX thread
            Platform.runLater(() -> {
                errorMessage.setText("Your complaint has been filed and will be handled within 24 hours.");
                errorMessage.setTextFill(javafx.scene.paint.Color.GREEN);
            });
        } else {
            // If complaint creation failed, show an error message
            Platform.runLater(() -> {
                errorMessage.setText("An error occurred while creating your complaint.");
                errorMessage.setTextFill(javafx.scene.paint.Color.RED);
            });
        }
    }


    // Clear the form after submission
    private void clearForm() {
        nameText.clear();
        emailText.clear();
        phoneText.clear();
        complaintTextArea.clear();
        branchComboBox.setValue(null);  // Reset the combo box
    }
}
