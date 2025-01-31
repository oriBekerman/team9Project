package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.CustomerRepository;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.EmployeeRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class LogInController {

    private CustomerRepository customerRepository;
    private EmployeeRepository employeeRepository;

    // Constructor to inject the repositories
    public LogInController(SessionFactory sessionFactory) {
        if (sessionFactory == null) {
            throw new NullPointerException("In LoginController, sessionFactory is null");
        }
        System.out.println("In LoginController constructor");
        this.customerRepository = new CustomerRepository(sessionFactory);
        this.employeeRepository = new EmployeeRepository(sessionFactory);
    }

    // Method to populate Customer and Employee tables if they are empty
    public void checkAndPopulateUsers() {
        try {
            // Check if the Customer table is empty
            if (customerRepository.checkIfEmpty()) {
                List<Customer> customers = new ArrayList<>();

                // Prepopulate with 5 customers
                Customer.CreditCardInfo cardInfo1 = new Customer.CreditCardInfo("1234-5678-9876-5432", "12/25");
                Customer customer1 = new Customer("John Doe", "1234 Elm St", "john.doe@example.com", cardInfo1);

                Customer.CreditCardInfo cardInfo2 = new Customer.CreditCardInfo("9876-5432-1234-5678", "10/23");
                Customer customer2 = new Customer("Jane Smith", "5678 Oak St", "jane.smith@example.com", cardInfo2);

                Customer.CreditCardInfo cardInfo3 = new Customer.CreditCardInfo("1111-2222-3333-4444", "08/24");
                Customer customer3 = new Customer("Michael Johnson", "91011 Maple Ave", "michael.johnson@example.com", cardInfo3);

                Customer.CreditCardInfo cardInfo4 = new Customer.CreditCardInfo("5555-6666-7777-8888", "02/26");
                Customer customer4 = new Customer("Emily Davis", "1213 Pine St", "emily.davis@example.com", cardInfo4);

                Customer.CreditCardInfo cardInfo5 = new Customer.CreditCardInfo("9999-0000-1111-2222", "06/27");
                Customer customer5 = new Customer("David Wilson", "1415 Birch Rd", "david.wilson@example.com", cardInfo5);


                customers.add(customer1);
                customers.add(customer2);
                customers.add(customer3);
                customers.add(customer4);
                customers.add(customer5);

                // Save customers to the database
                customerRepository.populate(customers);
            }

            // Check if the Employee table is empty
            if (employeeRepository.checkIfEmpty()) {
                List<Employee> employees = new ArrayList<>();

                // Prepopulate with 5 employees, using enum for employeeType
                Employee employee1 = new Employee("Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "managerpass", EmployeeType.COMPANY_MANAGER, 1);
                Employee employee2 = new Employee("Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "regularpass", EmployeeType.RESTAURANT_SERVICE, 1);
                Employee employee3 = new Employee("Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "dietitianpass", EmployeeType.DIETITIAN, 2);
                Employee employee4 = new Employee("Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "customerservicepass", EmployeeType.CUSTOMER_SERVICE, 3);
                Employee employee5 = new Employee("Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "adminpass", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3);


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

