//package il.cshaifasweng.OCSFMediatorExample.server.repositories;
//
//import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
//import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
//import org.hibernate.Session;
//import org.hibernate.query.Query;
//
//public class CustomerRepository extends BaseRepository<Customer> {
//
//    @Override
//    public int getId(Customer entity) {
//        return entity.getId();
//    }
//
//    @Override
//    protected Class<Customer> getEntityClass() {
//        return Customer.class;
//    }
//
//    public Customer findByEmail(String email) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<Customer> query = session.createQuery(
//                    "FROM Customer WHERE email = :email", Customer.class);
//            query.setParameter("email", email);
//            return query.uniqueResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//}
