package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "MenuItems")
public class MenuItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int itemID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String ingredients;

    @Column
    private String preference;

    @Lob
    private byte[] picture;

    // Constructor with all fields except itemID (auto-generated)
    public MenuItem(String name, double price, String ingredients, String preference, byte[] picture) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.preference = preference;
        this.picture = picture;
    }

    public MenuItem() {
        // בנאי ריק נדרש על ידי Hibernate
    }


    // Getters and Setters
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
    public void printMenuIteam(){
        System.out.println(this.name+
        this.price+
        this.ingredients+
        this.preference+
        this.picture);
    }
}

