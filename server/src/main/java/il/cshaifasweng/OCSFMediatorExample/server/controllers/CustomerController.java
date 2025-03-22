package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.Response;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.CustomerRepository;
import org.hibernate.Session;

import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.CORRECTNESS_USER;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

public class CustomerController {
    private static CustomerRepository customerRepository;

    // Constructor to initialize the repository
    public CustomerController() {
        customerRepository = new CustomerRepository();
    }

//    public Response handleRequest(Request request) {
//        return switch (request.getRequestType()) {
//            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
//        };
//    }

    // Method to populate Customers if the table is empty
    public void PopulateCustomers(List<Customer> customers) {
        try {
            System.out.println("Checking and populating Customers...");
            if (customerRepository.getCustomers().isEmpty()) {
                customerRepository.populate(customers);
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    public void PopulateCustomer(List<Customer> customers) {
        try {
            System.out.println("populating Customer...");
            customerRepository.populate(customers);
        } catch (Exception exception) {
            throw exception;
        }
    }

    public boolean checkIfEmpty() {
        return customerRepository.getCustomers().isEmpty();
    }
}
