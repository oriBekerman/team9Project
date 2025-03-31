package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resSInfo")
public class ResInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resID;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "branch_id", referencedColumnName = "id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "reservation_tables",
            joinColumns = @JoinColumn(name = "reservation_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private Set<RestTable> tables = new HashSet<>();


    @Column(nullable = false)
    private LocalTime hours;

    @Column(nullable = false)
    private int numOfGuests;

    @Column(nullable = false)
    private String inOrOut;

    @Column(nullable = false)
    private Status status;

    @Column
    private String walk_in;

    @Transient
    public boolean customerIsSet=false;
    @Transient
    public boolean branchIsSet=false;
    @Transient
    public boolean tableIsSet=false;

    @Column(name = "isCancelled")
    private boolean isCancelled = false;

    public boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }


    // Default constructor
    public ResInfo() {}

    public ResInfo(Branch branch, Customer customer,LocalTime hours, int numOfGuests, String inOrOut, Set<RestTable> table) {
        this.branch = branch;
        this.customer = customer;
        this.hours = hours;
        this.numOfGuests = numOfGuests;
        this.inOrOut = inOrOut;
        this.tables = table;
        branchIsSet=true;
        customerIsSet=true;
        tableIsSet=true;
        for(RestTable t: table)
        {
            t.addUnavailableFromTime(hours);
        }
    }
    public ResInfo(LocalTime hours, int numOfGuests, String inOrOut) {
        this.hours = hours;
        this.numOfGuests = numOfGuests;
        this.inOrOut = inOrOut;
    }

    // Getters and Setters
    public Integer getResID() {
        return resID;
    }
    public void setResID(Integer resID) {
        this.resID = resID;
    }

    public Branch getBranch() {
        return branch;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
        branchIsSet=true;
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerIsSet=true;
    }

    public LocalTime getHours() {
        return hours;
    }
    public void setHours(LocalTime hours) {
        this.hours = hours;
    }

    public int getNumOfGuests() {
        return numOfGuests;
    }
    public void setNumOfGuests(int numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public String getInOrOut() {
        return inOrOut;
    }
    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public void setTable(Set<RestTable> tables) {
        this.tables = tables;
        tableIsSet=true;
        for (RestTable t: tables)
        {
            t.addUnavailableFromTime(hours);
        }
    }
    public Set<RestTable> getTable() {
        return tables;
    }
    public void setWalk_in(String walk_in) {
        this.walk_in = walk_in;
    }
    public String getWalk_in() {
        return walk_in;
    }
    @Override
    public String toString() {
        return "ResInfo{" +
                "resID=" + resID +
                ", Branch=" + branch +
                ", customer=" + customer +
                ", hours='" + hours + '\'' +
                ", numOfGuests=" + numOfGuests +
                ", inOrOut='" + inOrOut + '\'' +
                '}';
    }

    public enum Status {
        APPROVED,
        DENIED,
        WAIT,
        COMPLETED,
    }
}