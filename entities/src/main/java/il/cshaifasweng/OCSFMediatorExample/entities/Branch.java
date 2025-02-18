package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
public class Branch implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String openingTime; // Opening hour

    @Column(nullable = false)
    private String closingTime; // Closing hour

    @ManyToMany
    @JoinTable(
            name = "branch_menu_items",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> menuItems;

    public Branch() {}

    public Branch(String name, String location, String openingTime, String closingTime) {
        this.name = name;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getBranchID() {
        return id;
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
            if (menuItem.getDishType().equals(DishType.SPECIAL)){
                special.add(menuItem);
            }
        }
        return special;
    }
}

