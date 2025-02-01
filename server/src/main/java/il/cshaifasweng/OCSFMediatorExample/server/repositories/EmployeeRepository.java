package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import org.hibernate.SessionFactory;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;

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
    // get Employee form database returns employeesList
    public List<Employee> getEmployee()
    {
        List<Employee> data=new ArrayList<>();
        try {
            session =openSession();
            session.beginTransaction();
            //get items from database
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Employee> query = builder.createQuery(Employee.class);
            query.from(Employee.class);
            data = session.createQuery(query).getResultList();
            System.out.println("getting employees");
            System.out.println(data);
            session.getTransaction().commit(); // Save everything.
        } catch (Exception exception)
        {
            if (session != null)
            {
                session.getTransaction().rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        }
        finally {
            session.close();
        }
        return data;
    }

}
