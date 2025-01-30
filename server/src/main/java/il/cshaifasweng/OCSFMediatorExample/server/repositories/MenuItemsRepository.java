package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;

public class MenuItemsRepository extends BaseRepository<MenuItem>
{
//    private final SessionFactory sessionFactory;
    public MenuItemsRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    @Override
    public int getId(MenuItem entity) {
        return ((MenuItem)entity).getItemID();
    }

    @Override
    protected Class<MenuItem> getEntityClass() {
        return MenuItem.class;
    }

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


    public boolean removeDish(int id)
    {
        System.out.println("In MenuRepository removeDish");

        MenuItem item = findById(id);
        if (item == null)
        {
            System.out.println("Menu item not found with ID: " + id);
            return false;
        }

        Transaction transaction = null;
        try (Session session = openSession())
        {
            transaction = session.beginTransaction();
            session.remove(item);
            transaction.commit();
            System.out.println("Menu item removed successfully.");
            return true;
        } catch (Exception e)
        {
            if (transaction != null)
            {
                transaction.rollback();
            }
            System.err.println("Error removing menu item: " + e.getMessage());
        }
        return false;
    }


    public MenuItem addDish(String name, double price)
    {
        System.out.println("In MenuRepository addDish");
        MenuItem newItem = new MenuItem(name, price, "Tomatoes, cucumbers, lettuce",
                "Low calorie", null);
        Transaction transaction = null;

        try (Session session = openSession())
        {
            transaction = session.beginTransaction();
            session.persist(newItem);
            transaction.commit();
            System.out.println("Menu item added successfully.");
            return newItem;
        }
        catch (Exception e)
        {
            if (transaction != null)
            {
                transaction.rollback();
            }
            System.err.println("Error adding menu item: " + e.getMessage());
        }
        return null;
    }



    public MenuItem updateIngredients(int id, String newIngredients)
    {
        System.out.println("In MenuRepository updateIngredients");

        MenuItem item = findById(id);
        if (item == null) {
            System.out.println("Menu item not found with ID: " + id);
            return null;
        }

        Transaction transaction = null;
        try (Session session = openSession())
        {
            transaction = session.beginTransaction();

            // Update ingredients
            item.setIngredients(newIngredients);
            session.merge(item);

            transaction.commit();
            System.out.println("Ingredients updated successfully.");
            return item;
        }
        catch (Exception e)
        {
            if (transaction != null)
            {
                transaction.rollback();
            }
            System.err.println("Error updating ingredients: " + e.getMessage());
        }
        return null;
    }


}
