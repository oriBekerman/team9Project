package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;

public class BranchRepository extends BaseRepository<Branch> {
    public BranchRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public int getId(Branch entity) {
        return ((Branch)entity).getBranchID();
    }

    @Override
    protected Class<Branch> getEntityClass() {
        return Branch.class;
    }

    public List<MenuItem> getBranchMenuItems(Branch branch) {
        List<MenuItem> result = new ArrayList<>();
        try {
            int branchID = branch.getBranchID();
            session =openSession();
            session.beginTransaction();
            if(session==null)
            {
                System.out.println("session is null");
            }
            session.clear();
            Query<MenuItem> query = session.createQuery("SELECT m FROM Branch b JOIN b.menuItems m WHERE b.id = :branchId", MenuItem.class);
            query.setParameter("branchId", branchID);
            result = query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            assert session != null;
            session.close();
        }
        return result;
    }
    //change



}
