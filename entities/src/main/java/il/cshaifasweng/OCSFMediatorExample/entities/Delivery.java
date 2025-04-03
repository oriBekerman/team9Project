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
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderNumber", unique = true)
    private Integer orderNumber;

    @Column(name = "time")
    private String time;

    // One-to-many relationship with OrderItem
    @OneToMany(mappedBy = "delivery")
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryMethod deliveryMethod;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "total_price")
    private double totalPrice = 0.0;

    @Column(name = "is_canceled")
    private boolean isCanceled = false;

    public static final double DELIVERY_FEE = 15.0;

    // Default constructor
    public Delivery() {}

    // Constructor
    public Delivery(List<OrderItem> orderItems, Customer customer, DeliveryMethod deliveryMethod, Branch branch, String time) {
        this.orderItems = orderItems;
        this.customer = customer;
        this.deliveryMethod = deliveryMethod;
        this.branch = branch;
        this.totalPrice = calculateTotalPrice(); // Calculate total price based on items
        this.time =time;
    }

    // Calculate total price dynamically
    public double calculateTotalPrice() {
        double total = 0.0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getMenuItem().getPrice() * orderItem.getQuantity(); // MenuItem price * quantity
        }
        if (this.deliveryMethod == DeliveryMethod.DELIVERY) {
            total += DELIVERY_FEE; // Add delivery fee if the method is DELIVERY
        }
        return total;
    }

    // Getters and Setters
    public int getDeliveryNumber() {
        return orderNumber;
    }
    public void setTime(String time) {
        this.time = time;
    }


    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getTime() {
        return time;
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

    // Setter for deliveryTime
    public void setDeliveryTime(String time) {
        this.time = time;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public String toString() {
        return "Delivery{" +
                "orderNumber=" + orderNumber +
                ", time='" + time + '\'' +
                ", orderItems=" + orderItems +
                ", customer=" + customer +
                ", deliveryMethod=" + deliveryMethod +
                ", branch name=" + branch.getName() +
                ", totalPrice=" + totalPrice +
                '}';
    }
}