package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;

public class ActiveUser {
    private String username;
    private EmployeeType employeeType;

    // Constructor to initialize the active user
    public ActiveUser(String username, EmployeeType employeeType) {
        this.username = username;
        this.employeeType = employeeType;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for employee type
    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    // Setter for employee type
    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }
}
