package il.cshaifasweng.OCSFMediatorExample.entities;


import javax.persistence.*;
import java.io.Serializable;
import java.util.*;


@Entity
@Table(name ="menuItems",uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID")})
public class MenuItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer itemID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private String ingredients;

    @Column
    private String preference;

    @Column //stores a list of branch IDs where the menuItem cam be delivered from
    private String deliverableBranchIds;

    @Lob
    private byte[] picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishType dishType;  // BASE or SPECIAL


    @ManyToMany(mappedBy = "menuItems", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
   List<Branch> branches =new ArrayList<>();

    @ManyToMany(mappedBy = "deliverableItems")
    private List<Branch> deliverableBranches = new ArrayList<>();



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
    public List<Branch> getDeliverableBranches() { return deliverableBranches; }
    public void setDeliverableBranches(List<Branch> deliverableBranches) { this.deliverableBranches = deliverableBranches; }

    public void printMenuItem(){
        System.out.println("Name:"+this.name+" price:"+this.price+
                " ingredients:"+this.ingredients+" preference:"+this.preference);

    }
}

