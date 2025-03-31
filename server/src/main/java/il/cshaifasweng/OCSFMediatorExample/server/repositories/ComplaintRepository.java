package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComplaintRepository extends BaseRepository<Complaint>
{
    public ComplaintRepository() {
        super();
    }
    @Override
    public int getId(Complaint entity) {
        return entity.getComplaintId();
    }
    @Override
    protected Class<Complaint> getEntityClass() {
        return Complaint.class;
    }
    public static boolean populateComplaint(Complaint complaint) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction(); // Start the transaction

            // Check if the customer is transient (not saved in the database yet)
            if (complaint.getCustomer() != null) {
                // Save the customer before saving the complaint
                session.save(complaint.getCustomer());
            }else {
                complaint.setCustomer((Customer) session.merge(complaint.getCustomer()));  // Merge if customer exists
            }

            // Save the complaint
            session.save(complaint);

            // Commit the transaction if everything went smoothly
            session.getTransaction().commit();
            System.out.println("Complaint saved successfully: " + complaint);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false if an error occurred
        }
    }

    //gets all complaint associated to this employee
    public List<Complaint> getComplaintsByEmployee(Integer employeeId) {
        Transaction tx = null;
        List<Complaint> complaints = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complaint> criteriaQuery = builder.createQuery(Complaint.class);
            Root<Complaint> root = criteriaQuery.from(Complaint.class);
            criteriaQuery.select(root)
                    .where(builder.equal(root.get("employee").get("id"),employeeId));

            complaints = session.createQuery(criteriaQuery).getResultList();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();  // Roll back transaction on error
            }
            e.printStackTrace();
        }
        return complaints;
    }

    //get all complaints entered at this date
    public List<Complaint> getComplaintsByDate(LocalDateTime dateTime)
    {
        Transaction tx = null;
        List<Complaint> complaints = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx = session.beginTransaction();
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Complaint> criteriaQuery = builder.createQuery(Complaint.class);
            Root<Complaint> root = criteriaQuery.from(Complaint.class);
            criteriaQuery.select(root)
                    .where(builder.equal(root.get("date"), dateTime));
            complaints = session.createQuery(criteriaQuery).getResultList();
            tx.commit();
        }
        catch (Exception e) {
            if (tx != null) {
                tx.rollback();  // Roll back transaction on error
            }
            e.printStackTrace();
        }
        return complaints;
    }
    public List<Complaint> updateComplaintsList(List<Complaint> complaintsToUpdate) {
        Transaction tx = null;
        List<Complaint> updatedComplaints = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            for (Complaint complaint : complaintsToUpdate) {
                // Attach the complaint to the current session and update
                Complaint updated = (Complaint) session.merge(complaint);
                updatedComplaints.add(updated);
            }

            tx.commit();
            System.out.println("Complaints updated successfully.");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }

        return updatedComplaints;
    }


}
