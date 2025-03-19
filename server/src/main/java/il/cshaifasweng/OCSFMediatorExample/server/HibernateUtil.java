package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.dataBasePassword;

public class HibernateUtil {
    private static final HibernateUtil instance = new HibernateUtil(); // Singleton Instance
    private static SessionFactory sessionFactory;
    private static final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();

    static {
        try {
            // Load hibernate.cfg.xml
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    //private Constructor (Singleton)
    private HibernateUtil() {
    }
    // Close Hibernate SessionFactory
    public static void shutdown() {
//        closeSession();  // Ensure all sessions are closed
        if (sessionFactory != null) {
            sessionFactory.close();
            System.out.println("Hibernate SessionFactory closed.");
        }
    }

    //Creates and returns a Singleton Hibernate SessionFactory instance
    public static SessionFactory getSessionFactory() throws HibernateException {
        if (sessionFactory == null) // Check if SessionFactory is already created
        {
            synchronized (HibernateUtil.class) {  //ensure thread safety
                {
                    if (sessionFactory == null) {
                        try {
                            Configuration configuration = new Configuration();
                            configuration.setProperty("hibernate.connection.password",dataBasePassword);

                            // Add entity mappings
                            configuration.addAnnotatedClass(Branch.class);
                            configuration.addAnnotatedClass(MenuItem.class);
                            configuration.addAnnotatedClass(Employee.class);
                            configuration.addAnnotatedClass(RestTable.class);
                            configuration.addAnnotatedClass(Delivery.class);
                            configuration.addAnnotatedClass(OrderItem.class);
                            configuration.addAnnotatedClass(Customer.class);
                            configuration.addAnnotatedClass(ResInfo.class);
                            configuration.addAnnotatedClass(Complaint.class);





                            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                                    .applySettings(configuration.getProperties())
                                    .build();

                            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
                        } catch (Exception e) {
                            System.err.println("Error creating SessionFactory!");
                            e.printStackTrace();
                            throw new HibernateException("SessionFactory creation failed", e);
                        }
                    }
                }
            }
        }
        return sessionFactory;
    }
}