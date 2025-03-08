package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import il.cshaifasweng.OCSFMediatorExample.entities.RestTable;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class BranchRepository extends BaseRepository<Branch> {
    public BranchRepository() {
        super();
    }

    @Override
    public int getId(Branch entity) {
        return entity.getBranchID();
    }

    @Override
    protected Class<Branch> getEntityClass() {
        return Branch.class;
    }

    public List<MenuItem> getBranchMenuItems(Branch branch) {
        List<MenuItem> result = new ArrayList<>();
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            Query<MenuItem> query = session.createQuery(
                    "SELECT m FROM Branch b JOIN b.menuItems m WHERE b.id = :branchId",
                    MenuItem.class);
            query.setParameter("branchId", branch.getBranchID());
            result = query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public void populate(List<Branch> branches) {
        for (Branch branch : branches) {
            save(branch);
        }
    }
    public List<MenuItem> getDeliverableMenuItems(Branch branch) {
        List<MenuItem> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<MenuItem> query = session.createQuery(
                    "SELECT mi FROM Branch b JOIN b.deliverableItems mi WHERE b.id = :id",
                    MenuItem.class
            );
            query.setParameter("id", branch.getBranchID());
            result = query.getResultList();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public List<RestTable> getRestTables(Branch branch) {
        List<RestTable> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<RestTable> query=session.createQuery("SELECT rt FROM RestTable rt WHERE rt.branch = :branch",RestTable.class);
            query.setParameter("branch", branch.getBranchID());
            result = query.getResultList();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
