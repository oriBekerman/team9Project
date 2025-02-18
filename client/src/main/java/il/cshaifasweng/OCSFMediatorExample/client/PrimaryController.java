
package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
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
public class PrimaryController {

	@FXML
	private ResourceBundle resources;
	@FXML
	private URL location;

	@FXML
	private Label HomePageLabel;

	@FXML
	private Button MenutBtn;

	@FXML
	private Label WelcomeLabel;

	@FXML
	private ImageView MOMSImage;

	@FXML
	private Pane MenuBarPane;

	@FXML private Button toggleButtonBranch;
	private Popup popup = new Popup();

	public List<Branch> branchList =null;
	public boolean branchListInit=false;
	private final Object lock = new Object();


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
		try {
			App.setRoot("secondary");
			SimpleClient.getClient().displayNetworkMenu();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
	void initialize() throws IOException {
		assert HomePageLabel != null : "fx:id=\"HomePageLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert MOMSImage != null : "fx:id=\"MOMSImage\" was not injected: check your FXML file 'primary.fxml'.";
		assert MenuBarPane != null : "fx:id=\"MenuBarPane\" was not injected: check your FXML file 'primary.fxml'.";
		assert MenutBtn != null : "fx:id=\"MenutBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'primary.fxml'.";
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
			System.out.println("in getClass");
			Parent popupContent = loader.load();
			System.out.println("after load");

			// Get BranchListController instance and set branches
			BranchListController controller = loader.getController();
			controller.setBranches(branchList);

			popup.getContent().clear();
			popup.getContent().add(popupContent);
			System.out.println("after popupContent");
			popup.setAutoHide(true);
			System.out.println("after setAutoHide");
			// ✅ Ensure popup shows correctly
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
		if (branch==null) {
			System.out.println("branch is null");
		}
		openBranchPage(branch);
	}

	private void openBranchPage(Branch branch) {
		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchPage.fxml"));
//			Parent branchPageRoot = loader.load();
//
//			// Get the controller and pass the branch
//			BranchPageController controller = loader.getController();
//			controller.setBranch(branch);
//
////			// Use App.setRoot() to switch the scene
////			App.setRoot("BranchPage");
//			while (!controller.branchIsSet)
//			{
//				System.out.println("Waiting for branch to load");
//			}
//			App.setContent1(branchPageRoot);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Branch.fxml"));
			Parent branchPageRoot = loader.load();
			// Get the controller and pass the branch
			BranchPageController controller = loader.getController();
			controller.setBranch(branch);
			if (controller.branchIsSet)
			{
				System.out.println("branch is already set");
			}
			while(!controller.branchIsSet)
			{
				System.out.println("Waiting for branch to be set");
			}
			App.setContent1(branchPageRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Subscribe
	public void onBranchListSentEvent(BranchListSentEvent event) {
		synchronized (lock) {
			this.branchList = event.branches;
			this.branchListInit = true;
			System.out.println("onBranchesSentEvent");
			lock.notifyAll(); // Notify waiting threads that branches are initialized
		}
	}
} //change
