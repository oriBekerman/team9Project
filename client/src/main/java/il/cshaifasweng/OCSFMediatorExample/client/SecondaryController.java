package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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

import static il.cshaifasweng.OCSFMediatorExample.entities.Request.RequestType.*;

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
    private TableView<MenuItem> menuTableView;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> ingredientsColumn;

    @FXML
    private TableColumn<MenuItem, String> preferenceColumn;

    @FXML
    private TableColumn<MenuItem, Double> priceColumn;

    @FXML
    void updateIngredients(ActionEvent event) {
        // Code to update ingredients (handled in SaveTheUpdateMenu)
    }

    private Map<MenuItem, TextField> priceFieldMap = new HashMap<>();
    private Map<MenuItem, TextField> ingredientsFieldMap = new HashMap<>();

    // Event handler for MenuEvent
    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        Menu menu = event.getMenu();
        System.out.println("Menu received in secondary controller");

        Platform.runLater(() -> {
            // Clear the TableView before updating
            menuTableView.getItems().clear();
            // Add new menu items to the TableView
            menuTableView.getItems().setAll(menu.getMenuItems());
        });
    }

    // Event handler for MenuEvent
    @Subscribe
    public void onUpdateEvent(updateDishEvent event) {
        try {
            Request request = new Request<>(DISPLAY_MENU);
            SimpleClient.getClient().sendToServer(request);
            menuTableView.refresh();
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
        // Create a map to hold the MenuItem IDs and their updated prices and ingredients
        Map<Integer, Double> updatedPrices = new HashMap<>();
        Map<Integer, String> updatedIngredients = new HashMap<>();

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

        // Iterate over the ingredientsFieldMap to check for ingredient changes
        for (Map.Entry<MenuItem, TextField> entry : ingredientsFieldMap.entrySet()) {
            MenuItem item = entry.getKey();
            TextField ingredientsField = entry.getValue();

            // If the ingredients field is not disabled, check if the ingredients have been updated
            if (!ingredientsField.isDisabled()) {
                String newIngredients = ingredientsField.getText();

                // Check if the ingredients have changed
                if (!newIngredients.equals(item.getIngredients())) {
                    // Add the MenuItem ID and updated ingredients to the map
                    updatedIngredients.put(item.getItemID(), newIngredients);

                    // Update the ingredients of the MenuItem
                    item.setIngredients(newIngredients);
                }
            }
        }

        // Send the updated prices to the server (stub)
        for (Map.Entry<Integer, Double> entry : updatedPrices.entrySet()) {
            SimpleClient.getClient().editMenu(entry.getKey().toString(), entry.getValue().toString());
            System.out.println("Item ID: " + entry.getKey() + " New Price: " + entry.getValue());
        }

        // Send the updated ingredients to the server (stub)
        for (Map.Entry<Integer, String> entry : updatedIngredients.entrySet()) {
            SimpleClient.getClient().editMenu(entry.getKey().toString(), entry.getValue());
            System.out.println("Item ID: " + entry.getKey() + " Updated Ingredients: " + entry.getValue());
        }

        // Disable all price TextFields and ingredient TextFields after saving
        for (TextField priceField : priceFieldMap.values()) {
            priceField.setDisable(true);
        }

        for (TextField ingredientsField : ingredientsFieldMap.values()) {
            ingredientsField.setDisable(true);
        }

        Platform.runLater(() -> {
            menuTableView.refresh();
            SaveBtn.setDisable(true);
            UpdatePriceBtn.setDisable(false);
            UpdatePriceBtn.requestFocus();
        });
    }

    @FXML
    void UpdateTheMenu(ActionEvent event) {
        // Enable all price and ingredient fields
        Platform.runLater(() -> {
            // Enable all price fields
            for (TextField priceField : priceFieldMap.values()) {
                priceField.setDisable(false);  // Enable the TextField
            }

            // Enable save button and disable update button
            SaveBtn.setDisable(false); // Enable save button
            UpdatePriceBtn.setDisable(true); // Disable update button
        });
    }
    @FXML
    void UpdateIngridients(ActionEvent event) {
        // Enable all price and ingredient fields
        Platform.runLater(() ->
        {
            // Enable all ingredients fields
            for (TextField ingredientsField : ingredientsFieldMap.values()) {
                ingredientsField.setDisable(false);  // Enable the TextField
            }
            // Enable save button and disable update button
            SaveBtn.setDisable(false); // Enable save button
            UpdatePriceBtn.setDisable(true); // Disable update button
        });
    }

    @FXML
    void addDish(ActionEvent event) {
        // Example of adding a new dish
        MenuItem newItem = new MenuItem();
        newItem.setName("New Dish"); // Prompt user to input the name
        newItem.setIngredients("Ingredients for the new dish"); // Prompt user for ingredients
        newItem.setPrice(10.0); // Set a default price or prompt user for price

        // Add the new item to the table view
        menuTableView.getItems().add(newItem);

        // Add the new item's price and ingredients fields to the corresponding maps
        TextField priceField = new TextField(String.valueOf(newItem.getPrice()));
        priceField.setDisable(true);
        priceFieldMap.put(newItem, priceField);

        TextField ingredientsField = new TextField(newItem.getIngredients());
        ingredientsField.setDisable(true);
        ingredientsFieldMap.put(newItem, ingredientsField);
    }

    @FXML
    void removeDish(ActionEvent event) {
        // Get the selected item from the table
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Remove the selected item from the table view
            menuTableView.getItems().remove(selectedItem);

            // Remove the corresponding price and ingredients fields from the maps
            priceFieldMap.remove(selectedItem);
            ingredientsFieldMap.remove(selectedItem);

            // Ensure to clear any input fields from the UI (e.g., deactivate TextFields)
            // This ensures both active and inactive fields are properly handled
            Platform.runLater(() -> {
                // Optionally clear or reset the TextFields in the table if you want to clear the fields for the item
                menuTableView.refresh();  // This refreshes the TableView to ensure it's updated

                // Clear any fields that were previously active or inactive
                // If you want, you can clear the value of TextField to reset the fields (depending on your preference)
                for (TextField priceField : priceFieldMap.values()) {
                    priceField.setText("");  // Clear the value
                    priceField.setDisable(true);  // Optionally disable it
                }

                for (TextField ingredientsField : ingredientsFieldMap.values()) {
                    ingredientsField.setText("");  // Clear the value
                    ingredientsField.setDisable(true);  // Optionally disable it
                }
            });

            // Optionally, send a request to the server to remove the item from the database if needed
            // SimpleClient.getClient().removeMenuItem(selectedItem);
        } else {
            // Show an alert if no dish is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Dish Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a dish to remove.");
            alert.showAndWait();
        }
    }



    // Initialize method to register for events
    @FXML
    void initialize() {
        System.out.println("SecondaryController initialized");

        // Register to listen for MenuEvent
        EventBus.getDefault().register(this);

        // Notify the SimpleClient that the SecondaryController has been initialized
        SimpleClient.setSecondaryControllerInitialized();

        // Initialize TableColumns to bind MenuItem data
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
        preferenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreference()));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());

        // Set cell factories for price fields
        priceColumn.setCellFactory(col -> new TableCell<MenuItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    TextField priceField = new TextField(price.toString());
                    priceField.setDisable(true);  // Initially disable the price field
                    priceFieldMap.put(getTableView().getItems().get(getIndex()), priceField);
                    setGraphic(priceField);
                }
            }
        });

        // Set cell factories for ingredients fields
        ingredientsColumn.setCellFactory(col -> new TableCell<MenuItem, String>() {
            @Override
            protected void updateItem(String ingredients, boolean empty) {
                super.updateItem(ingredients, empty);
                if (empty || ingredients == null) {
                    setText(null);
                } else {
                    TextField ingredientsField = new TextField(ingredients);
                    ingredientsField.setDisable(true);  // Initially disable the ingredients field
                    ingredientsFieldMap.put(getTableView().getItems().get(getIndex()), ingredientsField);
                    setGraphic(ingredientsField);
                }
            }
        });

        Platform.runLater(() -> {
            // Clear the TableView and refresh it with the updated menu items
            menuTableView.getItems().clear(); // Clear previous items
            SaveBtn.setDisable(true);  // Disable the save button
            UpdatePriceBtn.setDisable(false);  // Re-enable the update button
            UpdatePriceBtn.requestFocus();  // Focus the update button
        });
    }
}
