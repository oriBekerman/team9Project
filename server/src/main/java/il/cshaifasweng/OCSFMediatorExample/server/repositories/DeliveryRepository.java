package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;

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
// Populate and save a new delivery along with its order items and customer
    public boolean populateDelivery(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Check if the customer is transient (not saved in the database yet)
            if (delivery.getCustomer() != null && delivery.getCustomer().getId() == 0) {
                // Save the customer before saving the delivery
                session.save(delivery.getCustomer());
            }

            // Now save the delivery
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


}
