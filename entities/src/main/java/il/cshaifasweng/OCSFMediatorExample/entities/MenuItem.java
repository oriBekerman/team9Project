package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name ="menuItems")
public class MenuItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int itemID;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishType dishType;  // BASE or SPECIAL


    @ManyToMany(mappedBy = "menuItems")
    private List<Branch> branches;


    // Constructor with all fields except itemID (auto-generated)
    public MenuItem(String name, double price, String ingredients, String preference, byte[] picture,DishType dishType) {
        this.name = name;
        this.price = price;
        this.ingredients = ingredients;
        this.preference = preference;
        this.picture = picture;
        this.dishType = dishType;
    }

    public MenuItem() {
        // בנאי ריק נדרש על ידי Hibernate
    }

     //Getters and Setters

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
    public DishType getDishType() {
        return dishType;
    }
    public void setDishType(DishType dishType) {
        this.dishType = dishType;
    }
    public List<Branch> getBranches() { return branches; }
    public void setBranches(List<Branch> branches) { this.branches = branches; }

    public void printMenuItem(){
        System.out.println("Name:"+this.name+" price:"+this.price+
                " ingredients:"+this.ingredients+" preference:"+this.preference);

    }
}

