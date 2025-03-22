package il.cshaifasweng.OCSFMediatorExample.server.controllers;

import il.cshaifasweng.OCSFMediatorExample.entities.OrderItem;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import il.cshaifasweng.OCSFMediatorExample.server.repositories.OrderItemRepository;
import org.hibernate.Session;

import java.util.List;

public class OrderItemController {
    private static OrderItemRepository orderItemRepository;

    // Constructor to initialize the repository
    public OrderItemController() {
        orderItemRepository = new OrderItemRepository();
    }

    // Method to populate OrderItems if the table is empty
//    public void populateOrderItems(List<OrderItem> orderItems) {
//        try {
//            System.out.println("Checking and populating OrderItems...");
//            if (orderItemRepository.getOrderItems().isEmpty()) {
//                orderItemRepository.populate(orderItems);
//            }
//        } catch (Exception exception) {
//            throw exception;
//        }
//    }

    // Method to populate specific order items (e.g., populate additional information for the order items)
    public void populateOrderItem(List<OrderItem> orderItems) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            System.out.println("Populating OrderItem...");
            for (OrderItem item : orderItems) {
                session.saveOrUpdate(item);  // ✅ Ensure each item is saved in an active transaction
                System.out.println("Saved OrderItem: " + item);
            }

            session.getTransaction().commit();  // ✅ Commit transaction
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }


    // Method to check if OrderItems repository is empty
    public boolean checkIfEmpty() {
        return orderItemRepository.getOrderItems().isEmpty();
    }
}
