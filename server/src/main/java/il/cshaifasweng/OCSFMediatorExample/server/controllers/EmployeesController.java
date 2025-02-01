package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Request;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.EmployeesRepository;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class EmployeesController {

    private EmployeesRepository employeesRepository;

    // constructor to inject the repository
    public EmployeesController(SessionFactory sessionFactory) {
        if(sessionFactory == null)
        {
            throw new NullPointerException(" in EmployeeController(?) sessionFactory is null");
        }
        System.out.println("in EmployeeController constructor");
        this.employeesRepository = new EmployeesRepository(sessionFactory);
    }
    //constructor
    public EmployeesController() {};

    //checks if employees table is empty in DB and if empty saves the next items to table
    public void checkAndPopulateEmployees() {
        try {
            // Check if the employees is already populated
            if(employeesRepository.checkIfEmpty())
            {
                Employee item1 = new Employee("shirMay", "1234", "high manager");
                Employee item2 = new Employee("gal", "1234", "branch manager");
                Employee item3 = new Employee("ori", "1234", "employee");
                Employee item4 = new Employee("liron", "1234", "employee");
                Employee item5 = new Employee("noam", "1234", "employee");

                List<Employee> initEmployees=new ArrayList<Employee>();
                initEmployees.add(item1);
                initEmployees.add(item2);
                initEmployees.add(item3);
                initEmployees.add(item5);
                employeesRepository.populate(initEmployees);
            }
        } catch (Exception exception) {
            throw exception; // Rethrow to ensure rollback in the constructor
        }
    }

    //checks if the employee is already exist in the employees table FUNCTION...
    // public void checkUser(Employee user)....

}