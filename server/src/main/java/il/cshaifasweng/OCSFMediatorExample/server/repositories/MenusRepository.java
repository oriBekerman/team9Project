//package il.cshaifasweng.OCSFMediatorExample.server.repositories;
//
//
//import il.cshaifasweng.OCSFMediatorExample.entities.*;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.Transaction;
//
//import javax.persistence.criteria.CriteriaBuilder;
//import javax.persistence.criteria.CriteriaQuery;
//import java.util.ArrayList;
//import java.util.List;
//
//import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;
//
//public class MenusRepository extends BaseRepository<Menu>{
//
//    public MenusRepository(SessionFactory sessionFactory) {
//        super(sessionFactory);
//    }
//
//    @Override
//    public int getId(Menu entity) {
//        return entity.getMenuID();
//    }
//    @Override
//    protected Class<Menu> getEntityClass() {
//        return Menu.class;
//    }
//    public Menu getBaseMenu()
//    {
//        return findById(1);
//    }
//    public void add(Menu menu)
//    {
//        save(menu);
//    }
//    public Menu getMenuByBranchID(int branchID){
//        // If not found, search in the database
//        Session session = null;
//        try
//        {
//            session = openSession();
//            return session.get(Menu.class, branchID);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to find entity by ID", e);
//        } finally {
//            closeSession(session);
//        }
//    }
//}
