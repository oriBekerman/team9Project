package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Delivery;
import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ComplaintRepository extends BaseRepository<Complaint> {
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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Save the customer first if it's not null
            if (complaint.getCustomer() != null) {
                session.saveOrUpdate(complaint.getCustomer());  // Ensure the customer is saved or updated
            }
            session.save(complaint);
            tx.commit();
            System.out.println("[ComplaintRepository]Complaint saved successfully: " + complaint);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Complaint> getComplaintsForReport(int branchId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Complaint> complaints = session.createQuery("FROM Complaint WHERE branch.id = :branchId", Complaint.class)
                    .setParameter("branchId", branchId)
                    .getResultList();

            // Explicitly initialize branch to avoid lazy initialization
            complaints.forEach(c -> Hibernate.initialize(c.getBranch()));

            return complaints;
        }
    }


}

