package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.entities.Response;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.EmployeeRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Recipient.THIS_CLIENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.BRANCHES_SENT;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.ResponseType.CORRECTNESS_USER;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.ERROR;
import static il.cshaifasweng.OCSFMediatorExample.entities.Response.Status.SUCCESS;

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
    public Response handleRequest(Request request)
    {
        return switch (request.getRequestType())
        {
            case CHECK_USER->verifyUser(request);
            default -> throw new IllegalArgumentException("Invalid request type: " + request.getRequestType());
        };
    }

    public Response verifyUser(Request request) {
        Response response=new Response<>(CORRECTNESS_USER,null,ERROR,THIS_CLIENT);
        String data = (String) request.getData();
        String[] credentials = data.split(" ");
        String username = credentials[0];
        String password = credentials[1];
        Employee employee = this.employeeRepository.findByUsername(username);
        String loginResult;

        if (employee == null) {
            loginResult= "User not found";
        }

        if (!employee.getPassword().equals(password)) {
            loginResult= "Wrong password";
        }
        loginResult= "Login successful";
        if (loginResult.equals("Login successful"))
        {
            EmployeeType employeeType = getEmployeeTypeByUsername(username);
            response.setStatus(SUCCESS);
            response.setMessage(username + ":" + employeeType);
        } else {
            response.setStatus(ERROR);
            response.setData(loginResult);

        }
        return response;
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

