
package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
            System.out.println("getting reservations information");
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
    public void populateResInfo(ResInfo resSInfo) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            if (resSInfo.getCustomer() != null) {
                session.saveOrUpdate(resSInfo.getCustomer());
            }

            if (resSInfo.getBranch() != null) {
                session.saveOrUpdate(resSInfo.getBranch());
            }

            if (resSInfo.getTable() != null && !resSInfo.getTable().isEmpty()) {
                for (RestTable table : resSInfo.getTable()) {
                    table.addUnavailableFromTime(resSInfo.getHours());
                    session.saveOrUpdate(table);
                }
            }
            session.save(resSInfo);
            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.getStatus().canRollback()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to populate reservation", e);
        }
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
    public ResInfo addReservation(ResInfo reservation) {
        Transaction tx = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Save or update customer if needed
            if (reservation.getCustomer() != null) {
                session.saveOrUpdate(reservation.getCustomer());
            }

            // Save or update the branch only if necessary
            if (reservation.getBranch() != null) {
                session.saveOrUpdate(reservation.getBranch());
            }

            // Save or update each table and mark them unavailable at the reservation time
            if (reservation.getTable() != null) {
                for (RestTable table : reservation.getTable()) {
                    table.addUnavailableFromTime(reservation.getHours());
                    session.saveOrUpdate(table);
                }
            }

            // Save the reservation itself
            session.save(reservation);
            tx.commit();

            return reservation;

        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Failed to add reservation", e);
        }
    }



    public void deleteReservation(ResInfo reservation)
    {
        deleteById(reservation.getResID());
    }
    public void saveCustomer(Customer customer)
    {
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx = session.beginTransaction();
            session.saveOrUpdate(customer);
            tx.commit();
        }
        catch (Exception e) {
            System.out.println("customer save failed");
            e.printStackTrace();
        }
    }


}
