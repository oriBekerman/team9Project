package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.entities.DishType;
import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.updateDishEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;


public class SecondaryBoundary {

    public Label menuLabel;
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
    private TableColumn<MenuItem, ImageView> imageColum;
    @FXML
    private TableColumn<il.cshaifasweng.OCSFMediatorExample.entities.MenuItem, String> dishTypeColumn;


    @FXML
    private TextField searchField;

    @FXML
    private Button UpdateingridientsBtn;

    @FXML
    private Button addDishBtn;

    @FXML
    private Button removeDishBtn;


    @FXML
    void UpdateIngridients(ActionEvent event) {
        // Get the selected MenuItem from the table view
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        // If no item is selected, show a message
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dish to update ingredients.");
            alert.showAndWait();
            return;
        }

        // Open a dialog or prompt to update ingredients (you can create a separate method for this if needed)
        TextInputDialog dialog = new TextInputDialog(selectedItem.getIngredients());
        dialog.setTitle("Update Ingredients");
        dialog.setHeaderText("Edit the ingredients for: " + selectedItem.getName());
        dialog.setContentText("Ingredients:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newIngredients -> {
            // Update the selected item’s ingredients
            selectedItem.setIngredients(newIngredients);
            menuTableView.refresh();  // Refresh the TableView to show the updated ingredients
        });
    };

    @FXML
    void addDish(ActionEvent event) {
        // Prompt the user to enter a new dish's details
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add New Dish");
        nameDialog.setHeaderText("Enter the name of the new dish:");
        Optional<String> nameResult = nameDialog.showAndWait();

        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String dishName = nameResult.get();

            // Continue with ingredients, preference, and price input as before...
            TextInputDialog ingredientsDialog = new TextInputDialog();
            ingredientsDialog.setTitle("Add Ingredients");
            ingredientsDialog.setHeaderText("Enter the ingredients for: " + dishName);
            Optional<String> ingredientsResult = ingredientsDialog.showAndWait();

            if (ingredientsResult.isPresent() && !ingredientsResult.get().trim().isEmpty()) {
                String dishIngredients = ingredientsResult.get();

                TextInputDialog preferenceDialog = new TextInputDialog();
                preferenceDialog.setTitle("Add Preference");
                preferenceDialog.setHeaderText("Enter any preference for: " + dishName);
                Optional<String> preferenceResult = preferenceDialog.showAndWait();

                String dishPreference = preferenceResult.orElse("");  // Default to empty if not provided

                TextInputDialog priceDialog = new TextInputDialog("0.0");
                priceDialog.setTitle("Add Price");
                priceDialog.setHeaderText("Enter the price for: " + dishName);
                Optional<String> priceResult = priceDialog.showAndWait();

                if (priceResult.isPresent() && !priceResult.get().trim().isEmpty()) {
                    try {
                        double price = Double.parseDouble(priceResult.get());

                        // Create a default byte[] for the picture (empty for now)
                        byte[] defaultPicture = new byte[0];  // Empty byte array for now

                        // Create a new MenuItem and add it to the menu
                        MenuItem newDish = new MenuItem(dishName, price, dishIngredients, dishPreference, defaultPicture, DishType.BASE);
                        allMenuItems.add(newDish);  // Add to the list
                        menuTableView.getItems().add(newDish);  // Add to the TableView
                    } catch (NumberFormatException e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format.");
                        alert.showAndWait();
                    }
                }
            }
        }
    }

    @FXML
    void removeDish(ActionEvent event) {
        // Get the selected MenuItem from the table view
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        // If no item is selected, show a message
        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dish to remove.");
            alert.showAndWait();
            return;
        }

        // Ask for confirmation before removing the item
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this dish?");
        confirmAlert.setTitle("Remove Dish");
        confirmAlert.setHeaderText("You are about to remove: " + selectedItem.getName());
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            allMenuItems.remove(selectedItem);  // Remove the dish from the list
            menuTableView.getItems().remove(selectedItem);  // Remove the dish from the TableView
        }
    };


    //    @FXML
