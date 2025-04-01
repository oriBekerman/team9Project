package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.time.LocalTime;
import java.util.*;

@Entity

@Table(name = "branch", uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID")})
public class Branch implements Serializable  {
    private static final long serialVersionUID = 1L;

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

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<ResInfo> reservations = new HashSet<>();


    @Transient
    public boolean tablesAreSet=false;


    public Branch() {}

    public Branch(String name, String location, String openingTime, String closingTime) {
        this.name = name;
        this.location = location;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.tablesAreSet=true;
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
    public void setRestTables(List<RestTable> newTables) {
        Set<RestTable> restTables=new HashSet<>();
        for(RestTable table : newTables)
        {
            restTables.add(table);
        }
        this.tables=restTables;
        if (tables != null){
            if(!tables.isEmpty())
            {
                this.tablesAreSet=true;
            }
        }

    }
    public List<RestTable> getTablesSortedByID() {
        List<RestTable> tablesByID = new ArrayList<>(tables); // copy the list
        tablesByID.sort(Comparator.comparingInt(RestTable::getId)); // sort by ID
        return tablesByID;
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
    public Set<RestTable> getAvailableTablesWithArea(String area,LocalTime time)
    {
        Set<RestTable> availableTables = new HashSet<>();
        System.out.println("in getAvailableTables branch");
        for(RestTable table : tables){
            if(table.getArea().equals(area) && table.isAvailableAt(time)){
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
    public Set<RestTable> getUnavailableTablesAt(LocalTime time)
    {
        Set<RestTable> unavailableTables = new HashSet<>();
        for(RestTable table : tables){
            if(!table.isAvailableAt(time)){
                unavailableTables.add(table);
            }
        }
        return unavailableTables;
    }

    //returns the best table/ combination of tables for the given time and number of people and sitting area
    public Set<RestTable> getAvailableTablesWithNumPeople(int numPeople, LocalTime time, String area) {
        Set<RestTable> availableTables = new HashSet<>();

        // Get available tables at the given time in the area
        Set<RestTable> availableAtTime = getAvailableTablesWithArea(area, time);
        if (availableAtTime.isEmpty()) {
            System.out.println("No available tables in area " + area + " at time " + time);
            return availableTables;
        }

        // Convert to list for easier sorting and processing
        List<RestTable> sortedTables = new ArrayList<>(availableAtTime);

        //  Try to find a single exact match
        for (RestTable table : sortedTables) {
            if (table.getCapacity() == numPeople) {
                availableTables.add(table);
                System.out.println("Exact match found: Table ID " + table.getId() + ", Capacity: " + table.getCapacity());
                return availableTables;
            }
        }

        // Try to find a combination of tables that sum exactly to numPeople
        Set<RestTable> bestCombination = null;
        int minWastedCapacity = Integer.MAX_VALUE;

        // Power set approach to find best fit
        int n = sortedTables.size();
        for (int mask = 0; mask < (1 << n); mask++) {
            Set<RestTable> currentCombination = new HashSet<>();
            int totalCapacity = 0;

            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) { // Check if this table is included
                    RestTable table = sortedTables.get(i);
                    currentCombination.add(table);
                    totalCapacity += table.getCapacity();
                }
            }

            // Check if it's an exact match
            if (totalCapacity == numPeople) {
                System.out.println("Exact combination found!");
                return currentCombination;
            }

            // If it's not an exact match, check how much we waste
            int wasted = totalCapacity - numPeople;
            if (wasted >= 0 && wasted < minWastedCapacity) {
                minWastedCapacity = wasted;
                bestCombination = currentCombination;
            }
        }

        // If we didn't find an exact match, return the best fit
        if (bestCombination != null) {
            availableTables.addAll(bestCombination);
            System.out.println("Best fit found with minimum wasted capacity of " + minWastedCapacity);
        } else {
            System.out.println("No suitable table combination found.");
        }

        return availableTables;
    }

    public ResInfo createReservation(Customer customer, int numGuests, String area, LocalTime time) {
        Set<RestTable> tables = getAvailableTablesWithNumPeople(numGuests, time, area);
        List<Integer> tableIds=new ArrayList<>();

        if (tables.isEmpty()) return null;

        ResInfo reservation = new ResInfo(this, customer, time, numGuests, area, tables);
        reservation.setStatus(ResInfo.Status.APPROVED);

        for (RestTable table : tables) {
            table.addUnavailableFromTime(time);
            tableIds.add(table.getId());
        }

        addReservation(reservation, tables,tableIds);
        return reservation;
    }
    public synchronized void addReservation(ResInfo reservation, Set<RestTable> newTables, List<Integer> tableIds) {
        reservations.add(reservation);
        reservation.setBranch(this);
        tablesAreSet = false;

        // Collect tables to remove based on IDs
        Set<RestTable> toRemove = new HashSet<>();
        for (RestTable table : tables) {
            if (tableIds.contains(table.getId())) {
                toRemove.add(table);
            }
        }
        tables.removeAll(toRemove);
        tables.addAll(newTables);
        tablesAreSet = true;
        this.notifyAll();  // Notify any thread waiting for tables
    }



    public Set<ResInfo> getAllReservations()
    {
        return reservations;
    }
    public void cancelReservation(ResInfo reservation, Set<RestTable> newTables, List<Integer> tableIds)
    {
        reservations.remove(reservation);
        reservation.setBranch(this);
        tablesAreSet = false;
        Set<RestTable> toRemove = new HashSet<>();
        for (RestTable table : tables)
        {
            if (tableIds.contains(table.getId()))
            {
                toRemove.add(table);
            }
        }
        tables.removeAll(toRemove);
        tables.addAll(newTables);
        tablesAreSet = true;
        this.notifyAll();
    }
}


