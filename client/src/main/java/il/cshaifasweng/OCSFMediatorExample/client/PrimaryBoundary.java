package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;


import javafx.fxml.FXMLLoader;
import javafx.stage.Popup;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.*;

//
public class PrimaryBoundary {

	public Button MenuBtn;
	public Button subCompBtn;
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
	private Button toggleButtonBranch;

	@FXML
	private Button cancelDelivery;
	private Popup popup = new Popup();

	public List<Branch> branchList = null;
	public boolean branchListInit = false;
	private final Object lock = new Object();

	@FXML
	private Button loginBttn;

	@FXML
	private Button logoutBttn;

	@FXML
	private Button SaveBtn;

	@FXML
	private Button givePermitBtn;




	@FXML
	void givePermit(ActionEvent event) {
		try {
			// Create a Request object for the PERMIT_GRANTED category with the PERMISSION_REQUEST type
			Request<Void> request = new Request<>(ReqCategory.PERMIT_GRANTED, RequestType.PERMISSION_REQUEST, null);

			// Send the request to the server
			SimpleClient.getClient().sendToServer(request);

			System.out.println("Permit granted to dietitian.");
		} catch (IOException e) {
			e.printStackTrace();  // Print the stack trace for debugging
			showErrorMessage("Network error occurred while granting the permit.");
		}
	}


	// Helper method to show error messages to the user
	private void showErrorMessage(String message) {
		// Example of showing the error message to the user
		System.out.println("Error: " + message);
		// You can also display a pop-up or dialog to the user with the error message
	}





	@FXML
	void navToLoginP(ActionEvent event) {
		switchScreen("Login");
	}

	@FXML
	void navToDeliv(ActionEvent event) {
		switchScreen("Delivery");
		try {
			SimpleClient.getClient().displayNetworkMenu();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void cancelDel(ActionEvent event) {
		switchScreen("CancelDelivery");
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
		switchScreen("Branch");
	}


	@FXML
	void displayMenuFunc(ActionEvent event) throws IOException {
		switchScreen("secondary");
		try {
			App.setRoot("secondary");
			SimpleClient.getClient().displayNetworkMenu();
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
		assert givePermitBtn != null : "fx:id=\"givePermitBtn\" was not injected: check your FXML file 'primary.fxml'.";

		EventBus.getDefault().register(this);
		SimpleClient.getClient().getBranchList(); // Request branch list from server
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
			if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN)
			{
				System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
				UpdateMenuBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
			}
			else
			{
				UpdateMenuBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
			}
			if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.COMPANY_MANAGER) {
				System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
				givePermitBtn.setVisible(true);
			}
			 else
			 {
				givePermitBtn.setVisible(false);
			}
		} else {
			// If not logged in, show login button and hide logout button
			logoutBttn.setVisible(false);
			loginBttn.setVisible(true);
			UpdateMenuBtn.setVisible(false); // Hide Update button if not logged in
			givePermitBtn.setVisible(false); // Hide Give Permission button if not logged in
		}


		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Set the button action here
		toggleButtonBranch.setOnAction(e -> {
			System.out.println("Button clicked - showing popup");
			GetBranchListPopup();
		});

	}

	//get list of brunches pop up
	private void GetBranchListPopup() {
		synchronized (lock) {
			if (!branchListInit) {
				try {
					SimpleClient.getClient().getBranchList();
					while (!branchListInit) {
						lock.wait();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println("Thread interrupted while waiting for branch list.");
					return;
				}
			}
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchList.fxml"));
			Parent popupContent = loader.load();

			// Get BranchListController instance and set branches
			BranchListBoundary controller = loader.getController();
			controller.setBranches(branchList);

			popup.getContent().clear();
			popup.getContent().add(popupContent);
			popup.setAutoHide(true);
			// Ensure popup shows correctly
			if (toggleButtonBranch.getScene() != null) {
				popup.show(toggleButtonBranch.getScene().getWindow(),
						toggleButtonBranch.localToScreen(0, 0).getX(),
						toggleButtonBranch.localToScreen(0, 0).getY() + toggleButtonBranch.getHeight());
			} else {
				System.out.println("toggleButtonBranch scene is NULL - cannot display popup");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void getPopup(ActionEvent actionEvent) {
		System.out.println("getPopup");
		GetBranchListPopup();
	}

	// Handle the branch selected from the list
	@Subscribe
	public void onBranchSelectedEvent(BranchSelectedEvent event) {
		System.out.println("Branch selected: " + event.getBranch().getName());
		Branch branch = event.getBranch();
		if (branch == null) {
			System.out.println("branch is null");
		}
		openBranchPage(branch);
	}

	//open selected branch page
	private void openBranchPage(Branch branch) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
			Parent branchPageRoot = loader.load();
			// Get the controller and pass the branch
			BranchPageBoundary controller = loader.getController();
			controller.setBranch(branch);
			if (controller.branchIsSet) {
				System.out.println("branch is already set");
			}
			while (!controller.branchIsSet) {
				System.out.println("Waiting for branch to be set");
			}
			App.setContent(branchPageRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//handle branch list sent
	@Subscribe
	public void onBranchListSentEvent(BranchListSentEvent event) {
		synchronized (lock) {
			this.branchList = event.branches;
			this.branchListInit = true;
			System.out.println("onBranchesSentEvent");
			lock.notifyAll(); // Notify waiting threads that branches are initialized
		}
	}
	public void goToSubCompPage(ActionEvent actionEvent) {
		switchScreen("SubComplaint");
	}
}
