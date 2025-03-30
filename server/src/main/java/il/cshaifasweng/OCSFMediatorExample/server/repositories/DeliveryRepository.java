package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeliveryRepository extends BaseRepository<Delivery> {

    // Constructor
    public DeliveryRepository() {
        super();
    }

    @Override
    public int getId(Delivery entity) {
        return entity.getDeliveryNumber();
    }

    @Override
    protected Class<Delivery> getEntityClass() {
        return Delivery.class;
    }

    // Populate and save a new delivery along with its order items and customer
    public boolean populateDelivery(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Handle Customer saving or merging
            if (delivery.getCustomer() != null) {
                if (delivery.getCustomer().getId() == null) {
                    session.save(delivery.getCustomer());  // Save if customer is new
                } else {
                    delivery.setCustomer((Customer) session.merge(delivery.getCustomer()));  // Merge if customer exists
                }
            }

            // Explicitly save or merge each OrderItem (optional, depending on how cascading works)
            for (OrderItem orderItem : delivery.getOrderItems()) {
                if (orderItem.getId() == 0) { // Assuming `0` means new OrderItem (since ID is auto-generated)
                    session.save(orderItem);  // Save new OrderItem
                } else {
                    session.merge(orderItem);  // Merge existing OrderItem (optional)
                }
            }

            // Save the delivery itself
            session.save(delivery);  // Save the delivery

            // Commit the transaction
            session.getTransaction().commit();
            System.out.println("Delivery saved successfully: " + delivery);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // Get all deliveries from the database
    public List<Delivery> getAllDeliveries() {
        List<Delivery> data = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Delivery> query = builder.createQuery(Delivery.class);
            Root<Delivery> deliveryRoot = query.from(Delivery.class);

            //  join customer and orderItems to fetch them along with the delivery
            deliveryRoot.fetch("customer", JoinType.LEFT);  // Join with customer
            deliveryRoot.fetch("orderItems", JoinType.LEFT);  // Join with orderItems

            // Execute the query to fetch the results
            data = session.createQuery(query).getResultList();

            session.getTransaction().commit();  // Commit the transaction
            System.out.println("Getting all deliveries: " + data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch deliveries", e);
        }
        return data;
    }


    // Get a delivery by order number
    public Delivery getDeliveryByOrderNumber(int orderNumber) {
        Delivery delivery = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Use CriteriaBuilder to create the query
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Delivery> query = builder.createQuery(Delivery.class);
            Root<Delivery> deliveryRoot = query.from(Delivery.class);

            // Eagerly fetch customer and orderItems
            deliveryRoot.fetch("customer", JoinType.LEFT);  // Join with customer
            deliveryRoot.fetch("orderItems", JoinType.LEFT);  // Join with orderItems

            // Fetch the specific delivery by its order number (using ID)
            query.select(deliveryRoot).where(builder.equal(deliveryRoot.get("orderNumber"), orderNumber));

            // Execute the query and avoid NoResultException
            List<Delivery> result = session.createQuery(query).getResultList();
            delivery = result.stream().findFirst().orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch delivery by order number", e);
        }
        return delivery;
    }

    // Method to cancel a delivery by setting the isCanceled flag to true
    public boolean cancelDeliveryByOrderNumber(int orderNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Get the delivery by order number
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Delivery> query = builder.createQuery(Delivery.class);
            Root<Delivery> deliveryRoot = query.from(Delivery.class);
            query.select(deliveryRoot).where(builder.equal(deliveryRoot.get("orderNumber"), orderNumber));

            List<Delivery> result = session.createQuery(query).getResultList();

            if (!result.isEmpty()) {
                // Delivery found, cancel it by setting the isCanceled flag to true
                Delivery delivery = result.get(0);
                delivery.setCanceled(true); // Set the delivery status as canceled

                // Save the updated delivery
                session.update(delivery);
                session.getTransaction().commit();  // Commit the transaction
                System.out.println("Delivery with order number " + orderNumber + " has been canceled.");
                return true;  // Successfully canceled
            } else {
                // No delivery found with the given order number
                System.out.println("No delivery found with order number: " + orderNumber);
                return false;  // Failed to cancel
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // Failed to cancel due to an error
        }
    }

    public Customer getCustomerByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Delivery> cq = cb.createQuery(Delivery.class);
            Root<Delivery> root = cq.from(Delivery.class);

            // Access nested customer.email
            Predicate emailMatch = cb.equal(root.get("customer").get("email"), email);

            cq.select(root).where(emailMatch);

            Delivery delivery = session.createQuery(cq)
                    .setMaxResults(1)
                    .uniqueResult();

            return (delivery != null) ? delivery.getCustomer() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setCustomer(Delivery newDelivery) {
        Transaction tx=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx = session.beginTransaction();
            session.saveOrUpdate(newDelivery);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }

}
