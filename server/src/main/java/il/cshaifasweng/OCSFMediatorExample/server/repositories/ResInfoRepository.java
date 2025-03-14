
package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.ResInfo;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


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
        for (ResInfo res : resSInfo) {
            save(res);
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


}
