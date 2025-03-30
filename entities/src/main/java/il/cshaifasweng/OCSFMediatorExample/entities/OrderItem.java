package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem; // Many-to-one relationship with MenuItem

    private int quantity=0; // Quantity of the item
    private String preferences=""; // Preferences for the item (e.g., special instructions)

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;


    // Default constructor
    public OrderItem() {}

    // Constructor with fields
    public OrderItem(MenuItem menuItem, int quantity, String preferences, Delivery delivery) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.preferences = preferences;
        this.delivery = delivery;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "menuItem=" + menuItem +
                ", quantity=" + quantity +
                ", preferences='" + preferences + '\'' +
                '}';
    }
}