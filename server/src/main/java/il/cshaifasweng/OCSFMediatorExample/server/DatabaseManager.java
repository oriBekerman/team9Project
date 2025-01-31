package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.MenuItemsController;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.dataBasePassword;

//configures database,handles opening and closing sessions
public class DatabaseManager {
    private static SessionFactory sessionFactory;
    private MenuItemsController menuItemsController=null;
//    private boolean initializedFlag=false;

    public DatabaseManager(String password) {
        initialize(password);
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
    }
    public void checkAndPopulateTables()
    {
        menuItemsController.checkAndPopulateMenuItems();
    }

    private SessionFactory getSessionFactory(String password) throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.password",password);

        // Add annotated classes
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

}
