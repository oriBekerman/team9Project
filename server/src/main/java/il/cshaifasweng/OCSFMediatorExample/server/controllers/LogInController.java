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

        // Fetch employee by username
        Employee employee = this.employeeRepository.findByUsername(username);

        if (employee == null) {
            System.out.println("user not found");
            return "user not found";
        }

        // Check if password matches
        if (!employee.getPassword().equals(password)) {
            System.out.println("wrong password");
            return "wrong password";
        }
        System.out.println("login successful");
        return "login successful";
    }


    // Method to populate Customer and Employee tables if they are empty
    public void checkAndPopulateUsers() {
        try {

            // Check if the Employee table is empty
            if (employeeRepository.checkIfEmpty()) {
                List<Employee> employees = new ArrayList<>();

                // Prepopulate with 5 employees, using enum for employeeType
                Employee employee1 = new Employee(111111111, "Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "managerpass", EmployeeType.COMPANY_MANAGER, 1);
                Employee employee2 = new Employee(222222222, "Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "regularpass", EmployeeType.RESTAURANT_SERVICE, 1);
                Employee employee3 = new Employee(333333333, "Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "dietitianpass", EmployeeType.DIETITIAN, 2);
                Employee employee4 = new Employee(444444444, "Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "customerservicepass", EmployeeType.CUSTOMER_SERVICE, 3);
                Employee employee5 = new Employee(555555555, "Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "adminpass", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3);


                employees.add(employee1);
                employees.add(employee2);
                employees.add(employee3);
                employees.add(employee4);
                employees.add(employee5);

                // Save employees to the database
                employeeRepository.populate(employees);
            }

        } catch (Exception exception) {
            throw exception; // Rethrow to ensure rollback in case of an error
        }
    }
}