//   private TableColumn<MenuItem,String> branchSpecialColumn;
    private ObservableList<MenuItem> allMenuItems = javafx.collections.FXCollections.observableArrayList();

    private Map<MenuItem, TextField> priceFieldMap = new HashMap<>();

    // Event handler for MenuEvent
    @Subscribe
    public void onMenuEvent(MenuEvent event) {
        Menu menu = event.getMenu();
        System.out.println("Got menu event in SecondaryBoundary");
        menu.printMenu();

        Platform.runLater(() -> {
            menuTableView.getItems().clear();
            menuTableView.getItems().setAll(menu.getMenuItems());

            // שמירה של כל הפריטים ברשימה קבועה לחיפוש
            allMenuItems.setAll(menu.getMenuItems());

            System.out.println("Menu items loaded: " + allMenuItems.size()); // Debugging
        });
    }


    // Event handler for MenuEvent
    @Subscribe
    public void onUpdateEvent(updateDishEvent event) {
        try {
//            Request request=new Request<>(GET_BASE_MENU);
//            SimpleClient.getClient().sendToServer(request);
            SimpleClient.getClient().displayNetworkMenu();
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
            SimpleClient.getClient().editMenu(entry.getKey().toString(), entry.getValue().toString());
            System.out.println("Item ID: " + entry.getKey() + " New Price: " + entry.getValue());
        }

        // Disable all price TextFields after saving
        for (TextField priceField : priceFieldMap.values()) {
            priceField.setDisable(true);
        }

        Platform.runLater(() -> {
            menuTableView.refresh();
            SaveBtn.setDisable(true);
            UpdatePriceBtn.setDisable(false);
            UpdatePriceBtn.requestFocus();
        });
    }

    // Update the menu (enable price fields)
    @FXML
    void UpdateThePrice(ActionEvent event)
    {
        // Enable all price fields
        Platform.runLater(() -> {
            for (TextField priceField : priceFieldMap.values()) {
                priceField.setDisable(false);  // Enable the TextField
            }
            SaveBtn.setDisable(false); // Enable save button
            UpdatePriceBtn.setDisable(true); // Disable update button
        });
    }




    @FXML
    void performSearch(ActionEvent event) {
        String query = searchField.getText().toLowerCase().trim();
        System.out.println("Search Query: " + query); // Debugging

        // אם השדה ריק – הצג את כל המנות
        if (query.isEmpty()) {
            System.out.println("Showing all items");
            menuTableView.getItems().setAll(allMenuItems);
            menuTableView.refresh();
            return;
        }

        ObservableList<MenuItem> filteredList;

        // בדיקה אם המשתמש הקליד מספר (חיפוש לפי מחיר)
        try {
            double maxPrice = Double.parseDouble(query);
            System.out.println("Searching for items with price ≤ " + maxPrice);
            filteredList = allMenuItems.filtered(item -> item.getPrice() <= maxPrice);
        } catch (NumberFormatException e) {
            // אם המשתמש הקליד טקסט – חיפוש לפי שם ורכיבים
            filteredList = allMenuItems.filtered(item ->
                    (item.getName() != null && item.getName().toLowerCase().contains(query)) ||
                            (item.getIngredients() != null && item.getIngredients().toLowerCase().contains(query))
            );
        }

        System.out.println("Found " + filteredList.size() + " matching items");
        menuTableView.getItems().clear();
        menuTableView.getItems().setAll(filteredList);
        menuTableView.refresh();
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
        dishTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDishTypeString ()));

        // This section display the image of mamasKitchen
        // Set cell factories for price fields
        // Check if the active user is DIETITIAN
        if (SimpleClient.getClient().getActiveUser() != null) {
            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
                System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
                UpdatePriceBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
                UpdateingridientsBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
                addDishBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
                removeDishBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
                SaveBtn.setVisible(true);
            } else {
                UpdatePriceBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
                SaveBtn.setVisible(false);
                UpdateingridientsBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
                addDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
                removeDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            }
        } else {
            UpdatePriceBtn.setVisible(false); // Hide Update button if not logged in
            SaveBtn.setVisible(false);
            UpdateingridientsBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            addDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            removeDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
        }
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



        Platform.runLater(() -> {
            // Clear the TableView and refresh it with the updated menu items
            menuTableView.getItems().clear(); // Clear previous items
            SaveBtn.setDisable(true);  // Disable the save button
            UpdatePriceBtn.setDisable(false);  // Re-enable the update button
            UpdatePriceBtn.requestFocus();  // Focus the update button
        });
    }
}
