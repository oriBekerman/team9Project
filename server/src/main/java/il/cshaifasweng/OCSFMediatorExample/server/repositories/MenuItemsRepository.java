package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.DishType;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


public class MenuItemsRepository extends BaseRepository<MenuItem>
{

    public MenuItemsRepository() {
        super();
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
    public List<MenuItem> getAllItems()
    {
        List<MenuItem> data=new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            session.beginTransaction();
                //get items from database
                CriteriaBuilder builder = session.getCriteriaBuilder();
                CriteriaQuery<MenuItem> query = builder.createQuery(MenuItem.class);
                query.from(MenuItem.class);
                data = session.createQuery(query).getResultList();
                System.out.println("getting menu items");
                System.out.println(data);
                session.getTransaction().commit(); // Save everything.
            }
            return data;
    }

    public List<MenuItem>getBaseItems()
    {
        List<MenuItem> items;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            session.beginTransaction();
            if(session==null)
            {
                System.out.println("session is null");
            }
            session.clear();
            System.out.println("menu base rep 1");
            Query<MenuItem> query = session.createQuery("from MenuItem WHERE dishType= :type", MenuItem.class);
            System.out.println("menu base rep 2");
            query.setParameter("type", DishType.BASE);
            System.out.println("menu base rep 3");
            items = query.getResultList();
            for(MenuItem item : items)
            {
                System.out.println(item.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return items;
    }
    public MenuItem updatePrice(int id,double price)
    {
        System.out.println("in MenuRepository updatePrice");
        MenuItem item=findById(id);
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
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
        return item;
    }

    public void populate(List<MenuItem> menuItems) {
        for (MenuItem menuItem : menuItems) {
            save(menuItem);
        }
    }
    //change
}
