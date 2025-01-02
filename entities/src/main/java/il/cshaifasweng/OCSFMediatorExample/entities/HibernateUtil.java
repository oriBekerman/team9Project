package il.cshaifasweng.OCSFMediatorExample.entities;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static String password;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                if (password != null) {
                    configuration.setProperty("hibernate.connection.password", password);
                }
                sessionFactory = configuration.buildSessionFactory();
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ExceptionInInitializerError(ex);
            }
        }
        return sessionFactory;
    }

    public static void setPassword(String dbPassword) {
        password = dbPassword;
        sessionFactory = null; // איפוס ה-SessionFactory כדי שייבנה מחדש עם הסיסמה החדשה
    }
}

