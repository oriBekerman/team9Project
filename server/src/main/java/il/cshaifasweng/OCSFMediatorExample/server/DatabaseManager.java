package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.LogInController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.dataBasePassword;


//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private LogInController logInController;
    private RestTableController restTableController;
    private DeliveryController deliveryController;

    public DatabaseManager(String password) {
        initialize(password);
        initControllers();
        checkAndPopulateTables();
        System.out.println("Database initialized & populated successfully!");
    }
private static void initialize(String password) {
    System.out.println("initializing database...");
    if (sessionFactory == null) {
        System.err.println("failed to initialize Hibernate! SessionFactory is null.");
        throw new HibernateException("SessionFactory creation failed.");
    }
    System.out.println("SessionFactory initialized successfully!");
}



    public void initControllers()
    {
        this.menuItemsController = new MenuItemsController();
        this.branchController = new BranchController();
        this.logInController = new LogInController();
        this.restTableController = new RestTableController();
        this.deliveryController = new DeliveryController();
    }

    //if  database tables are empty initialize them
    public void checkAndPopulateTables() {
        // If there menuItem and branches are empty initialize them
        if (menuItemsController.checkIfEmpty() && branchController.checkIfEmpty() && logInController.checkIfEmpty() && restTableController.checkIfEmpty() && deliveryController.checkIfEmpty()) {

            // Prepopulate with 5 employees, using enum for employeeType
            Employee employee1 = new Employee(111111111, "Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "1234", EmployeeType.COMPANY_MANAGER, 1);
            Employee employee2 = new Employee(222222222, "Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "1234", EmployeeType.RESTAURANT_SERVICE, 1);
            Employee employee3 = new Employee(333333333, "Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "1234", EmployeeType.DIETITIAN, 2);
            Employee employee4 = new Employee(444444444, "Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "1234", EmployeeType.CUSTOMER_SERVICE, 3);
            Employee employee5 = new Employee(555555555, "Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "1234", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3);
            List<Employee> employees = List.of(employee1, employee2, employee3, employee4, employee5);
            logInController.checkAndPopulateUsers(employees);

            // Menu items and branches
            MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce", "Low calorie", null, BASE);
            MenuItem item2 = new MenuItem("Pizza", 45.00, "Mushrooms, onions, tomatoes", "Includes vegan option", null, BASE);
            MenuItem item3 = new MenuItem("Pasta", 70.00, "Mushroom cream sauce", "Available gluten-free", null, BASE);
            MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce", "Choice of meat or plant-based", null, BASE);
            MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame", "Served with sea salt", null, BASE);
            MenuItem item6 = new MenuItem("Fries", 15.00, "potato", "Served with sea salt", null, DishType.SPECIAL);
            MenuItem item7 = new MenuItem("Salmon", 70.00, "Salmon", "Served with lemon", null, DishType.SPECIAL);

            List<MenuItem> menuItems1 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6));
            List<MenuItem> menuItems2 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item7));
            List<MenuItem> deliverable1 = new ArrayList<>(List.of(item1, item2, item6));
            List<MenuItem> deliverable2 = new ArrayList<>(List.of(item1, item4, item5, item7));

            // Branches
            Branch telAvivBranch = new Branch("Tel Aviv", "Tel Aviv", "9:00", "22:00");
            Branch haifaBranch = new Branch("Haifa", "Haifa port", "9:00", "19:00");
            List<Branch> branches2 = List.of(telAvivBranch);
            List<Branch> branches1 = List.of(haifaBranch);
            List<Branch> branches3 = List.of(haifaBranch, telAvivBranch);

            // Rest tables and availability times
            RestTable restTable1 = new RestTable("inside", 2);
            RestTable restTable2 = new RestTable("inside", 4);
            RestTable restTable3 = new RestTable("inside", 3);
            RestTable restTable4 = new RestTable("outside", 3);
            RestTable restTable5 = new RestTable("outside", 2);
            RestTable restTable6 = new RestTable("inside", 4);
            Coordinates coordinates = new Coordinates(100, 100);
            restTable1.setCoordinates(coordinates);

            // Setting unavailable times
            LocalTime time1 = LocalTime.of(9, 0);
            LocalTime time2 = LocalTime.of(10, 30);
            LocalTime time3 = LocalTime.of(11, 0);
            LocalTime time4 = LocalTime.of(12, 30);
            LocalTime time5 = LocalTime.of(14, 30);
            List<LocalTime> unavailableTimes1 = List.of(time1, time2, time3, time5);
            List<LocalTime> unavailableTimes2 = List.of(time1, time4, time5);
            restTable1.setUnavailableFromTimes(unavailableTimes1);
            restTable2.setUnavailableFromTimes(unavailableTimes2);
            restTable3.addUnavailableFromTime(time3);
            restTable4.addUnavailableFromTime(time4);
            restTable5.addUnavailableFromTime(time5);

            List<RestTable> restTables = List.of(restTable1, restTable2, restTable3, restTable4, restTable5, restTable6);

            // Assigning branches to menu items
            for (MenuItem menuItem : deliverable1) {
                menuItem.setDeliverableBranches(branches1);
            }
            for (MenuItem menuItem : deliverable2) {
                menuItem.setDeliverableBranches(branches2);
            }

            // Assigning branches to tables
            for (RestTable table : restTables) {
                table.setBranch(haifaBranch);
            }

            // Setting up the menu and deliverable items for the branches
            haifaBranch.setBranchMenuItems(menuItems1);
           haifaBranch.setDeliverableItems(deliverable1);
           haifaBranch.setRestTables(restTables);
            telAvivBranch.setBranchMenuItems(menuItems2);
            telAvivBranch.setDeliverableItems(deliverable2);

            // Populating menu items and branches controllers
            List<MenuItem> menuItems3 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6, item7));
            menuItemsController.PopulateMenuItems(menuItems3);
            branchController.populateBranches(branches3);


            // Populating some delivery orders
            // Create Customer instances with associated credit card information
            Customer customer1 = new Customer(1, "Michael Johnson", "7890 Maple Ave, Tel Aviv", "michael.johnson@example.com", "1234-5678-9876-5432", "12/25", "123");
            Customer customer2 = new Customer(2, "Sarah Williams", "1234 Birch St, Haifa", "sarah.williams@example.com", "9876-5432-1234-5678", "11/24", "456");

            // Create OrderItems from MenuItem and quantity
            OrderItem orderItem1 = new OrderItem(item1, 2, "No dressing", null); // 2 of "Salad" with preferences
            OrderItem orderItem2 = new OrderItem(item4, 1, "Extra ketchup", null); // 1 of "Hamburger" with preferences
            OrderItem orderItem3 = new OrderItem(item6, 3, "No salt", null); // 3 of "Fries" with preferences
            OrderItem orderItem4 = new OrderItem(item7, 1, "Well done", null); // 1 of "Salmon" with preferences

            // First, create the deliveries and set their customer, date, method, etc.
            Delivery order1 = new Delivery(
                    new ArrayList<>(), // Initialize empty OrderItems list for order1
                    customer1, // Customer
                    DeliveryMethod.DELIVERY, // Delivery method
                    telAvivBranch // Branch
            );
            order1.setDate(LocalDateTime.now());

            Delivery order2 = new Delivery(
                    new ArrayList<>(), // Initialize empty OrderItems list for order2
                    customer2, // Customer
                    DeliveryMethod.SELF_PICKUP, // Delivery method
                    haifaBranch // Branch
            );
            order2.setDate(LocalDateTime.now());

            // Now associate the OrderItems with the Delivery orders
            orderItem1.setDelivery(order1); // Associate orderItem1 with order1
            orderItem2.setDelivery(order1); // Associate orderItem2 with order1
            orderItem3.setDelivery(order2); // Associate orderItem3 with order2
            orderItem4.setDelivery(order2); // Associate orderItem4 with order2

            // Create lists of OrderItems for the delivery orders
            List<OrderItem> orderItems1 = List.of(orderItem1, orderItem2);
            List<OrderItem> orderItems2 = List.of(orderItem3, orderItem4);

            // Set the OrderItems in the respective Delivery objects
            order1.setOrderItems(orderItems1);
            order2.setOrderItems(orderItems2);

            // Adding delivery orders to the delivery controller
            deliveryController.populateDelivery(order1);
            deliveryController.populateDelivery(order2);
        }

    }

    MenuItemsController getMenuItemsController() {
        if(menuItemsController==null)
        {
            menuItemsController=new MenuItemsController();
        }
        return menuItemsController;
    }

    BranchController getBranchController() {
        if(branchController==null)
        {
            branchController=new BranchController();
        }
        return branchController;
    }

    LogInController getLogInController() {
        if (logInController == null) {
            logInController = new LogInController();
        }
        return logInController;
    }
    DeliveryController getDeliveryController() {
        if (deliveryController == null) {
            deliveryController = new DeliveryController();
        }
        return deliveryController;
    }
    RestTableController getRestTableController() {
        if (restTableController == null) {
            restTableController = new RestTableController();
        }
        return restTableController;
    }
    // shuts down Hibernate.
    public static void shutdown() {
        HibernateUtil.shutdown();
    }
}

