package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
            if (complaint.getCustomer() != null && complaint.getCustomer().getId() == 0) {
                // Save the customer before saving the complaint
                session.save(complaint.getCustomer());
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


}
