package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
    private TextField searchField;



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
    void UpdateTheMenu(ActionEvent event) {
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
//        branchSpecialColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDishType()));
        // This section display the image of mamasKitchen
        // Set cell factories for price fields
        // Check if the active user is DIETITIAN
        if (SimpleClient.getClient().getActiveUser() != null) {
            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN) {
                System.out.println("Active User: " + SimpleClient.getClient().getActiveUser().getUsername());
                UpdatePriceBtn.setVisible(true);  // Show Update button if user is a DIETITIAN
                SaveBtn.setVisible(true);
            } else {
                UpdatePriceBtn.setVisible(false);  // Hide Update button if user is not a DIETITIAN
                SaveBtn.setVisible(false);
            }
        } else {
            UpdatePriceBtn.setVisible(false); // Hide Update button if not logged in
            SaveBtn.setVisible(false);
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
