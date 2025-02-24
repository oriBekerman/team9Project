package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity

@Table(name = "branch", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID")})
public class Branch implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer branchID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String openingTime; // Opening hour

    @Column(nullable = false)
    private String closingTime; // Closing hour

//all branch menu items
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    @JoinTable(name = "branchSpecialItems",
            joinColumns = @JoinColumn(name = "branch_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id", referencedColumnName = "ID"))
    private List<MenuItem> menuItems = new ArrayList<>();

    // only deliverable menu items
    @ManyToMany
    @JoinTable(
            name = "branchDeliverableItems",
            joinColumns = @JoinColumn(name = "branchID"),
            inverseJoinColumns = @JoinColumn(name = "ItemID")
    )
    private List<MenuItem> deliverableItems = new ArrayList<>();


    public Branch() {}

    public Branch(String name, String location, String openingTime, String closingTime) {
        this.name = name;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    // Getters and Setters
    public int getId() {
        return branchID;
    }

    public void setId(int id) {
        this.branchID = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<MenuItem> getBranchMenuItems() { return menuItems; }
    public void setBranchMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
    public void setDeliverableItems(List<MenuItem> deliverableItems) { this.deliverableItems = deliverableItems; }
    public List<MenuItem> getDeliverableItems() { return deliverableItems; }
    public void addMenuItem(MenuItem menuItem) { this.menuItems.add(menuItem); }
    public void addDeliverableItem(MenuItem menuItem) { this.deliverableItems.add(menuItem); }
    public int getBranchID() {
        return branchID;
    }

    public String getOpeningTime() {
        return openingTime.toString();
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime.toString();
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }
    public List<MenuItem> getBranchSpecial(){
        List<MenuItem> special = new ArrayList<MenuItem>();
        for(MenuItem menuItem : menuItems){
            if (menuItem.getDishType().equals(il.cshaifasweng.OCSFMediatorExample.entities.DishType.SPECIAL)){
                special.add(menuItem);
            }
        }
        return special;
    }
}

