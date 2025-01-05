package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import javafx.scene.control.ListView;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;

public class SecondaryController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private Button SaveBtn;

    @FXML
    private Button UpdatePriceBtn;

    @FXML
    private ListView<String> menuListView;

    // Event handler for MenuEvent
    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        Menu menu = event.getMenu();
        System.out.println("Menu received in secondary controller");

        // Display menu items in the ListView
        menuListView.getItems().clear();  // Clear previous items
        for (MenuItem item : menu.getMenuItems()) {
            // Create a string with all the details of the menu item
            String itemDetails = "Name: " + item.getName() + "\n" +
                    "Price: " + item.getPrice() + "\n" +
                    "Ingredients: " + item.getIngredients() + "\n" +
                    "Preference: " + item.getPreference() + "\n" +
                    "Picture: " + (item.getPicture() != null ? "Available" : "Not available")+"\n";

            // Add the item details as a string to the ListView
            menuListView.getItems().add(itemDetails);
        }
    }

    // Back to home page button logic
    @FXML
    void BackToHPfunc(ActionEvent event) throws IOException {
        App.setRoot("primary");  // Switch to the primary screen
    }

    // Save the updated menu logic (stub)
    @FXML
    void SaveTheUpdateMenu(ActionEvent event) {
        // Logic for saving the updated menu can be added here
    }

    // Update the menu logic (stub)
    @FXML
    void UpdateTheMenu(ActionEvent event) {
        // Logic for updating the menu can be added here
    }

    // Initialize method to register for events and notify SimpleClient about SecondaryController initialization
    @FXML
    void initialize() {
        System.out.println("SecondaryController initialized");

        // Register to listen for MenuEvent
        EventBus.getDefault().register(this);

        // Notify the SimpleClient that the SecondaryController has been initialized
        SimpleClient.setSecondaryControllerInitialized();

        // Ensure that FXML components are injected properly
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SaveBtn != null : "fx:id=\"SaveBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpdatePriceBtn != null : "fx:id=\"UpdatePriceBtn\" was not injected: check your FXML file 'secondary.fxml'.";
    }
}
