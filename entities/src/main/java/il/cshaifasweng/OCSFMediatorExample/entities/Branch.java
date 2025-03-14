package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.time.LocalTime;
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
    private Set<MenuItem> menuItems = new HashSet<>();


    // only deliverable menu items
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "branchDeliverableItems",
            joinColumns = @JoinColumn(name = "BRANCH_ID"),
            inverseJoinColumns = @JoinColumn(name = "ITEM_ID")
    )
    private Set<MenuItem> deliverableItems = new HashSet<>();



    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<RestTable> tables = new HashSet<>();

//    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private Set<ResInfo> reservations = new HashSet<>();

    public boolean tablesAreSet=false;


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

    public Set<MenuItem> getBranchMenuItems() { return menuItems; }
    public void setBranchMenuItems(Set<MenuItem> menuItems) { this.menuItems = menuItems; }
    public void setDeliverableItems(Set<MenuItem> deliverableItems) { this.deliverableItems = deliverableItems; }
    public Set<MenuItem> getDeliverableItems() {
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
    public Set<RestTable> getTables() {
        if(tables.isEmpty())
        {
            System.out.println("tables is empty");
        }
        if (tables==null)
        {
            System.out.println("tables is null");
        }
        return tables;
    }

    public void setRestTables(Set<RestTable> tables) {
        this.tables = tables;
        if (tables != null){
            if(tables.size()>0)
            {
                this.tablesAreSet=true;
            }
        }
    }
    public List<RestTable> getAvailableTablesWithCapacity(int capacity,LocalTime time)
    {
        List<RestTable> availableTables = new ArrayList<>();
        System.out.println("in getAvailableTables branch");
        for(RestTable table : tables){
            if(table.getCapacity()==capacity && table.isAvailableAt(time)){
                availableTables.add(table);
            }
        }
        return availableTables;
    }
    public void printAvailableTables(int capacity,LocalTime time)
    {
        System.out.println("Available Tables:");
        List<RestTable>availableTables = getAvailableTablesWithCapacity(capacity,time);
        for(RestTable table : availableTables){
            table.print();
            System.out.println("available at "+time);
        }
    }
    public Set<RestTable> getAvailableTablesAt(LocalTime time)
    {
        Set<RestTable> availableTables = new HashSet<>();
        for(RestTable table : tables){
            if(table.isAvailableAt(time)){
                availableTables.add(table);
            }
        }
        return availableTables;
    }
}


