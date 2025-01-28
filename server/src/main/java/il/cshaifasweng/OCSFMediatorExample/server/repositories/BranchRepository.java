package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Branch;
import org.hibernate.SessionFactory;

import java.util.List;

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


}
