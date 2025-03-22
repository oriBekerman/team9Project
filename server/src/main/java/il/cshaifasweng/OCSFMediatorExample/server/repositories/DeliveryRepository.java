package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.DatabaseManager;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.CustomerController;
import il.cshaifasweng.OCSFMediatorExample.server.controllers.OrderItemController;
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
// Populate and save a new delivery along with its order items and customer
    public boolean populateDelivery(Delivery delivery) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Save the customer first if it's not null
            if (delivery.getCustomer() != null) {
                List<Customer> customer = Collections.singletonList(delivery.getCustomer());
                CustomerController customerController = SimpleServer.getCustomerController();
                customerController.PopulateCustomer(customer);
            }

            // Save the order items before associating them with the delivery
            if (delivery.getOrderItems() != null && !delivery.getOrderItems().isEmpty()) {
                OrderItemController orderItemController = SimpleServer.getOrderItemController();
                orderItemController.populateOrderItem(delivery.getOrderItems());
            }

            // Link each order item to the delivery and persist them explicitly
            for (OrderItem item : delivery.getOrderItems()) {
                item.setDelivery(delivery);  // Ensure each order item is linked to the delivery
                System.out.println("Saving OrderItem: " + item);
                session.save(item);  // Save each order item explicitly
            }

            // Save the delivery, which will also cascade save the orderItems due to the @OneToMany(cascade = CascadeType.ALL) in Delivery
            System.out.println("Saving Delivery: " + delivery);
            session.save(delivery);

            // Explicitly flush the session to persist the changes
            session.flush();  // This ensures that the SQL statements are executed immediately

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
