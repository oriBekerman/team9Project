package il.cshaifasweng.OCSFMediatorExample.entities;

public class EmployeeFactory {
    public static Employee createEmployee(EmployeeType type, int id, String name, String address,
                                          String email, String username, String password, int branchID) {
        switch (type) {
            case CUSTOMER_SERVICE:
                return new CustomerServiceEmployee(id, name, address, email, username, password, branchID);
            case DIETITIAN:
                return new DietitianEmployee(id, name, address, email, username, password, branchID);
//            case CUSTOMER_SERVICE_MANAGER:
//                return new CustomerServiceManager(id, name, address, email, username, password, branchID);
            default:
                return new Employee(id, name, address, email, username, password, type, branchID);
        }
    }
}
