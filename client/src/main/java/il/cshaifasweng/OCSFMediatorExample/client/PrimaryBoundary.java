package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class PrimaryBoundary {

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

		Parent sideBarParent = App.loadFXML("sideBarPrimary");
		sideBarPrimaryPlace.getChildren().clear();
		sideBarPrimaryPlace.getChildren().add(sideBarParent);
	}
}
