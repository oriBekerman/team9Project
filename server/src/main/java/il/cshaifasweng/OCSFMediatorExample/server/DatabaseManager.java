package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.EmployeeType;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.LogInController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                complaintController.checkIfEmpty())
        {

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
            MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce", "Low calorie", null, BASE,"");
            MenuItem item2 = new MenuItem("Pizza", 45.00, "Mushrooms, onions, tomatoes", "Includes vegan option", null, BASE,"");
            MenuItem item3 = new MenuItem("Pasta", 70.00, "Mushroom cream sauce", "Available gluten-free", null, BASE,"");
            MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce", "Choice of meat or plant-based", null, BASE,"");
            MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame", "Served with sea salt", null, BASE,"");
            MenuItem item6 = new MenuItem("Fries", 15.00, "potato", "Served with sea salt", null, DishType.SPECIAL,"");
            MenuItem item7 = new MenuItem("Salmon", 70.00, "Salmon", "Served with lemon", null, DishType.SPECIAL,"");

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
            // ==========================
            // 4. Define Restaurant Tables & Availability
            // ==========================
            RestTable table1= new RestTable("inside", 2);
            RestTable table2= new RestTable("inside", 4);
            RestTable table3= new RestTable("inside", 3);
            RestTable table4= new RestTable("inside", 4);
            RestTable table5= new RestTable("inside", 3);
            RestTable table6= new RestTable("inside", 2);
            RestTable table7= new RestTable("outside", 2);
            RestTable table8= new RestTable("outside", 3);
            RestTable table9= new RestTable("outside", 4);
            RestTable table10= new RestTable("outside", 2);
//           List<RestTable> restTablesHaifa = List.of(table1, table2, table3, table4,table5,table6,table7,table8,table9,table10);
            List<RestTable> restTablesHaifa = List.of(table1, table2, table3,table4,table5,table6,table7,table8,table9,table10);


            RestTable table11= new RestTable("inside", 2);
            RestTable table12= new RestTable("inside", 4);
            RestTable table13= new RestTable("inside", 3);
            RestTable table14= new RestTable("inside", 4);
            RestTable table15= new RestTable("inside", 3);
            RestTable table16= new RestTable("inside", 2);
            RestTable table17= new RestTable("outside", 2);
            RestTable table18= new RestTable("outside", 3);
            RestTable table19= new RestTable("outside", 4);
            RestTable table20= new RestTable("outside", 2);
            List<RestTable> restTablesTelAviv = List.of(table11, table12, table13, table14,table15,table16,table17,table18,table19,table20);
            // Assign tables to Haifa branch
            for (RestTable table : restTablesHaifa) {
                table.setBranch(haifaBranch);
            }
            //haifaBranch.setRestTables(new HashSet<>(restTablesHaifa));
            haifaBranch.setRestTables(restTablesHaifa);

            // Assign tables to Tel Aviv branch
            for (RestTable table : restTablesTelAviv) {
                table.setBranch(telAvivBranch);
            }
            telAvivBranch.setRestTables(restTablesTelAviv);
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
            // Populating some delivery orders
            // Create Customer instances with associated credit card information
            Customer customer1 = new Customer(
                    "Michael Johnson",                   // Name
                    "7890 Maple Ave, Tel Aviv",          // Address
                    "michael.johnson@example.com",       // Email
                    "0525616469",                        // Phone number
                    "5555555555554444",                  // Credit Card Number
                    "12/25",                             // Expiration Date
                    "123"                                // CVV
            );

            Customer customer2 = new Customer(
                    "Sarah Williams",                    // Name
                    "1234 Birch St, Haifa",              // Address
                    "sarah.williams@example.com",        // Email
                    "0525616468",                        // Phone number
                    "4111111111111111",                  // Credit Card Number
                    "11/26",                             // Expiration Date
                    "456"                                // CVV
            );
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
                    telAvivBranch, // Branch
                    "10:30"
            );

            Delivery order2 = new Delivery(
                    new ArrayList<>(), // Initialize empty OrderItems list for order2
                    customer2, // Customer
                    DeliveryMethod.SELF_PICKUP, // Delivery method
                    haifaBranch, // Branch
                    "17:00"
            );

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

            //create reservations and setting them in branches
            LocalTime time1 = LocalTime.of(19, 30);
            LocalTime time2 = LocalTime.of(10, 30);
            table2.addUnavailableFromTime(time1);
            ResInfo reservation1 = new ResInfo(haifaBranch, customer1, time1, 4, "Inside", Set.of(table2));
            reservation1.setStatus(APPROVED);
            List tableIds2=List.of(table2.getId());
            haifaBranch.addReservation(reservation1,  Set.of(table2), tableIds2);
            table1.addUnavailableFromTime(time2);
            ResInfo reservation2 = new ResInfo(haifaBranch, customer2, time2, 2, "Inside", Set.of(table1));
            reservation2.setStatus(APPROVED);
            List tableIds1=List.of(table1.getId());
            haifaBranch.addReservation(reservation2, Set.of(table1), tableIds1);

            resInfoController.PopulateResSInfo(List.of(reservation1, reservation2));
            // Create a Complaint instance without a Branch
            Complaint complaint = new Complaint( "Delayed order delivery",NEW);
            complaint.setBranch(telAvivBranch);
            complaint.setCustomer(customer1);
            complaintController.populateComplaints(List.of(complaint));

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

