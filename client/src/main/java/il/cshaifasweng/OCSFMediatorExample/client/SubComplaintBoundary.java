package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.client.Events.ComplaintCustomerEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.ComplaintStatus;
import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
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

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

public class SubComplaintBoundary {
    public TextArea complaintTextArea;
    public Button continueBtn;
    public ComboBox <String> branchComboBox;
    public  PersonalDetailsFillingBoundary personalDetailsFillingBoundary;
    public TextField nameText;
    public TextField emailText;
    public TextField phoneText;
    public String name;
    public String email;
    public String phone;
    public String message;
    public Label errorMessage;

    @FXML
    void initialize() {
        assert branchComboBox != null : "fx:id=\"branchesList\" was not injected: check your FXML file 'reservation.fxml'.";
        assert continueBtn != null : "fx:id=\"continueBtn\" was not injected: check your FXML file 'reservation.fxml'.";
        setBranchesList();
        // Add listener to check inputs and enable/disable the button accordingly
        nameText.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        emailText.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        phoneText.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        complaintTextArea.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
        EventBus.getDefault().register(this);
    }

//    public void getCustomerDetails(ActionEvent actionEvent) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonalDetailsFilling.fxml"));
//            Parent parent = loader.load(); // Load the FXML correctly
//
//            // Ensure the controller is set
//            personalDetailsFillingBoundary = loader.getController();
//            personalDetailsFillingBoundary.setType("Complaint");
//
//            // Switch to the new screen
//            App.switchScreen("Personal Details Filling");
//        } catch (IOException e) {
//            System.err.println("Error loading PersonalDetailsFilling.fxml: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }



    public void getComplaintMessage(InputMethodEvent inputMethodEvent) {
        this.message=complaintTextArea.getText();
    }
    void setBranchesList(){
        branchComboBox.getItems().add("Haifa");
        branchComboBox.getItems().add("Tel-Aviv");
        branchComboBox.getItems().add("Jerusalem");
        branchComboBox.getItems().add("Zikhron Ya'akov");
    }
    @Subscribe
    public void getCustomerInfo(ComplaintCustomerEvent event)
    {
        String name=event.getName();
        String email=event.getEmail();
        String phone=event.getPhone();
        System.out.println(name + " " + email + " " + phone );

    }
    public void getCustomerDetails(ActionEvent actionEvent) {
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
            List<String> details = List.of(name, email, phone);
            Complaint complaint = new Complaint(message, ComplaintStatus.NEW);
            complaint.customerIsSet = false;
            SimpleClient.getClient().submitComplaint(details,complaint);
        }
        catch (Exception e) {
            errorMessage.setText(e.getMessage());
        }

    }

    // Disable the continue button if any field is empty
    private void validateFields() {
        continueBtn.setDisable(areFieldsEmpty());
    }

    private boolean areFieldsEmpty() {
        return nameText.getText().trim().isEmpty() ||
                emailText.getText().trim().isEmpty() ||
                phoneText.getText().trim().isEmpty() ||
                complaintTextArea.getText().trim().isEmpty();
    }
}
