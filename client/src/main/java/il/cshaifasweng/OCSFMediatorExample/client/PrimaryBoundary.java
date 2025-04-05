package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.EventBus;

public class PrimaryBoundary
{
	private SideBarPrimaryBoundary sidebarController;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label WelcomeLabel;

	@FXML
	private AnchorPane sideBarPrimaryPlace;


	@FXML
	void initialize() throws IOException {
		assert WelcomeLabel != null : "fx:id=\"WelcomeLabel\" was not injected: check your FXML file 'primary.fxml'.";
		assert sideBarPrimaryPlace != null : "fx:id=\"sideBarPrimaryPlace\" was not injected: check your FXML file 'primary.fxml'.";

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("sideBarPrimary.fxml"));
			Parent sideBarPrimaryRoot = loader.load();
//			sidebarController = loader.getController();
			sidebarController = SideBarPrimaryBoundary.getInstance();


			sideBarPrimaryPlace.getChildren().clear();
			sideBarPrimaryPlace.getChildren().add(sideBarPrimaryRoot);

			// ✅ We now wait until sidebar is fully initialized before requesting data
			Platform.runLater(() -> {
				if (!EventBus.getDefault().isRegistered(sidebarController)) {
					EventBus.getDefault().register(sidebarController);
					System.out.println("✅ [PrimaryBoundary] Sidebar registered to EventBus");
				}

				// ✅ Only now send the branch list request!
				SimpleClient.getClient().getBranchList();
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
