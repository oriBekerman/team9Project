package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
			sidebarController = loader.getController();

			sideBarPrimaryPlace.getChildren().clear();
			sideBarPrimaryPlace.getChildren().add(sideBarPrimaryRoot);
		} catch (IOException e) {
			e.printStackTrace();
		}

//		Parent sideBarParent = App.loadFXML("sideBarPrimary");
//		sideBarPrimaryPlace.getChildren().clear();
//		sideBarPrimaryPlace.getChildren().add(sideBarParent);

	}
}
