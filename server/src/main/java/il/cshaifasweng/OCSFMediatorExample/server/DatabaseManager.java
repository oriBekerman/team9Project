package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.LogInController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.DishType.BASE;


//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
    private MenuItemsController menuItemsController;
    private BranchController branchController;
    private LogInController logInController;
    private ResInfoController resInfoController;

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
        this.resInfoController = new ResInfoController();
    }

    //if  database tables are empty initialize them
    public void checkAndPopulateTables() {
        //if there menuItem and branches are empty initialize them
        if (menuItemsController.checkIfEmpty() && branchController.checkIfEmpty() && resInfoController.checkIfEmpty()) {

            // Prepopulate with 5 employees, using enum for employeeType
            Employee employee1 = new Employee(111111111, "Alice Manager", "1234 Maple St", "alice.manager@example.com", "alice.manager", "1234", EmployeeType.COMPANY_MANAGER, 1);
            Employee employee2 = new Employee(222222222, "Bob Regular", "5678 Oak St", "bob.regular@example.com", "bob.regular", "1234", EmployeeType.RESTAURANT_SERVICE, 1);
            Employee employee3 = new Employee(333333333, "Charlie Dietitian", "9101 Pine St", "charlie.dietitian@example.com", "charlie.dietitian", "1234", EmployeeType.DIETITIAN, 2);
            Employee employee4 = new Employee(444444444, "Debbie Customer Service", "2345 Birch St", "debbie.cs@example.com", "debbie.cs", "1234", EmployeeType.CUSTOMER_SERVICE, 3);
            Employee employee5 = new Employee(555555555, "Eva Admin", "6789 Cedar St", "eva.admin@example.com", "eva.admin", "1234", EmployeeType.CUSTOMER_SERVICE_MANAGER, 3);
            List<Employee> employees = List.of(employee1, employee2, employee3, employee4, employee5);
            logInController.checkAndPopulateUsers(employees);


            MenuItem item1 = new MenuItem("Salad", 35.00, "Tomatoes, cucumbers, lettuce", "Low calorie", null, BASE);
            MenuItem item2 = new MenuItem("Pizza ", 45.00, " Mushrooms, onions, tomatoes", " Includes vegan option ", null, BASE);
            MenuItem item3 = new MenuItem("Pasta", 70.00, "Mushroom cream sauce", "Available gluten-free", null, BASE);
            MenuItem item4 = new MenuItem("Hamburger", 80.00, "Meatball, pickle, tomato, lettuce", "Choice of meat or plant-based", null, BASE);
            MenuItem item5 = new MenuItem("Edamame", 30.00, "Edamame", "Served with sea salt", null,BASE);
            MenuItem item6 = new MenuItem("Fries", 15.00, "potato", "Served with sea salt", null, DishType.SPECIAL);
            MenuItem item7 = new MenuItem("salmon", 70.00, "salmon", "Served with lemon", null, DishType.SPECIAL);

            List<MenuItem> menuItems1 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6));
            List<MenuItem> menuItems2 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item7));
            List<MenuItem> menuItems3 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6));
            List<MenuItem> menuItems4 = new ArrayList<>(List.of(item1, item2, item3, item4, item5, item7));

            List<MenuItem> deliverable1 = new ArrayList<>(List.of(item1, item2,item6));
            List<MenuItem> deliverable2 = new ArrayList<>(List.of(item1,item4, item5, item7));
            List<MenuItem> deliverable3 = new ArrayList<>(List.of(item1, item2,item6));
            List<MenuItem> deliverable4 = new ArrayList<>(List.of(item1,item4, item5, item7));

            Branch haifaBranch = new Branch("Haifa", "Haifa port", "9:00", "19:00");
            Branch telAvivBranch = new Branch("Tel Aviv", "Tel Aviv", "9:00", "19:00");
            Branch jerusalemBranch = new Branch("Jerusalem", "Jerusalem", "9:00", "19:00");
            Branch zikhronBranch = new Branch("Zikhron Ya'akov", "Zikhron Ya'akov", "9:00", "19:00");

            List<Branch> branches1 = List.of(haifaBranch);
            List<Branch> branches2 = List.of(telAvivBranch);
            List<Branch> branches3 = List.of(jerusalemBranch);
            List<Branch> branches4 = List.of(zikhronBranch);
            List<Branch> branches5 = List.of(haifaBranch,telAvivBranch,jerusalemBranch,zikhronBranch);

            RestTable restTable1=new RestTable("inside",2);
            RestTable restTable2=new RestTable("inside",4);
            RestTable restTable3=new RestTable("inside",3);
            RestTable restTable4=new RestTable("outside",3);
            RestTable restTable5=new RestTable("outside",2);
            RestTable restTable6=new RestTable("inside",4);

            ResInfo resInfo1 = new ResInfo("Shir May Rapaport","0526222222","shirMay@gmail.com","000000",
                    "01/01/2029",888,"20/03/2025","09:00-10:30",4,"in");

            ResInfo resInfo2 = new ResInfo("Shir May Rapaport","0526222222","shirMay@gmail.com","000000",
                    "01/01/2029",888,"25/03/2025","15:00-17:30",2,"out");

            ResInfo resInfo3 = new ResInfo("Shir May Rapaport","0526222222","shirMay@gmail.com","000000",
                    "01/01/2029",888,"27/03/2025","17:00-18:30",8,"in");
            List<ResInfo> resSInfo = List.of(resInfo1,resInfo2,resInfo3);


            Coordinates coordinates=new Coordinates(100,100);
            restTable1.setCoordinates(coordinates);

            LocalTime time1=LocalTime.of(9,0);
            LocalTime time2=LocalTime.of(11,30);
            List<LocalTime> unavailableTimes=List.of(time1,time2);

            restTable1.setUnavailableFromTimes(unavailableTimes);
            List<RestTable> restTables = List.of(restTable1,restTable2,restTable3,restTable4,restTable5,restTable6);

           for (MenuItem menuItem : deliverable1) {
               menuItem.setDeliverableBranches(branches1);
           }
           for (MenuItem menuItem : deliverable2) {
               menuItem.setDeliverableBranches(branches2);
           }
           for (MenuItem menuItem : deliverable3) {
               menuItem.setDeliverableBranches(branches3);
           }
           for (MenuItem menuItem : deliverable4) {
               menuItem.setDeliverableBranches(branches4);
           }
           for (RestTable table: restTables) {
               table.setBranch(haifaBranch);
           }
            haifaBranch.setBranchMenuItems(menuItems1);
            haifaBranch.setDeliverableItems(deliverable1);
            haifaBranch.setRestTables(restTables);

            telAvivBranch.setBranchMenuItems(menuItems2);
            telAvivBranch.setDeliverableItems(deliverable2);

            jerusalemBranch.setBranchMenuItems(menuItems3);
            jerusalemBranch.setDeliverableItems(deliverable3);

            zikhronBranch.setBranchMenuItems(menuItems4);
            zikhronBranch.setDeliverableItems(deliverable4);

            List<MenuItem> menuItems5= new ArrayList<>(List.of(item1, item2, item3, item4, item5, item6, item7));
            menuItemsController.PopulateMenuItems(menuItems5);
            branchController.populateBranches(branches5);
            resInfoController.PopulateResSInfo(resSInfo);
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

    ResInfoController getResInfoController() {
        if (resInfoController == null) {
            resInfoController = new ResInfoController();
        }
        return resInfoController;
    }



    // shuts down Hibernate.
    public static void shutdown() {
        HibernateUtil.shutdown();
    }
}

