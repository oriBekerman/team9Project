package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

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
    private ListView<MenuItem> menuListView;

    private Map<MenuItem, TextField> priceFieldMap = new HashMap<>();

    // Event handler for MenuEvent
    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        Menu menu = event.getMenu();
        System.out.println("Menu received in secondary controller");

        // Display menu items in the ListView
        menuListView.getItems().clear();  // Clear previous items
        menuListView.getItems().addAll(menu.getMenuItems()); // Add the new items
    }
    // Event handler for MenuEvent
    @Subscribe
    public void onUpdateEvent(updateDishEvent event) {
        try {
            SimpleClient.getClient().sendToServer("#display menu");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Back to home page button logic
    @FXML
    void BackToHPfunc(ActionEvent event) throws IOException {
        App.setRoot("primary");  // Switch to the primary screen
    }

    // Save the updated menu logic (stub)

    @FXML
    void SaveTheUpdateMenu(ActionEvent event) throws IOException {
        // Create a map to hold the MenuItem IDs and their new prices
        Map<Integer, Double> updatedPrices = new HashMap<>();

        // Iterate over the priceFieldMap to check for price changes
        for (Map.Entry<MenuItem, TextField> entry : priceFieldMap.entrySet()) {
            MenuItem item = entry.getKey();
            TextField priceField = entry.getValue();

            // If the price field is not disabled, check if the price has been updated
            if (!priceField.isDisabled()) {
                try {
                    double newPrice = Double.parseDouble(priceField.getText());

                    // Check if the price has changed
                    if (newPrice != item.getPrice()) {
                        // Add the MenuItem ID and new price to the updatedPrices map
                        updatedPrices.put(item.getItemID(), newPrice);

                        // Update the price of the MenuItem
                        item.setPrice(newPrice);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid price input for item: " + item.getName());
                }
            }
        }

        // Send the updated prices to the server (stub)
        for (Map.Entry<Integer, Double> entry : updatedPrices.entrySet()) {
            SimpleClient.getClient().editMenu(entry.getKey().toString(),entry.getValue().toString());
            System.out.println("Item ID: " + entry.getKey() + " New Price: " + entry.getValue());
        }

        // After saving, disable all price TextFields again
        for (TextField priceField : priceFieldMap.values()) {
            priceField.setDisable(true);  // Disable the TextField again
        }
    }

    @FXML
    void UpdateTheMenu(ActionEvent event) {
        // Enable all price fields
        for (TextField priceField : priceFieldMap.values()) {
            priceField.setDisable(false);  // Enable the TextField
        }
    }


    // Initialize method to register for events and notify SimpleClient about SecondaryController initialization
    @FXML
    void initialize() {
        System.out.println("SecondaryController initialized");

        // Register to listen for MenuEvent
        EventBus.getDefault().register(this);

        // Notify the SimpleClient that the SecondaryController has been initialized
        SimpleClient.setSecondaryControllerInitialized();
        // Set custom cell factory to display MenuItem details more nicely
        menuListView.setCellFactory(param -> new ListCell<MenuItem>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    VBox vbox = new VBox();

                    // Create labels for each attribute
                    Text name = new Text("Name: " + item.getName());
                    Text ingredients = new Text("Ingredients: " + item.getIngredients());
                    Text preference = new Text("Preference: " + item.getPreference());
                    Text picture = new Text("Picture: " + (item.getPicture() != null ? "Available" : "Not available"));

                    // Create an HBox for the price field and label
                    HBox priceBox = new HBox();
                    Text priceLabel = new Text("Price: ");
                    TextField priceField = new TextField(String.valueOf(item.getPrice()));
                    priceField.setDisable(true);  // Initially disabled
                    priceField.setPrefWidth(50);

                    // Add the label and text field to the HBox
                    priceBox.getChildren().addAll(priceLabel, priceField);
                    priceBox.setSpacing(5);  // Add space between label and field

                    // Add all the elements to the VBox
                    vbox.getChildren().addAll(name, priceBox, ingredients, preference, picture);

                    // Set VBox as the graphic of the cell
                    setGraphic(vbox);
                    // Store the price field
                    priceFieldMap.put(item, priceField);
                }
            }


        });

        // Ensure that FXML components are injected properly
        assert BackToHPbtn != null : "fx:id=\"BackToHPbtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert SaveBtn != null : "fx:id=\"SaveBtn\" was not injected: check your FXML file 'secondary.fxml'.";
        assert UpdatePriceBtn != null : "fx:id=\"UpdatePriceBtn\" was not injected: check your FXML file 'secondary.fxml'.";
    }
}
