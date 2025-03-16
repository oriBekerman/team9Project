package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
@ElementCollection(fetch = FetchType.EAGER)
@CollectionTable(name = "table_unavailable_from", joinColumns = @JoinColumn(name = "rest_table_id"))
@Column(name = "start_time")
private Set<LocalTime> unavailableFromTimes = new HashSet<>();



//    //location for gui seating chart
//    @Embedded
//    private Coordinates coordinates=new Coordinates();

    public RestTable(String area, int capacity, Branch branch, Set<LocalTime> unavailableFromTimes)
    {
        this.area = area;
        this.capacity = capacity;
        this.branch = branch;
        this.unavailableFromTimes = unavailableFromTimes;
//        this.coordinates = coordinates;
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
    public Set<LocalTime> getUnavailableFromTimes() {
        return unavailableFromTimes;
    }
//    public Coordinates getCoordinates() {
//        return coordinates;
//    }
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
    public void setUnavailableFromTimes(Set<LocalTime> unavailableFromTimes) {
        this.unavailableFromTimes = unavailableFromTimes;
    }
//    public void setCoordinates(Coordinates coordinates) {
//        this.coordinates = coordinates;
//    }
    public void addUnavailableFromTime(LocalTime unavailableFromTime) {
        unavailableFromTimes.add(unavailableFromTime);
    }

    public void removeUnavailableFromTime(LocalTime unavailableFromTime) {
        unavailableFromTimes.remove(unavailableFromTime);
    }
    public boolean isAvailableAt(LocalTime time)
    {
        List<LocalTime> times=getTimeRange(time);
        for(LocalTime t:times)
        {
            if(unavailableFromTimes.contains(t))
            {
                return false;
            }
        }
        return true;
    }
    public List<LocalTime> getAvailableFromTimes() {
        String start= branch.getOpeningTime();
        String end= branch.getClosingTime();
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        List<LocalTime> availableFromTimes = new ArrayList<>();
        LocalTime time = startTime;
        while (time!=endTime)
        {
            if(!unavailableFromTimes.contains(time))
            {
                availableFromTimes.add(time);
            }
            if(unavailableFromTimes.contains(time))
            {
                time=time.plusHours(1).plusMinutes(15);
            }
            time=time.plusMinutes(15);

        }
        return availableFromTimes;
    }
    public List<LocalTime> getTimeRange(LocalTime time)
    {
        List<LocalTime>times=new ArrayList<>();
        LocalTime t=time.minusHours(1).minusMinutes(15);
        while(t.isBefore(time.plusHours(1).plusMinutes(15)))
        {
            times.add(t);
            t=t.plusMinutes(15);
        }
        return times;
    }

    public void print() {
        System.out.println("Table: "+tableId);
        System.out.println("Area: "+area);
        System.out.println("Capacity: "+capacity);
        System.out.println("Branch: "+branch.getName());
    }
}

