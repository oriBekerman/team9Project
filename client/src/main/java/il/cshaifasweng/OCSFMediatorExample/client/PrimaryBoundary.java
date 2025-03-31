package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchListSentEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.BranchSelectedEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;

import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.application.Platform;
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

public class PrimaryBoundary {
	public Button MenuBtn;
	public Button subCompBtn;
	public Button complaintsTableBtn;
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
			Request<Void> request = new Request<>(ReqCategory.PERMIT_GRANTED, RequestType.PERMISSION_REQUEST, null);
			SimpleClient.getClient().sendToServer(request);
		} catch (IOException e) {
			e.printStackTrace();
			showErrorMessage("Network error occurred while granting the permit.");
		}
	}

	private void showErrorMessage(String message) {
		System.out.println("Error: " + message);
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
		onExit();
		switchScreen("Home Page");
	}

	@FXML
	void navToReservation(ActionEvent event) {
		switchScreen("Reservation");
	}

	@FXML
	void navToBranches(ActionEvent event) {
		onExit();
		switchScreen("Branch");
	}

	public void onExit() {
		EventBus.getDefault().unregister(this);
	}

	@FXML
	void displayMenuFunc(ActionEvent event) throws IOException {
		switchScreen("secondary");
		try {
			App.setRoot("secondary");
			//	SimpleClient.getClient().displayNetworkMenu();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void LogOut(ActionEvent event) {
		SimpleClient.getClient().logout();
		logoutBttn.setVisible(false);
		loginBttn.setVisible(true);
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

		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

		SimpleClient.getClient().getBranchList();
		// Menu bar (in the home page - this is the menu bar that is shown as "ALL")
		Parent menuBarParent = App.loadFXML("MenuBar");
		MenuBarPane.getChildren().clear();
		MenuBarPane.getChildren().add(menuBarParent);

		// This section display the image of mamasKitchen
		String imagePath = "il/cshaifasweng/OCSFMediatorExample/client/mamasKitchen.jpg";
		Image image = new Image(imagePath);
		MOMSImage.setImage(image);

		if (SimpleClient.getClient().getActiveUser() != null) {
			logoutBttn.setVisible(true);
			loginBttn.setVisible(false);
			UpdateMenuBtn.setVisible(false);
			givePermitBtn.setVisible(false);
			complaintsTableBtn.setVisible(false);
			getUserAuthorizedTools();
		} else {
			logoutBttn.setVisible(false);
			loginBttn.setVisible(true);
			UpdateMenuBtn.setVisible(false);
			givePermitBtn.setVisible(false);
			complaintsTableBtn.setVisible(false);
		}
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			e.printStackTrace();
		}
		toggleButtonBranch.setOnAction(e ->
		{
			GetBranchListPopup();
		});
	}

	private void getUserAuthorizedTools()
	{
		EmployeeType employeeType=SimpleClient.getClient().getActiveUser().getEmployeeType();
		switch (employeeType)
		{
			case DIETITIAN :
				UpdateMenuBtn.setVisible(true);
				break;
			case COMPANY_MANAGER:
				givePermitBtn.setVisible(true);
				complaintsTableBtn.setVisible(true);
				break;
			case CUSTOMER_SERVICE:
				complaintsTableBtn.setVisible(true);
				break;
			case CUSTOMER_SERVICE_MANAGER:
				complaintsTableBtn.setVisible(true);
				break;
		}
	}

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

	@Subscribe
	public void onBranchSelectedEvent(BranchSelectedEvent event) {
		Branch branch = event.getBranch();
		if (branch == null) {
			System.out.println("branch is null");
		}
		openBranchPage(branch);
	}

	private void openBranchPage(Branch branch) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
			Parent branchPageRoot = loader.load();
			// Get the controller and pass the branch
			BranchPageBoundary controller = loader.getController();
			controller.setBranch(branch);
			while (!controller.branchIsSet) {
				System.out.println("Waiting for branch to be set");
			}
			App.setContent(branchPageRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onBranchListSentEvent(BranchListSentEvent event) {
		synchronized (lock) {
			this.branchList = event.branches;
			this.branchListInit = true;
			lock.notifyAll();
		}
	}

	public void goToSubCompPage(ActionEvent actionEvent) {
		switchScreen("SubComplaint");
	}

	public void viewComplaints(ActionEvent actionEvent) {
		openComplaintsTablePage();
	}

	public void openComplaintsTablePage() {
		switchScreen("Complaints");
	}
}
