package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import net.bytebuddy.asm.Advice;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RestTableRepository extends BaseRepository<RestTable> {

    public RestTableRepository(){
        super();
    }

    @Override
    public int getId(RestTable entity) {
        return entity.getId();
    }

    @Override
    protected Class<RestTable> getEntityClass() {
        return RestTable.class;
    }
    public void populate(List<RestTable> tables) {
        for (RestTable table : tables) {
            save(table);
        }
    }
//    public List<LocalTime> getUnavailableTimes() {
//        List<LocalTime> times = new ArrayList<>();
//        try (Session session = HibernateUtil.getSessionFactory().openSession())
//        {
//            Query<RestTable> query = session.createQuery("from RestTable");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return result;
//    }

}
