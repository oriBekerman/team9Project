package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity

@Table(name = "branch", uniqueConstraints = {@UniqueConstraint(columnNames = "ID")})
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
            joinColumns = @JoinColumn(name = "BRANCH_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private List<MenuItem> deliverableItems = new ArrayList<>();


    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestTable> tables = new ArrayList<>();


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
    public List<MenuItem> getDeliverableItems() {
        return deliverableItems;
    }
    public void addMenuItem(MenuItem menuItem) { this.menuItems.add(menuItem); }
    public void addDeliverableItem(MenuItem menuItem) { this.deliverableItems.add(menuItem); }
    public int getBranchID() {
        return branchID;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
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
    public List<RestTable> getTables() {
        return tables;
    }

    public void setRestTables(List<RestTable> tables) {
        this.tables = tables;
    }
//    public List<RestTable> getAvailableTables(int capacity,LocalTime time)
//    {
//        List<RestTable> availableTables = new ArrayList<>();
//        System.out.println("in getAvailableTables branch");
//        for(RestTable table : tables){
//            if(table.getCapacity()==capacity && table.isAvailableAt(time)){
//                availableTables.add(table);
//            }
//        }
//        return availableTables;
//    }
//    public void printAvailableTables(int capacity,LocalTime time)
//    {
//        System.out.println("Available Tables:");
//        List<RestTable>availableTables = getAvailableTables(capacity,time);
//        for(RestTable table : availableTables){
//            table.print();
//            System.out.println("available at "+time);
//        }
//    }
}

