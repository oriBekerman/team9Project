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

    public List<MenuItem> getBaseItems()
    {
        List<MenuItem> items;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            session.beginTransaction();
            if(session == null)
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

    public MenuItem updateThePrice(int id, double price)
    {
        System.out.println("in MenuRepository updateThePrice");
        MenuItem item = findById(id);
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
    public boolean removeDish(MenuItem dishToRemove) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start the transaction
            transaction = session.beginTransaction();

            // Retrieve the dish from the database using its ID
            MenuItem item = session.get(MenuItem.class, dishToRemove.getItemID());  // Assuming ItemID is the ID of MenuItem
            if (item != null) {
                // If the dish exists, delete it
                session.delete(item);

                // Commit the transaction
                transaction.commit();
                System.out.println("Dish removed successfully: " + item.getName());  // Optional logging
                return true;
            } else {
                // If the dish is not found, return false
                System.out.println("Dish not found: " + dishToRemove.getName());
                return false;
            }
        } catch (Exception e) {
            // Rollback if there was an error and the transaction is not null
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();  // Log the error
            return false;
        }
    }
    public boolean updateDishIngredients(int itemId, String newIngredients) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Retrieve the menu item by its ID
            MenuItem menuItem = session.get(MenuItem.class, itemId); // Retrieve by ID
            if (menuItem != null) {
                // Update ingredients of the menu item
                menuItem.setIngredients(newIngredients);

                // Merge the updated menu item into the session
                session.merge(menuItem); // Use merge to apply updates

                // Commit the transaction
                session.getTransaction().commit();
                System.out.println("Ingredients updated successfully for: " + menuItem.getName());  // Logging
                return true;
            } else {
                System.out.println("MenuItem with ID " + itemId + " not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return false;
        }
    }
    public boolean updateDishType(int itemId, DishType newDishType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Retrieve the menu item by its ID
            MenuItem menuItem = session.get(MenuItem.class, itemId); // Retrieve by ID
            if (menuItem != null) {
                // Update dish type of the menu item
                menuItem.setDishType(newDishType);

                // Merge the updated menu item into the session
                session.merge(menuItem); // Use merge to apply updates

                // Commit the transaction
                session.getTransaction().commit();
                System.out.println("Dish type updated successfully for: " + menuItem.getName());  // Logging
                return true;
            } else {
                System.out.println("MenuItem with ID " + itemId + " not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            return false;
        }
    }
    // Add a new MenuItem (Dish) to the database
    public boolean addMenuItem(MenuItem newDish) {
        Transaction transaction = null;  // Declare a transaction object
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Start the transaction
            transaction = session.beginTransaction();

            // Save the new dish to the database
            session.save(newDish);

            // Commit the transaction
            transaction.commit();

            System.out.println("Dish added successfully: " + newDish.getName());  // Optional logging
            return true;
        } catch (Exception e) {
            // Rollback if there was an error and the transaction is not null
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();  // Log the error
            return false;
        }
    }
}

