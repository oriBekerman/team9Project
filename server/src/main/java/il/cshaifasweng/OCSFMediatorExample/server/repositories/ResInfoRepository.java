
package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.entities.ResInfo.Status.APPROVED;


public class ResInfoRepository extends BaseRepository<ResInfo>
{

    public ResInfoRepository() {
        super();
    }

    ///  both of them to copy ( to my new EmployeeRepository )and change just name + type

    @Override
    public int getId(ResInfo entity) {return ((ResInfo)entity).getResID();}

    @Override
    protected Class<ResInfo> getEntityClass() {
        return ResInfo.class;
    }

    /// //////

    // get ResSInfo form database returns resSInfoList
    public List<ResInfo> getAllResSInfo()
    {
        List<ResInfo> data =new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            session.beginTransaction();
            //get reservations from database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ResInfo> query = builder.createQuery(ResInfo.class);
            query.from(ResInfo.class);
            data = session.createQuery(query).getResultList();
            System.out.println("[ResInfoRepository]getting reservations information");
            System.out.println(data);
            session.getTransaction().commit(); // Save everything.
        }
        return data;
    }

    public void populate(List<ResInfo> resSInfo) {
        for (ResInfo resInfo : resSInfo) {
            populateResInfo(resInfo);
        }
    }
//    public void populateResInfo(ResInfo resSInfo)
//    {
//        Transaction tx = null;
//        try(Session session = HibernateUtil.getSessionFactory().openSession())
//        {
//            tx = session.beginTransaction();
//            if (resSInfo.getCustomer() != null) {
//                session.saveOrUpdate(resSInfo.getCustomer());  // Ensure the customer is saved or updated
//            }
//            session.save(resSInfo);
//            tx.commit();
//        }
//        catch (Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//    }

    public void populateResInfo(ResInfo resSInfo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Print the customer information before saving
            if (resSInfo.getCustomer() != null) {
                System.out.println("[ResInfoRepository] Saving customer: " + resSInfo.getCustomer());
                session.saveOrUpdate(resSInfo.getCustomer());
            }

            // Print the branch information to confirm branch association
            if (resSInfo.getBranch() == null) {
                System.out.println("[ResInfoRepository] ERROR: Reservation does not have a branch set!");
            } else {
                System.out.println("[ResInfoRepository] Saving reservation for branch id: " + resSInfo.getBranch().getId());
            }

            // Save the reservation and flush to catch any constraint violations early
            session.save(resSInfo);
            session.flush();  // Force execution of SQL statements

            tx.commit();
        } catch (Exception e) {
            System.err.println("[ResInfoRepository] Exception while populating reservation: " + e.getMessage());
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
        System.out.println("[ResInfoRepository] Added new reservation: " + resSInfo.toString());
    }



    public List<ResInfo> getReservationsByBranchAndMonth(int branchId, String monthYear) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ResInfo> cq = cb.createQuery(ResInfo.class);
            Root<ResInfo> root = cq.from(ResInfo.class);

            // Assuming 'monthYear' is in "MM/YYYY" format
            Predicate branchPredicate = cb.equal(root.get("branch"), branchId);
            Expression<String> monthExpression = cb.function("SUBSTRING", String.class, root.get("date"), cb.literal(4), cb.literal(7)); // Extracts "MM/YYYY" from "DD/MM/YYYY"
            Predicate monthPredicate = cb.equal(monthExpression, monthYear);

            cq.where(cb.and(branchPredicate, monthPredicate));
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    public ResInfo addReservation(ResInfo reservation)
    {
        if(reservation.branchIsSet && reservation.customerIsSet
                && reservation.tableIsSet && reservation.getStatus().equals(APPROVED))
        {
           save(reservation);

        }
        return reservation;
    }
    public void deleteReservation(ResInfo reservation)
    {
        deleteById(reservation.getResID());
    }

//    public List<ResInfo> getReservationsForReport(int branchId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            String queryString = "SELECT r FROM ResInfo r JOIN FETCH r.customer WHERE r.branch.id = :branchId";
//            Query<ResInfo> query = session.createQuery(queryString, ResInfo.class);
//            query.setParameter("branchId", branchId);
//            return query.getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

//    public List<ResInfo> getReservationsForReport(int branchId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Query<ResInfo> query = session.createQuery("FROM ResInfo WHERE branch.id = :branchId", ResInfo.class);
//            query.setParameter("branchId", branchId);
//            return query.getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }

    public List<ResInfo> getReservationsForReport(int branchId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ResInfo r LEFT JOIN FETCH r.customer LEFT JOIN FETCH r.branch WHERE r.branch.id = :branchId";
            List<ResInfo> reservations = session.createQuery(hql, ResInfo.class)
                    .setParameter("branchId", branchId)
                    .getResultList();

            // Explicitly initialize any lazy relationships
            reservations.forEach(res -> {
                Hibernate.initialize(res.getCustomer());
                Hibernate.initialize(res.getBranch());
            });

            return reservations;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }


}
