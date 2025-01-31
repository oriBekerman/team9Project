package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Customer;
import org.hibernate.SessionFactory;

public class CustomerRepository extends BaseRepository<Customer> {

    public CustomerRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public int getId(Customer entity) {
        return entity.getId();
    }

    @Override
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

}
