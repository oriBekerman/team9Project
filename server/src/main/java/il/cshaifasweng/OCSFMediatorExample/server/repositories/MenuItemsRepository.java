package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.DishType;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import il.cshaifasweng.OCSFMediatorExample.entities.UpdateBranchSpecialItemRequest;


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

    public boolean updateBranchMenu(Branch branch) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.update(branch);  // Save updated branch menu
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public List<Branch> getBranchesWithDish(MenuItem dish) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        List<Branch> branches = new ArrayList<>();

        try {
            branches = session.createQuery(
                            "SELECT b FROM Branch b JOIN b.menuItems m WHERE m.id = :dishId", Branch.class)
                    .setParameter("dishId", dish.getItemID())
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }

        return branches;
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
            transaction = session.beginTransaction();

            // Retrieve the dish from the database
            MenuItem item = session.get(MenuItem.class, dishToRemove.getItemID());
            if (item != null) {

                // Remove from branches (menuItems and deliverableItems)
                for (Branch branch : item.getBranches()) {
                    branch.getBranchMenuItems().remove(item);
                }
                for (Branch branch : item.getDeliverableBranches()) {
                    branch.getDeliverableItems().remove(item);
                }

                // Optionally, delete associated OrderItems first (if cascade is not defined)
                Query<?> orderItemDeleteQuery = session.createQuery("DELETE FROM OrderItem WHERE menuItem = :menuItem");
                orderItemDeleteQuery.setParameter("menuItem", item);
                orderItemDeleteQuery.executeUpdate();

                // Remove the dish itself
                session.delete(item);

                transaction.commit();
                System.out.println("Dish removed successfully: " + item.getName());
                return true;
            } else {
                System.out.println("Dish not found: " + dishToRemove.getName());
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }




    public boolean updateDishIngredients(int itemId, String newIngredients)
    {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            MenuItem menuItem = session.get(MenuItem.class, itemId);
            if (menuItem != null) {

                menuItem.setIngredients(newIngredients);

                session.merge(menuItem);

                session.getTransaction().commit();
                System.out.println("Ingredients updated successfully for: " + menuItem.getName());  // Logging
                return true;
            } else {
                System.out.println("MenuItem with ID " + itemId + " not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDishType(int itemId, DishType newDishType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            MenuItem menuItem = session.get(MenuItem.class, itemId);
            if (menuItem != null) {

                menuItem.setDishType(newDishType);

                session.merge(menuItem);

                session.getTransaction().commit();
                System.out.println("Dish type updated successfully for: " + menuItem.getName());
                return true;
            } else {
                System.out.println("MenuItem with ID " + itemId + " not found.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addMenuItem(MenuItem newDish) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            session.save(newDish);

            transaction.commit();

            System.out.println("Dish added successfully: " + newDish.getName());
            return true;
        } catch (Exception e) {

            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}

