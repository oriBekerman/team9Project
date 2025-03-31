package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Employees.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Employees.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.LogInController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

import static il.cshaifasweng.OCSFMediatorExample.entities.ComplaintStatus.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.DishType.*;
import static il.cshaifasweng.OCSFMediatorExample.entities.ResInfo.Status.APPROVED;


//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private LogInController logInController;
    private RestTableController restTableController;
    private DeliveryController deliveryController;
    private ResInfoController resInfoController;
    private ComplaintController complaintController;

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
        this.resInfoController=new ResInfoController();
        this.complaintController=new ComplaintController();
    }
    //if  database tables are empty initialize them
    public void checkAndPopulateTables() {
        // If database tables are empty, initialize them
        if (menuItemsController.checkIfEmpty() &&
                branchController.checkIfEmpty() &&
                logInController.checkIfEmpty() &&
                restTableController.checkIfEmpty() &&
                deliveryController.checkIfEmpty() &&
                complaintController.checkIfEmpty()&&
                resInfoController.checkIfEmpty()){

            // ==========================
            // 1. Populate Employees
            // ==========================
            List<Employee> employees = List.of(
                    new Employee(111111111, "Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "1234", EmployeeType.COMPANY_MANAGER, 1),
                    new Employee(222222222, "Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "1234", EmployeeType.RESTAURANT_SERVICE, 1),
                    new Employee(333333333, "Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "1234", EmployeeType.DIETITIAN, 2),
                    new Employee(444444444, "Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "1234", EmployeeType.CUSTOMER_SERVICE, 3),
                    new Employee(555555555, "Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "1234", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3)
            );
            logInController.checkAndPopulateUsers(employees);

            // ==========================
            // 2. Define Menu Items
            // ==========================
            MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce", "Low calorie", null, BASE);
            MenuItem item2 = new MenuItem("Pizza", 45.00, "Mushrooms, onions, tomatoes", "Includes vegan option", null, BASE);
            MenuItem item3 = new MenuItem("Pasta", 70.00, "Mushroom cream sauce", "Available gluten-free", null, BASE);
            MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce", "Choice of meat or plant-based", null, BASE);
            MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame", "Served with sea salt", null, BASE);
            MenuItem item6 = new MenuItem("Fries", 15.00, "potato", "Served with sea salt", null, DishType.SPECIAL);
            MenuItem item7 = new MenuItem("Salmon", 70.00, "Salmon", "Served with lemon", null, DishType.SPECIAL);

            Set<MenuItem> menuItems1 = Set.of(item1, item2, item3, item4, item5, item6);
            Set<MenuItem> menuItems2 = Set.of(item1, item2, item3, item4, item5, item7);
            Set<MenuItem> deliverable1 = Set.of(item1, item2, item6);
            Set<MenuItem> deliverable2 = Set.of(item1, item4, item5, item7);

            // ==========================
            // 3. Define Branches
            // ==========================
            Branch telAvivBranch = new Branch("Tel Aviv", "Tel Aviv", "9:00", "22:00");
            Branch haifaBranch = new Branch("Haifa", "Haifa port", "9:00", "19:00");
            List<Branch> branches = List.of(haifaBranch, telAvivBranch);

            // ==========================
            // 4. Define Restaurant Tables & Availability
            // ==========================
            List<RestTable> restTables = List.of(
                    new RestTable("inside", 2),
                    new RestTable("inside", 4),
                    new RestTable("inside", 3),
                    new RestTable("outside", 3),
                    new RestTable("outside", 2),
                    new RestTable("inside", 4)
            );

            // Set unavailable times
            LocalTime time1 = LocalTime.of(9, 0);
            LocalTime time2 = LocalTime.of(10, 30);
            LocalTime time3 = LocalTime.of(11, 0);
            LocalTime time4 = LocalTime.of(12, 30);
            LocalTime time5 = LocalTime.of(14, 30);

            restTables.get(0).setUnavailableFromTimes(Set.of(time1, time2, time3, time5));
            restTables.get(1).setUnavailableFromTimes(Set.of(time1, time4, time5));
            restTables.get(2).addUnavailableFromTime(time3);
            restTables.get(3).addUnavailableFromTime(time4);
            restTables.get(4).addUnavailableFromTime(time5);

            // Assign tables to Haifa branch
            for (RestTable table : restTables) {
                table.setBranch(haifaBranch);
            }
            haifaBranch.setRestTables(new HashSet<>(restTables));

            // ==========================
            // 5. Assign Menu Items & Deliverables to Branches
            // ==========================
            haifaBranch.setBranchMenuItems(menuItems1);
            haifaBranch.setDeliverableItems(deliverable1);
            telAvivBranch.setBranchMenuItems(menuItems2);
            telAvivBranch.setDeliverableItems(deliverable2);

            for (MenuItem menuItem : deliverable1) {
                menuItem.setDeliverableBranches(List.of(haifaBranch));
            }
            for (MenuItem menuItem : deliverable2) {
                menuItem.setDeliverableBranches(List.of(telAvivBranch));
            }

            // Populate controllers
            menuItemsController.PopulateMenuItems(new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6, item7)));
            branchController.populateBranches(branches);

            // ==========================
            // 6. Define Customers & Delivery Orders
            // ==========================
            Customer customer1 = new Customer("Michael Johnson", "7890 Maple Ave, Tel Aviv", "michael.johnson@example.com", "1234-5678-9876-5432", "12/25", "123");
            Customer customer2 = new Customer("Sarah Williams", "1234 Birch St, Haifa", "sarah.williams@example.com", "9876-5432-1234-5678", "11/24", "456");

            List<OrderItem> orderItems1 = List.of(
                    new OrderItem(item1, 2, "No dressing", null),
                    new OrderItem(item4, 1, "Extra ketchup", null)
            );

            List<OrderItem> orderItems2 = List.of(
                    new OrderItem(item6, 3, "No salt", null),
                    new OrderItem(item7, 1, "Well done", null)
            );


            List<OrderItem> orderItems3 = List.of(
                    new OrderItem(item3, 3, "Vegan", null),
                    new OrderItem(item5, 1, "No salt", null)
            );



            // Create deliveries
            Delivery order1 = new Delivery("2025-03-05", new ArrayList<>(orderItems1), customer1, DeliveryMethod.DELIVERY, telAvivBranch);
            Delivery order2 = new Delivery("2025-05-05", new ArrayList<>(orderItems2), customer2, DeliveryMethod.SELF_PICKUP, haifaBranch);
            Delivery order3 = new Delivery("2025-05-10", new ArrayList<>(orderItems3), customer1, DeliveryMethod.SELF_PICKUP, haifaBranch);


            orderItems1.forEach(item -> item.setDelivery(order1));
            orderItems2.forEach(item -> item.setDelivery(order2));
            orderItems3.forEach(item -> item.setDelivery(order3));


            // Populate delivery orders
            deliveryController.populateDelivery(order1);
            deliveryController.populateDelivery(order2);
            deliveryController.populateDelivery(order3);

            System.out.println("Adding new reservations:");

            ResInfo reservation1 = new ResInfo(LocalDate.of(2025, 3, 15), LocalTime.of(19, 30), 4, "inside");
            reservation1.setBranch(haifaBranch);
            reservation1.setCustomer(customer1);
            reservation1.setStatus(APPROVED);

            ResInfo reservation2 = new ResInfo(LocalDate.of(2025, 5, 15), LocalTime.of(20, 30), 2, "inside");
            reservation2.setBranch(telAvivBranch);
            reservation2.setCustomer(customer2);
            reservation2.setStatus(APPROVED);

            ResInfo reservation3 = new ResInfo(LocalDate.of(2025, 5, 25), LocalTime.of(20, 30), 5, "outside");
            reservation3.setBranch(telAvivBranch);
            reservation3.setCustomer(customer1);
            reservation3.setStatus(APPROVED);

            resInfoController.PopulateResSInfo(List.of(reservation1, reservation2,reservation3));

            // Create a Complaint instance without a Branch
            Complaint complaint1 = new Complaint( "Delayed order delivery",NEW);
            complaint1.setBranch(telAvivBranch);
            complaint1.setCustomer(customer1);
            complaint1.setComplaintDate(LocalDateTime.of(2025, 11, 5, 9, 45));

            Complaint complaint2 = new Complaint( "Delayed order delivery",NEW);
            complaint2.setBranch(haifaBranch);
            complaint2.setCustomer(customer1);

            Complaint complaint3 = new Complaint( "Delayed order delivery",NEW);
            complaint3.setBranch(telAvivBranch);
            complaint3.setCustomer(customer2);
            complaint3.setComplaintDate(LocalDateTime.of(2025, 2, 5, 9, 45));


            Complaint complaint4 = new Complaint( "Delayed order delivery",NEW);
            complaint4.setBranch(haifaBranch);
            complaint4.setCustomer(customer2);

            complaintController.populateComplaints(List.of(complaint1,complaint2,complaint3,complaint4));
            //    alice.manager
        }
    }
    //get controllers
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
    ResInfoController getResInfoController() {
        if (resInfoController == null) {
            resInfoController = new ResInfoController();
        }
        return resInfoController;
    }
    ComplaintController getComplaintController() {
        if(complaintController==null){
            complaintController=new ComplaintController();
        }
        return complaintController;
    }

    // shuts down Hibernate.
    public static void shutdown() {
        HibernateUtil.shutdown();
    }
}

