package il.cshaifasweng.OCSFMediatorExample.entities;

public class DietitianEmployee extends Employee {

    public DietitianEmployee(int id, String name, String address, String email,
                             String username, String password, int branchID) {
        super(id, name, address, email, username, password, EmployeeType.DIETITIAN, branchID);
    }

    @Override
    public void setEmployeeType(EmployeeType employeeType) {
        throw new UnsupportedOperationException("DietitianEmployee must always be DIETITIAN");
    }

    // Extra methods for dietitians
}
