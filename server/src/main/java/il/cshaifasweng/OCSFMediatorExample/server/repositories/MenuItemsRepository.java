package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.MenuItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class MenuItemsRepository extends BaseRepository<MenuItem>
{
//    private final SessionFactory sessionFactory;
    public MenuItemsRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    @Override
    public int getId(MenuItem entity) {
        return ((MenuItem)entity).getItemID();
    }

    @Override
    protected Class<MenuItem> getEntityClass() {
        return MenuItem.class;
    }
    private List<MenuItem> getMenuItems(Session session)
    {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MenuItem> query = builder.createQuery(MenuItem.class);
        query.from(MenuItem.class);
        List<MenuItem> data = session.createQuery(query).getResultList();
        System.out.println("getting menu items");
        System.out.println(data);
        return data;
    }
}
