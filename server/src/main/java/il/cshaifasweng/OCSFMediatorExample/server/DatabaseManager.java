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

import java.util.ArrayList;
import java.util.List;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.dataBasePassword;


//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private LogInController logInController;

    public DatabaseManager(String password) {
        initialize(password);
        initControllers();
        checkAndPopulateTables();
        System.out.println("Database initialized & populated successfully!");
    }

//    public static void initialize(String password)throws HibernateException
//    {
//        System.out.println("in initialize database");
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            sessionFactory= getSessionFactory(); // Create session factory
//            session = sessionFactory.openSession(); // Open session
//            session.beginTransaction(); // Start transaction
//
//        } catch (Exception exception) {
//            if (session != null) {
//                session.getTransaction().rollback(); // Rollback on failure
//            }
//            System.err.println("An error occurred, changes have been rolled back.");
//            exception.printStackTrace();
//        } finally {
//            if (session != null) {
//                session.close(); // Always close the session
//            }
//        }
//    }
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
    }

    //if  database tables are empty initialize them
    public void checkAndPopulateTables() {
        //if there menuItem and branches are empty initialize them
        if (menuItemsController.checkIfEmpty() && branchController.checkIfEmpty()) {

            // Prepopulate with 5 employees, using enum for employeeType
            Employee employee1 = new Employee(111111111, "Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "1234", EmployeeType.COMPANY_MANAGER, 1);
            Employee employee2 = new Employee(222222222, "Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "1234", EmployeeType.RESTAURANT_SERVICE, 1);
            Employee employee3 = new Employee(333333333, "Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "1234", EmployeeType.DIETITIAN, 2);
            Employee employee4 = new Employee(444444444, "Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "1234", EmployeeType.CUSTOMER_SERVICE, 3);
            Employee employee5 = new Employee(555555555, "Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "1234", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3);
            List<Employee> employees = List.of(employee1, employee2, employee3, employee4, employee5);
            logInController.checkAndPopulateUsers(employees);
            Branch defaultBranch = new Branch("Default Branch", "Default Location", "9:00", "19:00");
            Branch haifaBranch = new Branch("Haifa", "Haifa port", "9:00", "19:00");
            List<Branch> branches = List.of(defaultBranch, haifaBranch);

            MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce",
                    "Low calorie", null, il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE);

            MenuItem item2 = new MenuItem("Pizza ", 55.00, " Mushrooms, onions, tomatoes",
                    " Includes vegan option ", null, il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE);

            MenuItem item3 = new MenuItem("Pasta", 60.00, "Mushroom cream sauce",
                    "Available gluten-free", null, il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE);

            MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce",
                    "Choice of meat or plant-based", null, il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE);

            MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame",
                    "Served with sea salt", null, il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE);
            List<MenuItem> menuItems = List.of(item1, item2, item3, item4, item5);
            //set the base menu items for every branch
            for (Branch branch : branches) {
                branch.setBranchMenuItems(menuItems);
            }
            //sett all branches to each base menu item
            for (MenuItem menuItem : menuItems) {
                menuItem.setBranches(branches);
            }
            menuItemsController.PopulateMenuItems(menuItems);
            branchController.populateBranches(branches);
        }
    }
//    static SessionFactory getSessionFactory() throws HibernateException {
//        Configuration configuration = new Configuration();
//        configuration.setProperty("hibernate.connection.password",dataBasePassword);
//
//        // Add all entity classes here
//        configuration.addAnnotatedClass(Branch.class);
//        configuration.addAnnotatedClass(MenuItem.class);
//        configuration.addAnnotatedClass(Employee.class);
//
//
//        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
//                .applySettings(configuration.getProperties())
//                .build();
//
//        return configuration.buildSessionFactory(serviceRegistry);
//    }
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
    // shuts down Hibernate.
    public static void shutdown() {
        HibernateUtil.shutdown();
    }
}

