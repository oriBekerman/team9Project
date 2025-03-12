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

import static il.cshaifasweng.OCSFMediatorExample.client.App.switchScreen;

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

    @FXML
    private RadioButton deliveryRadio;
    @FXML
    private RadioButton pickupRadio;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label deliveryMessageLabel;

    private List<OrderItem> orderItems = new ArrayList<>();
    private Delivery currentDelivery= new Delivery();;

    private static final double DELIVERY_COST = 15.0;

    // Setter method for setting the branch ID
    public void setBranchId(Branch branch) {
        currentDelivery.setBranch(branch);
    }

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

    // Function to update the total price label
    private void updateTotalPrice() {
        // Ensure the menu table view is updated with the latest order items
        if (orderItems != null) {

            double totalPrice = 0.0;

            // Calculate the total price based on quantity and price for each item
            for (OrderItem orderItem : orderItems) {
                totalPrice += orderItem.getMenuItem().getPrice() * orderItem.getQuantity();
            }

            // Add delivery cost if delivery is selected
            if (deliveryRadio.isSelected()) {
                totalPrice += DELIVERY_COST;
                // Update currentDelivery for delivery method and total price
                currentDelivery.setDeliveryMethod(DeliveryMethod.DELIVERY);
            } else {
                // Update currentDelivery for pickup method and total price
                currentDelivery.setDeliveryMethod(DeliveryMethod.SELF_PICKUP);
            }

            // Update the total price regardless of the delivery method
            currentDelivery.setTotalPrice(totalPrice);

            // Update the total price label to reflect the calculated value
            totalPriceLabel.setText("Total Price: " + totalPrice);
        }
        else return;
    }

    // Function to handle changes in quantity
    private void setupQuantityColumn(TableColumn<OrderItem, Integer> quantityColumn) {
        quantityColumn.setCellFactory(column -> {
            return new TableCell<OrderItem, Integer>() {
                private final Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, 0);  // Values between 0 and 10, starting at 0

                {
                    quantitySpinner.setEditable(true);  // Allow users to type a number
                    quantitySpinner.setPrefWidth(60);  // Set a preferred width for the spinner

                    quantitySpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
                        OrderItem orderItem = getTableRow().getItem();
                        if (orderItem != null) {
                            orderItem.setQuantity(newValue);  // Update quantity in the OrderItem
                            updateTotalPrice(); // Update the total price when quantity changes
                        }
                    });
                }

                @Override
                protected void updateItem(Integer quantity, boolean empty) {
                    super.updateItem(quantity, empty);
                    if (empty || quantity == null) {
                        setGraphic(null);  // Remove spinner if cell is empty
                    } else {
                        quantitySpinner.getValueFactory().setValue(quantity);  // Set the current quantity to the spinner
                        setGraphic(quantitySpinner);  // Display the spinner
                    }
                }
            };
        });

        quantityColumn.setCellValueFactory(cellData -> {
            OrderItem orderItem = cellData.getValue();
            if (orderItem != null) {
                return new SimpleIntegerProperty(orderItem.getQuantity()).asObject();  // Return the quantity of the OrderItem
            } else {
                return null;
            }
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

    @FXML
    void navToHP(ActionEvent event) {
        switchScreen("Home Page");
    }

    @FXML
    void navToPD(ActionEvent event) {
        currentDelivery.setOrderItems(orderItems);
        System.out.println(currentDelivery);
        switchScreen("Personal Details Filling");
    }


    // Initialize method to register for events
    @FXML
    void initialize() {
        System.out.println("Delivery initialized");

        // Register this class to listen for MenuEvent
        EventBus.getDefault().register(this);

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

        // Set up the RadioButton group for delivery/pickup
        ToggleGroup deliveryGroup = new ToggleGroup();
        deliveryRadio.setToggleGroup(deliveryGroup);
        pickupRadio.setToggleGroup(deliveryGroup);

        // Add listeners to the RadioButtons to update the total price when the selection changes
        deliveryRadio.selectedProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
        pickupRadio.selectedProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());

        // Listen for changes to the 'Delivery' radio button
        deliveryRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If "Delivery" is selected, show the message
                deliveryMessageLabel.setText("Delivery fee is 15 shekels.");
                deliveryMessageLabel.setVisible(true);
            } else {
                // If "Delivery" is not selected, hide the message
                deliveryMessageLabel.setVisible(false);
            }
        });


        // Fetch menu data
        try {
            SimpleClient.getClient().displayNetworkMenu();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
