package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository extends BaseRepository<Customer> {

    public CustomerRepository() {
        super();
    }

    @Override
    public int getId(Customer entity) {
        return entity.getId();
    }

    @Override
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

    // Find customer by email
    public Customer findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Customer WHERE email = :email", Customer.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }

    // Get all customers from the database
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Retrieve customers using Criteria API
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Customer> query = builder.createQuery(Customer.class);
            query.from(Customer.class);
            customers = session.createQuery(query).getResultList();
            System.out.println("Getting customers");
            System.out.println(customers);
            tx.commit(); // Save everything.
        } catch (Exception exception) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        }
        return customers;
    }

    // Populate the database with customers
    public void populate(List<Customer> customers) {
        for (Customer customer : customers) {
            save(customer);
        }
    }
}
