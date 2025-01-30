package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.*;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.dataBasePassword;


//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static SessionFactory sessionFactory;
    private MenuItemsController menuItemsController=null;
    private MenusController menusController=null;
    private BranchController branchController=null;
//    private boolean initializedFlag=false;

    public DatabaseManager(String password) {
        initialize(password);//change password here!!!!
        if(sessionFactory!=null)
        {
            initControllers(sessionFactory);
        }
        if(sessionFactory==null)
        {
            System.out.println("SessionFactory is null");
        }
        System.out.println("in initialize database");
        checkAndPopulateTables();
        System.out.println("in  databaseManager populate");
    };
    public static void initialize(String password)throws HibernateException
    {
        System.out.println("in initialize database");
        try {
             sessionFactory = getSessionFactory(); // Create session factory
            session = sessionFactory.openSession(); // Open session
            session.beginTransaction(); // Start transaction

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback(); // Rollback on failure
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            if (session != null) {
                session.close(); // Always close the session
            }
        }
    }

    public void initControllers(SessionFactory sessionFactory)
    {
        this.menuItemsController = new MenuItemsController(sessionFactory);
        this.menusController = new MenusController(sessionFactory);
        this.branchController = new BranchController(sessionFactory);
    }

    public void checkAndPopulateTables()
    {
        //if there are no menus,menuItems and branches initialize all three
        if(menuItemsController.checkIfEmpty() && menusController.checkIfEmpty())
        {
            menuItemsController.PopulateMenuItems();
            Branch defaultBranch = new Branch("Default Branch", "Default Location", "9:00","19:00");
           List<MenuItem> items= menuItemsController.getItems();
           Menu base=new Menu();
            base.setBranch(defaultBranch);  // Associate with the branch
            defaultBranch.setMenu(base);   // Associate the menu with the branch
            base.SetMenuItems(items);//set items in menu
            List<Branch> branches = List.of(defaultBranch);
            branchController.populateBranches(branches);//save branch
        }
    }

    private SessionFactory getSessionFactory(String password) throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.password",password);

        // Add annotated classes
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(MenuItem.class);

        /// ///////////////////// shir may added this line
//        configuration.addAnnotatedClass(Employee.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
    static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.password",dataBasePassword);

        // Add all entity classes here
        configuration.addAnnotatedClass(Branch.class);
        configuration.addAnnotatedClass(Menu.class);
        configuration.addAnnotatedClass(MenuItem.class);

        /// ///////////////////// shir may added this line
//        configuration.addAnnotatedClass(Employee.class);


        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }
    MenuItemsController getMenuItemsController() {
        if(menuItemsController==null)
        {
            menuItemsController=new MenuItemsController(getSessionFactory());
        }
        return menuItemsController;
    }
    MenusController getMenusController() {
        if(menusController==null)
        {
            menusController=new MenusController(getSessionFactory());
        }
        return menusController;
    }
    BranchController getBranchController() {
        if(branchController==null)
        {
            branchController=new BranchController(getSessionFactory());
        }
        return branchController;
    }

}
