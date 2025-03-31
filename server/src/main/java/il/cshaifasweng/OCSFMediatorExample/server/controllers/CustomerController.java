package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.CustomerRepository;

import java.util.List;

public class CustomerController {

    private static CustomerRepository customerRepository;

    public CustomerController() {
        customerRepository = new CustomerRepository();
    }

    public boolean checkIfEmpty() {
        return customerRepository.checkIfEmpty();
    }

    public void populateCustomers(List<Customer> customers) {
        customerRepository.populate(customers);
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}

