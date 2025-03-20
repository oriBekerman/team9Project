package il.cshaifasweng.OCSFMediatorExample.entities;

import java.util.List;

public class CustomerServiceEmployee extends Employee {

    private List<Complaint> complaints;

    // Constructor ensures that employee type is only customer service
    public CustomerServiceEmployee(int id, String name, String address, String email,
                                   String username, String password, int branchID) {
        super(id, name, address, email, username, password, EmployeeType.CUSTOMER_SERVICE, branchID);
    }
    // Prevent changing employee type
    @Override
    public void setEmployeeType(EmployeeType employeeType) {
        if (employeeType != EmployeeType.CUSTOMER_SERVICE) {
            throw new UnsupportedOperationException("CustServEmp must always be of type CUSTOMER_SERVICE");
        }
        super.setEmployeeType(employeeType);
    }
    public List<Complaint> getComplaints() {
        return complaints;
    }
    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }
    public void addComplaint(Complaint complaint) {
        this.complaints.add(complaint);
        complaint.setEmployee(this);
    }
    public void removeComplaint(Complaint complaint) {
        this.complaints.remove(complaint);
        complaint.setEmployee(null);
    }
}
