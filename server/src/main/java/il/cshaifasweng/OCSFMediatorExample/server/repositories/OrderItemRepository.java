package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepository extends BaseRepository<OrderItem> {

    public OrderItemRepository() {
        super();
    }

    @Override
    public int getId(OrderItem entity) {
        return entity.getId();
    }

    @Override
    protected Class<OrderItem> getEntityClass() {
        return OrderItem.class;
    }

    // Find order item by ID
    public OrderItem findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(OrderItem.class, id);
        }
    }

    // Get all order items from the database
    public List<OrderItem> getOrderItems() {
        List<OrderItem> orderItems = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Retrieve order items using Criteria API
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<OrderItem> query = builder.createQuery(OrderItem.class);
            query.from(OrderItem.class);
            orderItems = session.createQuery(query).getResultList();
            System.out.println("Getting order items");
            System.out.println(orderItems);
            tx.commit(); // Save everything.
        } catch (Exception exception) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        }
        return orderItems;
    }

    // Populate the database with order items
    public void populate(List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            save(orderItem);
        }
    }
}
