package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.EmployeeRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class LogInController {


    private EmployeeRepository employeeRepository;

    // Constructor to inject the repositories
    public LogInController(SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new NullPointerException("In LoginController, sessionFactory is null");
        }
        System.out.println("In LoginController constructor");
        this.employeeRepository = new EmployeeRepository(sessionFactory);
    }

    public String verifyUser(String username, String password) {
        Employee employee = this.employeeRepository.findByUsername(username);

        if (employee == null) {
            return "User not found";
        }

        if (!employee.getPassword().equals(password)) {
            return "Wrong password";
        }

        return "Login successful";
    }

    public EmployeeType getEmployeeTypeByUsername(String username) {
        Employee employee = this.employeeRepository.findByUsername(username);
        if (employee != null) {
            return employee.getEmployeeType();
        }
        return null; // Return null if the user is not found
    }



    // Method to populate Customer and Employee tables if they are empty
    public void checkAndPopulateUsers(List<Employee> employees) {
        try {
            System.out.println("In checkAndPopulateUsers 1");
            // Check if the Employee table is empty
            if (employeeRepository.checkIfEmpty()) {
                // Save employees to the database
                System.out.println("In checkAndPopulateUsers 2");
                employeeRepository.populate(employees);
            }

        } catch (Exception exception) {
            throw exception; // Rethrow to ensure rollback in case of an error
        }
    }
}

