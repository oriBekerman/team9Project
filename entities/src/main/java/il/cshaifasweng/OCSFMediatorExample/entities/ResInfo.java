package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;

@Entity
@Table(name = "resSInfo")
public class ResInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer resID;

    @ManyToOne  // Assuming Branch is an entity
    @JoinColumn(name = "branch_id", referencedColumnName = "id")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    @Column(nullable = false)
    private String resDate;

    @Column(nullable = false)
    private String hours;

    @Column(nullable = false)
    private int numOfGuests;

    @Column(nullable = false)
    private String inOrOut;

    // Default constructor
    public ResInfo() {}

    // Constructor with fields
    public ResInfo(Branch branch,Customer customer, String resDate, String hours, int numOfGuests, String inOrOut) {
        this.branch = branch;
        this.customer = customer;
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
    }

    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getResDate() {
        return resDate;
    }
    public void setResDate(String date) {
        this.resDate = date;
    }

    public String getHours() {
        return hours;
    }
    public void setHours(String hours) {
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
}
