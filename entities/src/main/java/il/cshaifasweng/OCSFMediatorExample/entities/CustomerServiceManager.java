package il.cshaifasweng.OCSFMediatorExample.entities;

public class CustomerServiceManager extends Employee{
    // Constructor ensures that employee type is only customer service
    public CustomerServiceManager(int id, String name, String address, String email,
                                   String username, String password, int branchID) {
        super(id, name, address, email, username, password, EmployeeType.CUSTOMER_SERVICE_MANAGER, branchID);
    }

    // Prevent changing employee type
    @Override
    public void setEmployeeType(EmployeeType employeeType) {
        if (employeeType != EmployeeType.CUSTOMER_SERVICE_MANAGER) {
            throw new UnsupportedOperationException("customer service manager must always be of type CUSTOMER_SERVICE_MANAGER");
        }
        super.setEmployeeType(employeeType);
    }
}
