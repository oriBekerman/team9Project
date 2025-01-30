package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;

public class MenuItemsRepository extends BaseRepository<MenuItem>
{
//    private final SessionFactory sessionFactory;
    public MenuItemsRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    ///  both of them to copy ( to my new EmployeeRepository )and change just name + type


    @Override
    public int getId(MenuItem entity) {
        return ((MenuItem)entity).getItemID();
    }

    @Override
    protected Class<MenuItem> getEntityClass() {
        return MenuItem.class;
    }

    /// //////

    // get MenuItems form database returns menuItemsList
    public List<MenuItem> getMenuItems()
    {
        List<MenuItem> data=new ArrayList<>();
        try {
                session =openSession();
                session.beginTransaction();
                //get items from database
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<MenuItem> query = builder.createQuery(MenuItem.class);
                query.from(MenuItem.class);
                data = session.createQuery(query).getResultList();
                System.out.println("getting menu items");
                System.out.println(data);
                session.getTransaction().commit(); // Save everything.
            } catch (Exception exception)
            {
                if (session != null)
                {
                    session.getTransaction().rollback();
                }
                System.err.println("An error occured, changes have been rolled back.");
                exception.printStackTrace();
            }
            finally {
                session.close();
            }
            return data;
    }
    public MenuItem updatePrice(int id,double price)
    {
        System.out.println("in MenuRepository updatePrice");
        MenuItem item=findById(id);
        try {
            session = openSession();
            session.beginTransaction();
            // set item price
            item.setPrice(price);
            // Update the item in the database
            session.merge(item);
            // Force Hibernate to flush changes to the database
            session.flush();
            // Commit the transaction
            session.getTransaction().commit();
        }
        catch (Exception e) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return item;
    }

}
