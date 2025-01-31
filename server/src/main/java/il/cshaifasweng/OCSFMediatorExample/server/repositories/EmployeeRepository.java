package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import org.hibernate.SessionFactory;

public class EmployeeRepository extends BaseRepository<Employee> {

    public EmployeeRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public int getId(Employee entity) {
        return entity.getId();
    }

    @Override
    protected Class<Employee> getEntityClass() {
        return Employee.class;
    }

}
