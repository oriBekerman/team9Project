package il.cshaifasweng.OCSFMediatorExample.entities;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

import org.hibernate.Session;



public class MenuItemsController {

    @FXML
    private TableView<MenuItem> menuItemsTable;

    @FXML
    private TableColumn<MenuItem, Integer> itemIDColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, Double> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> ingredientsColumn;

    @FXML
    private TableColumn<MenuItem, String> preferenceColumn;

    public void initialize() {
        // הגדרת העמודות
        itemIDColumn.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        ingredientsColumn.setCellValueFactory(new PropertyValueFactory<>("ingredients"));
        preferenceColumn.setCellValueFactory(new PropertyValueFactory<>("preference"));

        // טוען נתונים למסד הנתונים
        loadMenuItems();
    }

    private void loadMenuItems() {
        // קבלת נתונים ממסד הנתונים
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<MenuItem> menuItems = session.createQuery("FROM MenuItem", MenuItem.class).list();
        session.close();

        // הוספת נתונים לטבלה
        ObservableList<MenuItem> menuItemsList = FXCollections.observableArrayList(menuItems);
        menuItemsTable.setItems(menuItemsList);
    }
}

