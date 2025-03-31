
package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static il.cshaifasweng.OCSFMediatorExample.entities.ResInfo.Status.APPROVED;


public class ResInfoRepository extends BaseRepository<ResInfo>
{

    public ResInfoRepository() {
        super();
    }

    @Override
    public int getId(ResInfo entity) {return ((ResInfo)entity).getResID();}

    @Override
    protected Class<ResInfo> getEntityClass() {
        return ResInfo.class;
    }

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
    public void populateResInfo(ResInfo resSInfo)
    {
        Transaction tx = null;
        try(Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx = session.beginTransaction();
            if (resSInfo.getCustomer() != null) {
                session.saveOrUpdate(resSInfo.getCustomer());  // Ensure the customer is saved or updated
            }
            // Print the branch information to confirm branch association
            if (resSInfo.getBranch() == null) {
                System.out.println("[ResInfoRepository] ERROR: Reservation does not have a branch set!");
            } else {
                System.out.println("[ResInfoRepository] Saving reservation for branch id: " + resSInfo.getBranch().getId());
            }


            if (resSInfo.getTable() != null && !resSInfo.getTable().isEmpty()) {
                for (RestTable table : resSInfo.getTable()) {
                    table.addUnavailableFromTime(resSInfo.getHours());
                    session.saveOrUpdate(table);
                }
            }
            session.save(resSInfo);
            // session.flush();  // Force execution of SQL statements
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
    public List<ResInfo> findConflictingReservations(Set<RestTable> tables, LocalTime time) {
        List<ResInfo> conflicts = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ResInfo> cq = cb.createQuery(ResInfo.class);
            Root<ResInfo> root = cq.from(ResInfo.class);
            Join<ResInfo, RestTable> tableJoin = root.join("tables");

            // Filter for reservations using any of the selected tables
            Predicate tableIn = tableJoin.in(tables);

            // Time range: 1.5 hours before and after the given time
            LocalTime startRange = time.minusHours(1).minusMinutes(15);
            LocalTime endRange = time.plusHours(1).plusMinutes(15);
            Predicate timeRange = cb.between(root.get("hours"), startRange, endRange);

            // ignore cancelled reservations
            Predicate notCancelled = cb.isFalse(root.get("isCancelled"));

            cq.select(root).distinct(true)
                    .where(cb.and(tableIn, timeRange, notCancelled));

            conflicts = session.createQuery(cq).getResultList();
            session.getTransaction().commit();
        }
        return conflicts;
    }
    //check if there is a reservation with a customer that has the same email if so returns that customer.
    public Customer getCustomerByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ResInfo> cq = cb.createQuery(ResInfo.class);
            Root<ResInfo> root = cq.from(ResInfo.class);

            // Access nested customer.email
            Predicate emailMatch = cb.equal(root.get("customer").get("email"), email);

            cq.select(root).where(emailMatch);

            ResInfo res = session.createQuery(cq)
                    .setMaxResults(1)
                    .uniqueResult();

            return (res != null) ? res.getCustomer() : null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void setCustomer(ResInfo newReservation) {
        Transaction tx=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx = session.beginTransaction();
            session.saveOrUpdate(newReservation);
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) tx.rollback();
        }
    }
    public String cancelReservation(Integer resID) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            ResInfo reservation = session.get(ResInfo.class, resID);
            if (reservation == null || reservation.getIsCancelled()) {
                tx.rollback();
                return "NOT_FOUND_OR_ALREADY_CANCELLED";
            }

            LocalTime reservationTime = reservation.getHours();
            LocalTime currentTime = LocalTime.now();
            int guests = reservation.getNumOfGuests();
            boolean penalty = currentTime.isAfter(reservationTime.minusHours(1));

            reservation.setIsCancelled(true);

            for (RestTable table : reservation.getTable()) {
                table.removeUnavailableFromTime(reservationTime);
                session.update(table);
            }

            session.update(reservation);
            tx.commit();

            return penalty ? "PENALTY:" + (guests * 10) : "NO_PENALTY";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error cancelling reservation: " + e.getMessage(), e);
        }
    }

    public List<ResInfo> getAllActiveReservations() {
        List<ResInfo> resInfoList=new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT r FROM ResInfo r JOIN FETCH r.customer WHERE r.isCancelled = false",
                    ResInfo.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch active reservations", e);
        }
    }

    public ResInfo refreshReservationWithBranch(int reservationId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Use JOIN FETCH to load the branch eagerly
            ResInfo reservation = session.createQuery(
                            "SELECT r FROM ResInfo r " +
                                    "JOIN FETCH r.branch " +
                                    "JOIN FETCH r.customer " +
                                    "JOIN FETCH r.tables " +
                                    "WHERE r.resID = :resID", ResInfo.class)
                    .setParameter("resID", reservationId)
                    .uniqueResult();

            session.getTransaction().commit();
            return reservation;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


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
