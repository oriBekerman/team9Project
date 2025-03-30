package il.cshaifasweng.OCSFMediatorExample.entities;

import il.cshaifasweng.OCSFMediatorExample.entities.Employees.Employee;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Integer complaintId;

    @Column(nullable = false)
    private LocalDateTime complaintDate;

    @Column(nullable = false, length = 1000)
    private String complaintText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee; // Employee handling the complaint

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch; // Branch associated with the complaint

    @Column
    long compensation;

    public boolean customerIsSet=false;

    // Constructors
    public Complaint() {}

    public Complaint(String complaintText, ComplaintStatus status, Customer customer,Branch branch) {
        this.complaintDate = LocalDateTime.now();
        this.complaintText = complaintText;
        this.status = status;
        this.customer = customer;
        this.branch = branch;
    }
    public Complaint(String complaintText, ComplaintStatus status){
        this.complaintDate = LocalDateTime.now();
        this.complaintText = complaintText;
        this.status = status;
    }
    // Getters and Setters
    public Integer getComplaintId() {
        return complaintId;
    }
    public void setComplaintId(Integer complaintId) {
        this.complaintId = complaintId;
    }
    public LocalDateTime getComplaintDate() {
        return complaintDate;
    }
    public void setComplaintDate(LocalDateTime complaintDate) {
        this.complaintDate = complaintDate;
    }
    public String getComplaintText() {
        return complaintText;
    }
    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }
    public ComplaintStatus getStatus() {
        return status;
    }
    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    public Branch getBranch() {
        return branch;
    }
    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    public void printComplaint()
    {
        System.out.println("complaint:" + String.valueOf(complaintId));
        System.out.println("Customer:");
        System.out.println(customer.toString());
        System.out.println("Message: "+complaintText);
        System.out.println("Status: "+String.valueOf(status));
        System.out.println("Date: "+String.valueOf(complaintDate));
    }
    public long getCompensation() {
        return compensation;
    }
    public void setCompensation(long compensation) {
        this.compensation = compensation;
    }
}
