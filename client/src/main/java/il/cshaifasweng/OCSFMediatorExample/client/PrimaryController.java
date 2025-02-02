package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;


//
public class PrimaryController {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label HomePageLabel;

	@FXML
	private Button UpdateMenuBtn;

	@FXML
	private Label WelcomeLabel;

	@FXML
	private ImageView MOMSImage;

	@FXML
	private Pane MenuBarPane;

	@FXML
	private Button loginBttn;

	@FXML
	private Button logoutBttn;

	@FXML
	void navToLoginP(ActionEvent event) {
		switchScreen("Login");
	}

	@FXML
	void navToDeliv(ActionEvent event) {
		switchScreen("Delivery");
	}

	@FXML
	void navToHP(ActionEvent event) {
		switchScreen("Home Page");
	}

	@FXML
	void navToReservation(ActionEvent event) {
		switchScreen("Reservation");
	}

	@FXML
	void navToBranches(ActionEvent event) {
		switchScreen("Branches");
	}


	@FXML
	void displayMenuFunc(ActionEvent event) throws IOException {
		switchScreen("secondary");
		try {
			SimpleClient.getClient().displayMenu();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	@FXML
	void LogOut(ActionEvent event) {
		// Call the logout method in SimpleClient to clear active user
		SimpleClient.getClient().logout();

		// Hide the logout button and show the login button again
		logoutBttn.setVisible(false);
		loginBttn.setVisible(true);
		// Hide the Update button after logging out
		UpdateMenuBtn.setVisible(false);
	}


	@FXML
	void initialize() throws IOException {

		assert HomePageLabel != null : "fx:id=\"HomePageLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'primary.fxml'.";
		assert MenuBarPane != null : "fx:id=\"MenuBarPane\" was not injected: check your FXML file 'primary.fxml'.";
		assert UpdateMenuBtn != null : "fx:id=\"UpdateMenuBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert loginBttn != null : "fx:id=\"loginBttn\" was not injected: check your FXML file 'primary.fxml'.";
		assert logoutBttn != null : "fx:id=\"logoutBttn\" was not injected: check your FXML file 'primary.fxml'.";


		// Menu bar (in the home page - this is the menu bar that is shown as "ALL")
		Parent menuBarParent = App.loadFXML("MenuBar");
		MenuBarPane.getChildren().clear();
		MenuBarPane.getChildren().add(menuBarParent);

		// This section display the image of mamasKitchen
		String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
		Image image = new Image(imagePath);
		MOMSImage.setImage(image);

		// Check if the user is logged in (activeUser is not null)
		if (SimpleClient.getClient().getActiveUser() != null) {
			// If logged in, show logout button and hide login button
			logoutBttn.setVisible(true);
			loginBttn.setVisible(false);
			// Check if the user is a "DIETITIAN" and display the Update button if true
			if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
				System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
				UpdateMenuBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
			} else {
				UpdateMenuBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
			}
		} else {
			// If not logged in, show login button and hide logout button
			logoutBttn.setVisible(false);
			loginBttn.setVisible(true);
			UpdateMenuBtn.setVisible(false); // Hide Update button if not logged in
		}


		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	}
