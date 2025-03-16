package il.cshaifasweng.OCSFMediatorExample.entities.Employees;

public class CompanyManager extends Employee{
    // Constructor ensures that employee type is only customer service
    public CompanyManager(int id, String name, String address, String email,
                                   String username, String password, int branchID) {
        super(id, name, address, email, username, password, EmployeeType.COMPANY_MANAGER, branchID);
    }

    // Prevent changing employee type
    @Override
    public void setEmployeeType(EmployeeType employeeType) {
        if (employeeType != EmployeeType.COMPANY_MANAGER) {
            throw new UnsupportedOperationException("company manager must always be of type COMPANY_MANAGER");
        }
        super.setEmployeeType(employeeType);
    }
}
