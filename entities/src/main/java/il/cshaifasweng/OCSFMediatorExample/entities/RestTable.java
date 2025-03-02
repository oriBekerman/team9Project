package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (name ="restTable",uniqueConstraints = {
        @UniqueConstraint(columnNames = "ID")})
public class RestTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer tableId;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String area;

    // Many RestTables belong to one Branch
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

// Stores the start times when the table becomes unavailable.
// Each unavailability period lasts for 1.5 hours from the recorded start time.
    @ElementCollection
    @CollectionTable(name = "table_unavailable_from", joinColumns = @JoinColumn(name = "rest_table_id"))
    @Column(name = "start_time")
    private List<LocalTime> unavailableFromTimes = new ArrayList<>();


    //location for gui seating chart
    @Embedded
    private Coordinates coordinates=new Coordinates();

    public RestTable(String area, int capacity, Branch branch, List<LocalTime> unavailableFromTimes, Coordinates coordinates)
    {
        this.area = area;
        this.capacity = capacity;
        this.branch = branch;
        this.unavailableFromTimes = unavailableFromTimes;
        this.coordinates = coordinates;
    }
    public RestTable(String area, int capacity)
    {
        this.area = area;
        this.capacity = capacity;
    }
    public RestTable() {}

    public int getId() {
        return tableId;
    }
    public String getArea() {
        return area;
    }
    public int getCapacity() {
        return capacity;
    }
    public Branch getBranch() {
        return branch;
    }
    public List<LocalTime> getUnavailableFromTimes() {
        return unavailableFromTimes;
    }
    public Coordinates getCoordinates() {
        return coordinates;
    }
    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    public void setUnavailableFromTimes(List<LocalTime> unavailableFromTimes) {
        this.unavailableFromTimes = unavailableFromTimes;
    }
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
    public void addUnavailableFromTime(LocalTime unavailableFromTime) {
        unavailableFromTimes.add(unavailableFromTime);
    }

}

