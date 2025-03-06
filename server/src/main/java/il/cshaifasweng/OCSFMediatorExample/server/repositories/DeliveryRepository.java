package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryRepository extends BaseRepository<Delivery> {

    // Constructor
    public DeliveryRepository() {
        super();
    }

    @Override
    public int getId(Delivery entity) {
        return entity.getOrderNumber();
    }

    @Override
    protected Class<Delivery> getEntityClass() {
        return Delivery.class;
    }

    // Populate and save a new delivery along with its order items and customer
// Populate and save a new delivery along with its order items and customer
    public boolean populateDelivery(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Save the customer first if it's not null
            if (delivery.getCustomer() != null) {
                session.saveOrUpdate(delivery.getCustomer());  // Ensure the customer is saved or updated
            }

            // Save the delivery, which will also cascade save the orderItems due to the @OneToMany(cascade = CascadeType.ALL) in Delivery
            session.save(delivery);

            session.getTransaction().commit();  // Commit the transaction
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
            query.from(Delivery.class);
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
            session.beginTransaction();
            delivery = session.get(Delivery.class, orderNumber);  // Get the delivery by order number
            session.getTransaction().commit();
            System.out.println("Found delivery: " + delivery);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch delivery by order number", e);
        }
        return delivery;
    }
}
