package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import il.cshaifasweng.OCSFMediatorExample.client.Events.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Menu;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.entities.UpdateBranchSpecialItemRequest;
import java.lang.Thread;


public class SecondaryBoundary
{
    public List<Branch> branchList = null;
    public Label menuLabel;
    public AnchorPane root;
    @FXML
    private ResourceBundle resources;
    @FXML
    private Button isBranchDishBtn;
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

    private Branch currentbranch;
    private final Object lock = new Object();
    public void setBranch(Branch branch){
        currentbranch= branch;
    }
    @FXML

    void UpdateIngridients(ActionEvent event)
    {
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dish to update ingredients.");
            alert.showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog(selectedItem.getIngredients());
        dialog.setTitle("Update Ingredients");
        dialog.setHeaderText("Edit the ingredients for: " + selectedItem.getName());
        dialog.setContentText("Ingredients:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newIngredients ->
        {
            selectedItem.setIngredients(newIngredients);
            SimpleClient.getClient().updateDishIngredients(selectedItem);
        });
    }

    @Subscribe
    public void onUpdateIngredientsEvent(UpdateIngredientsEvent event)
    {
        Platform.runLater(() -> {
            MenuItem updatedItem = event.getUpdatedMenuItem();

            for (MenuItem item : allMenuItems) {
                if (item.getItemID() == updatedItem.getItemID())
                {
                    item.setIngredients(updatedItem.getIngredients());
                    break;
                }
            }
            menuTableView.refresh();
        });
    }
    @FXML
    void addDish(ActionEvent event)
    {
        // Step 1: Get the name of the new dish
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add New Dish");
        nameDialog.setHeaderText("Enter the name of the new dish:");
        Optional<String> nameResult = nameDialog.showAndWait();

        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty())
        {
            String dishName = nameResult.get();

            // Step 2: Get the ingredients of the dish
            TextInputDialog ingredientsDialog = new TextInputDialog();
            ingredientsDialog.setTitle("Add Ingredients");
            ingredientsDialog.setHeaderText("Enter the ingredients for: " + dishName);
            Optional<String> ingredientsResult = ingredientsDialog.showAndWait();

            if (ingredientsResult.isPresent() && !ingredientsResult.get().trim().isEmpty())
            {
                String dishIngredients = ingredientsResult.get();

                // Step 3: Get any preference for the dish
                TextInputDialog preferenceDialog = new TextInputDialog();
                preferenceDialog.setTitle("Add Preference");
                preferenceDialog.setHeaderText("Enter any preference for: " + dishName);
                Optional<String> preferenceResult = preferenceDialog.showAndWait();

                String dishPreference = preferenceResult.orElse("");

                // Step 4: Get the price of the dish
                TextInputDialog priceDialog = new TextInputDialog("0.0");
                priceDialog.setTitle("Add Price");
                priceDialog.setHeaderText("Enter the price for: " + dishName);
                Optional<String> priceResult = priceDialog.showAndWait();

                if (priceResult.isPresent() && !priceResult.get().trim().isEmpty())
                {
                    try
                    {
                        double price = Double.parseDouble(priceResult.get());

                        // Step 5: Get the type of the dish (BASE or SPECIAL)
                        ChoiceDialog<DishType> typeDialog = new ChoiceDialog<>(DishType.BASE, DishType.BASE, DishType.SPECIAL);
                        typeDialog.setTitle("Dish Type");
                        typeDialog.setHeaderText("Select the type of the dish:");
                        Optional<DishType> typeResult = typeDialog.showAndWait();

                        DishType dishType = typeResult.orElse(DishType.BASE);

                        // Step 6: If the dish is SPECIAL, select a branch
                        final Branch[] finalSelectedBranch = {null};
                        List<Branch> branchesToUpdate = new ArrayList<>();

                        if (dishType == DishType.SPECIAL)
                        {
                            List<String> branchNames = branchList.stream().map(Branch::getName).toList();
                            if (branchNames.isEmpty())
                            {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "No branches available.");
                                alert.showAndWait();
                                return;
                            }
                            ChoiceDialog<String> branchDialog = new ChoiceDialog<>(branchNames.get(0), branchNames);
                            branchDialog.setTitle("Select Branch");
                            branchDialog.setHeaderText("Select a branch:");
                            branchDialog.setContentText("Choose a branch:");

                            Optional<String> branchResult = branchDialog.showAndWait();
                            if (branchResult.isPresent())
                            {
                                String selectedBranchName = branchResult.get();
                                finalSelectedBranch[0] = branchList.stream()
                                        .filter(branch -> branch.getName().equals(selectedBranchName))
                                        .findFirst()
                                        .orElse(null);

                                if (finalSelectedBranch[0] == null) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "Selected branch not found.");
                                    alert.showAndWait();
                                    return;
                                }
                                branchesToUpdate.add(finalSelectedBranch[0]);
                            }
                            else
                            {
                                return;
                            }
                        }


                        byte[] defaultPicture = new byte[0];
                        final String finalDishName = dishName;
                        final double finalPrice = price;
                        final String finalDishIngredients = dishIngredients;
                        final String finalDishPreference = dishPreference;
                        final DishType finalDishType = dishType;


                        SimpleClient.getClient().addDishToDatabase(
                                new MenuItem(finalDishName, finalPrice, finalDishIngredients, finalDishPreference, defaultPicture, finalDishType),
                                branchesToUpdate
                        );


                        Platform.runLater(() ->
                        {
                            MenuItem newDish = new MenuItem(

                                    finalDishName,
                                    finalPrice,
                                    finalDishIngredients,
                                    finalDishPreference,
                                    defaultPicture,
                                    finalDishType
                            );

                            allMenuItems.add(newDish);
                            menuTableView.getItems().add(newDish);

                            if (finalDishType == DishType.SPECIAL )
                            {
                                System.out.println("Dish ID:---------------- " + newDish.getItemID());
                              try
                              {

                                  SimpleClient.getClient().updateBranchSpecialItem(finalSelectedBranch[0].getId(), newDish.getItemID());
                              }
                              catch (Exception e)
                              {
                                  e.printStackTrace();
                              }
                            }
                            else if (finalDishType == DishType.BASE )
                            {
                                System.out.println("Dish ID:---------------- " + newDish.getItemID());
                                for (Branch branch : branchList)
                                {
                                    branch.addMenuItem(newDish);
                                    try
                                    {
                                        SimpleClient.getClient().updateBranchSpecialItem(branch.getId(), newDish.getItemID());
                                    }
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    }
                    catch (NumberFormatException e)
                    {

                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid price format.");
                        alert.showAndWait();
                    }
                }
            }
        }
    }


