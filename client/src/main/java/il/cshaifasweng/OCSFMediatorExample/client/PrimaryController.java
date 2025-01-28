
package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.util.List;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.Subscribe;

//
public class PrimaryController {


	private BranchPageController branchPageController;
	public Button menuBtn;
	public Button ourBranchesBtn;
	public VBox BranchList;
	public Button branchBtn1;
	public Button branchBtn2;
	@FXML
	private Label HomePageLabel;

	@FXML
	private Button MenutBtn;

	@FXML
	private Label WelcomeLabel;

private List<Branch> branches;
private final Object lock = new Object();
private boolean branchesLoaded = false;

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
	void initialize() {
		branchPageController = new BranchPageController();
		assert HomePageLabel != null : "fx:id=\"HomePageLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert MenutBtn != null : "fx:id=\"MenutBtn\" was not injected: check your FXML file 'primary.fxml'.";
		assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'primary.fxml'.";
		BranchList.setVisible(false);
		try {
			SimpleClient.getClient().sendToServer("add client");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Event handler for BranchesSentEvent
	@Subscribe
	public void onBranchesSentEvent(BranchesSentEvent event) {
		synchronized (lock) {
			this.branches = event.branches; // Set the branches
			branchesLoaded = true; // Set the flag to true
			lock.notify(); // Notify the waiting thread
		}
	}
@FXML
	public void goToBranchPage(ActionEvent event) {
		try {
			Button sourceButton = (Button) event.getSource(); // Get the button that triggered the action
			String branchName = sourceButton.getText(); // Get the button's text

			FXMLLoader loader = new FXMLLoader(getClass().getResource("BranchPage.fxml"));
			Parent branchPageRoot = loader.load();

			BranchPageController controller = loader.getController();

			// Find the branch associated with the button's text
			for (Branch branch : branches) {
				if (branch.getName().equals(branchName)) {
					branchPageController.setBranch(branch);
					break;
				}
			}

			Stage stage = new Stage();
			stage.setScene(new Scene(branchPageRoot));
			stage.setTitle("Branch Page - " + branchName);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Open and display the branch list
	public void openBranchList(ActionEvent mouseEvent) {
		try {
			SimpleClient.getClient().getBranchList(); // Request branch data
			waitForBranches(); // Wait until the branch data is loaded
			displayBranches(); // Display branches in the table
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Wait for branch data to load
	public void waitForBranches() throws InterruptedException {
		synchronized (lock) {
			while (!branchesLoaded) {
				lock.wait(); // Block until the branches are loaded
			}
		}
	}

	// Set branchesLoaded to true and notify waiting threads
	public void setBranchesLoadedTrue() {
		synchronized (lock) {
			branchesLoaded = true;
			lock.notify(); // Notify waiting threads
		}
	}
	private void displayBranches() {
		if (branches != null && !branches.isEmpty()) {
			BranchList.getChildren().removeIf(node -> node instanceof Button);
			for (int i = 0; i < branches.size(); i++)
			{
				String name=branches.get(i).getName();
				int branchID=branches.get(i).getId();
				addNewButton(name,branchID);
			}
		} else {
			System.out.println("No branches to display.");
		}
	}
	public void addNewButton(String text,int branchID) {
		Button newButton = new Button(text);
		newButton.setStyle("-fx-background-color: #8a6f48; -fx-text-fill: white; -fx-font-size: 16px;"); // Optional styling

		// Define the action for the button
		newButton.setOnAction(event -> goToBranchPage(event));

		// Add the button dynamically to the VBox (BranchList)
		BranchList.getChildren().add(newButton);
	}

}



