package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
            System.out.println("[DeliveryRepository] Delivery saved successfully: " + delivery);
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
            System.out.println("[DeliveryRepository]Getting all deliveries: " + data);
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



//    public List<Delivery> getDeliveriesForReport(int branchId) {
//        System.out.println("[DeliveryRepository] fetching deliveries for branchId: " + branchId);
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Delivery> query = session.createQuery("FROM Delivery WHERE branch.id = :branchId", Delivery.class);
//            query.setParameter("branchId", branchId);
//            List<Delivery> result = query.getResultList();
//            System.out.println("[DeliveryRepository] Deliveries fetched: " + result.size());
//            return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }


//    public List<Delivery> getDeliveriesForReport(int branchId) {
//        System.out.println("DeliveryRepository - getDeliveriesForReport do you even get here?? ");
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Delivery> query = session.createQuery("FROM Delivery WHERE branch.id = :branchId", Delivery.class);
//            query.setParameter("branchId", branchId);
//            return query.getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

//  the version that worked when the "MARCH" was on the right side
//    public List<Delivery> getDeliveriesForReport(int branchId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            String hql = "FROM Delivery d LEFT JOIN FETCH d.customer LEFT JOIN FETCH d.branch LEFT JOIN FETCH d.orderItems WHERE d.branch.id = :branchId";
//            List<Delivery> deliveries = session.createQuery(hql, Delivery.class)
//                    .setParameter("branchId", branchId)
//                    .getResultList();
//
//            // Explicitly initialize lazy relationships
//            deliveries.forEach(delivery -> {
//                Hibernate.initialize(delivery.getCustomer());
//                Hibernate.initialize(delivery.getBranch());
//                Hibernate.initialize(delivery.getOrderItems());
//            });
//
//            return deliveries;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

    public List<Delivery> getDeliveriesForReport(int branchId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT d FROM Delivery d " +
                    "LEFT JOIN FETCH d.customer " +
                    "LEFT JOIN FETCH d.branch " +
                    "LEFT JOIN FETCH d.orderItems " +
                    "WHERE d.branch.id = :branchId";
            List<Delivery> deliveries = session.createQuery(hql, Delivery.class)
                    .setParameter("branchId", branchId)
                    .getResultList();

            // Initialize lazy relationships if needed
            deliveries.forEach(delivery -> {
                Hibernate.initialize(delivery.getCustomer());
                Hibernate.initialize(delivery.getBranch());
                Hibernate.initialize(delivery.getOrderItems());
            });

            return deliveries;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
