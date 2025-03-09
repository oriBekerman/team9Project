package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.DeliveryMethod;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Deliveries")
public class Delivery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderNumber;

    @Column(nullable = false)
    private String date;

    // One-to-many relationship with OrderItem
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems; // A list of OrderItem objects

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryMethod deliveryMethod;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private double totalPrice;

    public static final double DELIVERY_FEE = 15.0;

    // Default constructor
    public Delivery() {}

    // Constructor
    public Delivery(String date, List<OrderItem> orderItems, Customer customer, DeliveryMethod deliveryMethod, Branch branch) {
        this.date = date;
        this.orderItems = orderItems;
        this.customer = customer;
        this.deliveryMethod = deliveryMethod;
        this.branch = branch;
        this.totalPrice = calculateTotalPrice(); // Calculate total price based on items
    }

    // Calculate total price dynamically
    public double calculateTotalPrice() {
        double total = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getMenuItem().getPrice() * orderItem.getQuantity(); // MenuItem price * quantity
        }
        if (this.deliveryMethod == DeliveryMethod.DELIVERY) {
            total += DELIVERY_FEE; // Add delivery fee if the method is DELIVERY
        }
        return total;
    }

    // Getters and Setters
    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        this.totalPrice = calculateTotalPrice(); // Recalculate total price when items change
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(DeliveryMethod deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
        this.totalPrice = calculateTotalPrice(); // Recalculate total price when delivery method changes
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "orderNumber=" + orderNumber +
                ", date='" + date + '\'' +
                ", orderItems=" + orderItems +
                ", customer=" + customer +
                ", deliveryMethod=" + deliveryMethod +
                ", branch name=" + branch.getName() +
                ", totalPrice=" + totalPrice +
                '}';
    }
}