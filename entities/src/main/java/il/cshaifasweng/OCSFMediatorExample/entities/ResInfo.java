package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "resSInfo")
public class ResInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resID;

    @ManyToOne  // Assuming Branch is an entity
    @JoinColumn(name = "branch_id", referencedColumnName = "ID")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name="tableId", referencedColumnName = "ID")
    RestTable table;

    @Column(nullable = false)
    private LocalDate resDate;

    @Column(nullable = false)
    private LocalTime hours;

    @Column(nullable = false)
    private int numOfGuests;

    @Column(nullable = false)
    private String inOrOut;

    @Column(nullable = false)
    private Status status;

    public boolean customerIsSet=false;
    public boolean branchIsSet=false;
    public boolean tableIsSet=false;


    // Default constructor
    public ResInfo() {}

    // Constructor with fields
    public ResInfo(Branch branch, Customer customer, LocalDate resDate, LocalTime hours, int numOfGuests, String inOrOut, RestTable table) {
        this.branch = branch;
        this.customer = customer;
        this.resDate = resDate;
        this.hours = hours;
        this.numOfGuests = numOfGuests;
        this.inOrOut = inOrOut;
        this.table = table;
        branchIsSet=true;
        customerIsSet=true;
        tableIsSet=true;
    }
    public ResInfo(LocalDate resDate, LocalTime hours, int numOfGuests, String inOrOut) {
        this.resDate = resDate;
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
        System.out.println("[ResInfo - setBranch] Branch set to: " + branch.getName() + " (id: " + branch.getId() + ")");
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerIsSet=true;
    }

    public LocalDate getResDate() {
        return resDate;
    }
    public void setResDate(LocalDate date) {
        this.resDate = date;
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
    public void setTable(RestTable table) {
        this.table = table;
        tableIsSet=true;
    }
    public RestTable getTable() {
        return table;
    }
    @Override
    public String toString() {
        return "ResInfo{" +
                "resID=" + resID +
                ", Branch=" + branch +
                ", customer=" + customer +
                ", resDate='" + resDate + '\'' +
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