    @Subscribe
    public void onBranchListSentEvent(BranchListSentEvent event)
    {
        this.branchList = event.branches;
    }

    @Subscribe
    public void onAddDishEvent(AddDishEvent event)
    {
        Platform.runLater(() ->
        {
            MenuItem addedItem = event.getAddedMenuItem();
            if (!allMenuItems.contains(addedItem))
            {
                allMenuItems.add(addedItem);
                menuTableView.getItems().add(addedItem);
            }
        });
    }

    @FXML
    void removeDish(ActionEvent event)
    {
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dish to remove.");
            alert.showAndWait();
            return;
        }
        allMenuItems.remove(selectedItem);
        menuTableView.getItems().remove(selectedItem);
        SimpleClient.getClient().removeDishFromDatabase(selectedItem);
    }

    @Subscribe
    public void onRemoveDishEvent(RemoveDishEvent event) {
        Platform.runLater(() ->
        {
            MenuItem removedItem = event.getRemovedMenuItem();
            allMenuItems.remove(removedItem);
            menuTableView.getItems().remove(removedItem);
        });
    }

    private ObservableList<MenuItem> allMenuItems = javafx.collections.FXCollections.observableArrayList();
    private Map<MenuItem, TextField> priceFieldMap = new HashMap<>();

    @Subscribe
    public void onMenuEvent(MenuEvent event)
    {
        Menu menu = event.getMenu();
        System.out.println("Got menu event in SecondaryBoundary");
        menuTableView.refresh();
        Platform.runLater(() ->
        {
            menuTableView.refresh();
            menuTableView.getItems().clear();
            menuTableView.getItems().setAll(menu.getMenuItems());
            allMenuItems.setAll(menu.getMenuItems());
            menuTableView.refresh();
        });
    }

    @Subscribe
    public void onUpdateEvent(updateDishEvent event)
    {
        try
        {
            if(currentbranch == null) {
                SimpleClient.getClient().displayNetworkMenu();
            }
            else{
                SimpleClient.getClient().displayBranchMenu(currentbranch);
            }
            menuTableView.refresh();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void BackToHPfunc(ActionEvent event) throws IOException
    {
        onExit();
        App.setRoot("primary");  // Switch to the primary screen
    }

    @FXML
    void SaveTheUpdateMenu(ActionEvent event) throws IOException
    {
        Map<Integer, Double> updatedPrices = new HashMap<>();
        for (Map.Entry<MenuItem, TextField> entry : priceFieldMap.entrySet())
        {
            MenuItem item = entry.getKey();
            TextField priceField = entry.getValue();

            if (!priceField.isDisabled())
            {
                try
                {
                    double newPrice = Double.parseDouble(priceField.getText());

                    if (newPrice != item.getPrice())
                    {
                        updatedPrices.put(item.getItemID(), newPrice);
                        item.setPrice(newPrice);
                    }
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid price input for item: " + item.getName());
                }
            }
        }

        for (Map.Entry<Integer, Double> entry : updatedPrices.entrySet()) {
            SimpleClient.getClient().editMenu(entry.getKey().toString(), entry.getValue().toString());
            System.out.println("Item ID: " + entry.getKey() + " New Price: " + entry.getValue());
        }

        for (TextField priceField : priceFieldMap.values()) {
            priceField.setDisable(true);
        }

        Platform.runLater(() ->
        {
            menuTableView.refresh();
            UpdatePriceBtn.setDisable(true);
            UpdatePriceBtn.requestFocus();
        });
    }

    @FXML
    void UpdateThePrice(ActionEvent event)
    {
        Platform.runLater(() ->
        {
            for (TextField priceField : priceFieldMap.values())
            {
                priceField.setDisable(false);
            }
            UpdatePriceBtn.setDisable(true);
        });
    }

    public void onExit()
    {
        EventBus.getDefault().unregister(this);
        System.out.println("Unregistered from EventBus");
    }

    @Subscribe
    public void onAcknowledgmentEvent(AcknowledgmentEvent event)
    {
        System.out.println("Enabling button...");
        UpdatePriceBtn.setDisable(true);
    }

    @FXML
    void isBranchDish(ActionEvent event)
    {
        MenuItem selectedItem = menuTableView.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a dish to update its type.");
            alert.showAndWait();
            return;
        }

        DishType currentType = selectedItem.getDishType();
        DishType newType = (currentType == DishType.BASE) ? DishType.SPECIAL : DishType.BASE;
        selectedItem.setDishType(newType);

        if (newType == DishType.SPECIAL)
        {
            List<String> branchNames = branchList.stream().map(Branch::getName).toList();
            if (branchNames.isEmpty())
            {
                Alert noBranchesAlert = new Alert(Alert.AlertType.WARNING, "No branches available.");
                noBranchesAlert.showAndWait();
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(branchNames.get(0), branchNames);
            dialog.setTitle("Select Branch");
            dialog.setHeaderText("This dish is now special. Please select a branch.");
            dialog.setContentText("Choose a branch:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(branchName -> {
                Branch selectedBranch = branchList.stream()
                        .filter(branch -> branch.getName().equals(branchName))
                        .findFirst()
                        .orElse(null);

                if (selectedBranch != null)
                {
                    selectedBranch.addMenuItem(selectedItem);
                    try
                    {

                        SimpleClient.getClient().updateBranchSpecialItem(selectedBranch.getId(), selectedItem.getItemID());

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    System.out.println("Added dish " + selectedItem.getName() + " to branch " + selectedBranch.getName());
                }
            });
        }
        else if (newType == DishType.BASE)
        {

            for (Branch branch : branchList)
            {
                branch.addMenuItem(selectedItem);
                try {

                    SimpleClient.getClient().updateBranchSpecialItem(branch.getId(), selectedItem.getItemID());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Added base dish " + selectedItem.getName() + " to branch " + branch.getName());
            }
        }

        SimpleClient.getClient().updateDishType(selectedItem);

        EventBus.getDefault().post(new UpdateDishTypeEvent(selectedItem));
        menuTableView.refresh();
    }


    public void updateBranchSpecialItem(int branchId, int menuItemId)
    {
        try {
            SimpleClient.getClient().sendToServer(new UpdateBranchSpecialItemRequest(branchId, menuItemId));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onUpdateDishTypeEvent(UpdateDishTypeEvent event) {
        Platform.runLater(() -> {
            MenuItem updatedItem = event.getUpdatedMenuItem();

            for (MenuItem item : allMenuItems) {
                if (item.getItemID() == updatedItem.getItemID()) {
                    item.setDishType(updatedItem.getDishType());
                    break;
                }
            }
            menuTableView.refresh();
        });
    }

    @FXML
    void performSearch(ActionEvent event)
    {
        String query = searchField.getText().toLowerCase().trim();
        System.out.println("Search Query: " + query); // Debugging

        if (query.isEmpty())
        {
            System.out.println("Showing all items");
            menuTableView.getItems().setAll(allMenuItems);
            menuTableView.refresh();
            return;
        }
        ObservableList<MenuItem> filteredList;
        try
        {
            double maxPrice = Double.parseDouble(query);
            System.out.println("Searching for items with price ≤ " + maxPrice);
            filteredList = allMenuItems.filtered(item -> item.getPrice() <= maxPrice);
        }
        catch (NumberFormatException e)
        {
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

    @Subscribe
    public void handleEnableUpdatePriceBtnEvent(AcknowledgmentEvent event)
    {
        Platform.runLater(() ->
        {
            UpdatePriceBtn.setDisable(false);
        });
    }

    @FXML
    void initialize()
    {
        System.out.println("SecondaryController initialized");

        if (!EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().register(this);
        }
        try
        {
            if(currentbranch == null)
            {
                SimpleClient.getClient().displayNetworkMenu();
            }
            else{
                SimpleClient.getClient().displayBranchMenu(currentbranch);
            }

        }
        catch (IOException e)
        {
            System.err.println("Error sending client confirmation: " + e.getMessage());
        }
        menuTableView.refresh();

        if (branchList==null)
        {
            System.out.println("get branches");
            SimpleClient.getClient().getBranchList();
        }
        menuTableView.refresh();
        SimpleClient.setSecondaryControllerInitialized();
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        ingredientsColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIngredients()));
        preferenceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPreference()));
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        dishTypeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDishTypeString ()));

        if (SimpleClient.getClient().getActiveUser() != null) {
            if (SimpleClient.getClient().getActiveUser().getEmployeeType() == EmployeeType.DIETITIAN)
            {
                UpdatePriceBtn.setVisible(true);
                UpdateingridientsBtn.setVisible(true);
                addDishBtn.setVisible(true);
                removeDishBtn.setVisible(true);
                isBranchDishBtn.setVisible(true);
            }
            else
            {
                UpdatePriceBtn.setVisible(false);
                //SaveBtn.setVisible(false);
                UpdateingridientsBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
                addDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
                removeDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
                isBranchDishBtn.setVisible(false);
            }
        }
        else
        {
            UpdatePriceBtn.setVisible(false); // Hide Update button if not logged in
            UpdateingridientsBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            addDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            removeDishBtn.setVisible(false);  // Show Update button if user is a DIETITIAN
            isBranchDishBtn.setVisible(false);
        }
        priceColumn.setCellFactory(col -> new TableCell<MenuItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                }
                else
                {
                    TextField priceField = new TextField(price.toString());
                    priceField.setDisable(true);  // Initially disable the price field
                    priceFieldMap.put(getTableView().getItems().get(getIndex()), priceField);
                    setGraphic(priceField);
                }
            }
        });

        imageColum.setCellValueFactory(cellData ->
        {
            byte[] imageBytes = cellData.getValue().getPicture();
            if (imageBytes != null && imageBytes.length > 0)
            {
                InputStream is = new ByteArrayInputStream(imageBytes);
                Image image = new Image(is);
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(80);
                imageView.setFitWidth(80);
                imageView.setPreserveRatio(true);
                return new javafx.beans.property.SimpleObjectProperty<>(imageView);
            } else
            {
                return new javafx.beans.property.SimpleObjectProperty<>(null);
            }
        });

        Platform.runLater(() ->
        {
            menuTableView.getItems().clear(); // Clear previous items
            UpdatePriceBtn.setDisable(true);  // Re-enable the update button
            UpdatePriceBtn.requestFocus();  // Focus the update button
        });
        setStyle();
    }
    private void setStyle()
    {
        root.setStyle("-fx-background-color: #fbe9d0;");
        for (Node node : root.getChildrenUnmodifiable())
        {
            if(node instanceof Button)
            {
                node.setStyle(" -fx-font-size: 16px;\n" +
                        "    -fx-font-weight: bold;\n" +
                        "    -fx-text-fill: white;\n" +
                        "    -fx-background-color: #8a6f48;\n" +
                        "    -fx-alignment: center;\n" +
                        "    -fx-padding: 8px 16px;\n" +
                        "    -fx-border-radius: 6px;\n" +
                        "    -fx-cursor: hand;");
            }
            if (node instanceof TableColumnHeader)
            {
                node.setStyle(" -fx-font-size: 16px;\n" +
                        "    -fx-text-fill: #4e453c;\n" +
                        "    -fx-padding: 8px;\n" +
                        "    -fx-font-weight: bold;;");
            }
        }
    }
}