package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import javax.persistence.Entity;
import java.time.LocalTime;
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

    @OneToOne(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private Menu menu;

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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

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
}

