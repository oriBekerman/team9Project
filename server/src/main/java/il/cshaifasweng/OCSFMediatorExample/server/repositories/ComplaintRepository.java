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
    public boolean populateComplaint(Complaint complaint) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
                tx=session.beginTransaction();
            // Save the customer first if it's not null
            if (complaint.getCustomer() != null)
            {
                session.saveOrUpdate(complaint.getCustomer());  // Ensure the customer is saved or updated
            }
            session.save(complaint);
            tx.commit();
            System.out.println("Complaint saved successfully: " + complaint);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
