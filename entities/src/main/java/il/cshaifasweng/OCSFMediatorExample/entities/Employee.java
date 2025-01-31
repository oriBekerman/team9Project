package il.cshaifasweng.OCSFMediatorExample.entities;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "employees") // Specifies the table name for the Employee class in the database
public class Employee extends Person implements Serializable {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Store enum values as strings in the database
    @Column(nullable = false)
    private EmployeeType employeeType; // Use enum instead of String

    @Column(nullable = false)
    private int branchID;

    // Default constructor
    public Employee() {}

    public Employee(String name, String address, String email, String username, String password, EmployeeType employeeType, int branchID) {
        super(name, address, email); // Call the parent class constructor (Person)
        this.username = username;
        this.password = password;
        this.employeeType = employeeType;
        this.branchID = branchID;
    }


    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    public int getBranchID() {
        return branchID;
    }

    public void setBranchID(int branchID) {
        this.branchID = branchID;
    }

    // Print the Employee details
    public void printEmployeeDetails() {
        System.out.println("Username: " + this.username + ", Type: " + this.employeeType + ", Branch ID: " + this.branchID);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "username='" + username + '\'' +
                ", employeeType='" + employeeType + '\'' +
                ", branchID=" + branchID +
                "} " + super.toString();
    }
}
