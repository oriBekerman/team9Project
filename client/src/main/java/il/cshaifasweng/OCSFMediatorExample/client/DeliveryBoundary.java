package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import il.cshaifasweng.OCSFMediatorExample.client.Events.MenuEvent;
import il.cshaifasweng.OCSFMediatorExample.client.Events.updateDishEvent;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;

public class DeliveryBoundary {

    public Label menuLabel;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BackToHPbtn;

    @FXML
    private TableView<OrderItem> menuTableView;

    @FXML
    private TableColumn<OrderItem, String> nameColumn;
    @FXML
    private TableColumn<OrderItem, String> ingredientsColumn;
    @FXML
    private TableColumn<OrderItem, String> preferenceColumn;
    @FXML
    private TableColumn<OrderItem, Double> priceColumn;
    @FXML
    private TableColumn<OrderItem, Integer> quantityColumn;
    @FXML
    private TableColumn<OrderItem, String> commentColumn;


    private List<OrderItem> orderItems = new ArrayList<>();
    private Delivery currentDelivery= new Delivery();;

    // Event handler for MenuEvent
    @Subscribe
    public void displayMenu(MenuEvent event) {
        Menu menu = event.getMenu();
        System.out.println("got menu event in delivery controller");
        menu.printMenu();

        Platform.runLater(() -> {
            // Clear the TableView before updating
            menuTableView.getItems().clear();
            // new delivery
            if(orderItems.isEmpty()) {
                // Create OrderItems from MenuItems (initialize with default values for quantity and preferences)
                for (MenuItem menuItem : menu.getMenuItems()) {
                    OrderItem orderItem = new OrderItem(menuItem, 0, "", currentDelivery);
                    orderItems.add(orderItem);
                }
            }
            //handle menu update after start of the delivery with navigation through pages
            else{
                // For each MenuItem, check if it exists in orderItems
                for (MenuItem menuItem : menu.getMenuItems()) {
                    boolean itemFound = false;

                    // Check if the MenuItem already exists in orderItems
                    for (OrderItem orderItem : orderItems) {
                        if (orderItem.getMenuItem().getItemID() == menuItem.getItemID()) {
                            // If it exists, update the existing OrderItem
                            orderItem.setMenuItem(menuItem);  // Update the MenuItem in the OrderItem
                            itemFound = true;
                            break;
                        }
                    }

                    // If the MenuItem was not found, create a new OrderItem
                    if (!itemFound) {
                        OrderItem newOrderItem = new OrderItem(menuItem, 0, "", currentDelivery);
                        orderItems.add(newOrderItem);
                    }
                }
            }

            // Add OrderItems to the TableView
            menuTableView.getItems().setAll(orderItems);
            menuTableView.refresh();
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


    // Initialize method to register for events
    @FXML
    void initialize() {
        System.out.println("Delivery initialized");

        // Register this class to listen for MenuEvent
        EventBus.getDefault().register(this);
//
//        // Set the TableView's column resize policy
//        menuTableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Make the TableView editable
        menuTableView.setEditable(true);

        // Initialize TableColumns to bind MenuItem data
        nameColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMenuItem().getName()));
        ingredientsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMenuItem().getIngredients()));
        preferenceColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMenuItem().getPreference()));
        priceColumn.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(cellData.getValue().getMenuItem().getPrice()).asObject());
        setupQuantityColumn(quantityColumn);
        setupCommentColumn(commentColumn);

        // Fetch menu data
        try {
            SimpleClient.getClient().displayNetworkMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to set up the quantity column with a Spinner
    private void setupQuantityColumn(TableColumn<OrderItem, Integer> quantityColumn) {
        quantityColumn.setCellFactory(column -> {
            return new TableCell<OrderItem, Integer>() {
                private final Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);  // Values between 0 and 10, starting at 0

                {
                    // Configure the Spinner
                    quantitySpinner.setEditable(true);  // Allow users to type a number
                    quantitySpinner.setPrefWidth(60);  // Set a preferred width for the spinner

                    // When the value changes in the spinner, update the OrderItem's quantity
                    quantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        OrderItem orderItem = getTableRow().getItem();
                        if (orderItem != null) {
                            orderItem.setQuantity(newValue);  // Update quantity in the OrderItem
                        }
                    });
                }

                @Override
                protected void updateItem(Integer quantity, boolean empty) {
                    super.updateItem(quantity, empty);
                    if (empty || quantity == null) {
                        setGraphic(null);  // Remove spinner if cell is empty
                    } else {
                        // Make sure the spinner's value is set correctly
                        quantitySpinner.getValueFactory().setValue(quantity);  // Set the current quantity to the spinner
                        setGraphic(quantitySpinner);  // Display the spinner
                    }
                }
            };
        });

        // Ensure the `quantityColumn` is set to the right property
        quantityColumn.setCellValueFactory(cellData -> {
            OrderItem orderItem = cellData.getValue();
            if (orderItem != null) {
                return new SimpleIntegerProperty(orderItem.getQuantity()).asObject();  // Return the quantity of the OrderItem
            } else {
                return null;
            }
        });
    }

    // Function to set up the comment column with a TextField
// Function to set up the comment column with a TextField that is always visible
    private void setupCommentColumn(TableColumn<OrderItem, String> commentColumn) {
        // Make the column editable and show a TextField always
        commentColumn.setEditable(true);

        // Set the cell factory to always show a TextField
        commentColumn.setCellFactory(col -> new TableCell<OrderItem, String>() {
            private final TextField textField = new TextField();

            {
                // Whenever the text in the cell changes, update the OrderItem's preferences (comment)
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                    OrderItem orderItem = getTableRow().getItem();
                    if (orderItem != null) {
                        orderItem.setPreferences(newValue);  // Update the comment (preferences)
                    }
                });
            }

            @Override
            protected void updateItem(String comment, boolean empty) {
                super.updateItem(comment, empty);
                if (empty || comment == null) {
                    setGraphic(null);  // Remove the text field if the cell is empty
                } else {
                    // Set the text of the textField to the current comment (preferences)
                    textField.setText(comment);
                    setGraphic(textField);  // Show the text field in the cell
                }
            }
        });

        // Set up the cell value factory for binding the OrderItem's comment
        commentColumn.setCellValueFactory(cellData -> {
            OrderItem orderItem = cellData.getValue();
            if (orderItem != null) {
                return new SimpleStringProperty(orderItem.getPreferences());  // Return the comment (preferences) of the OrderItem
            } else {
                return null;
            }
        });
    }





}
