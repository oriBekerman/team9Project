package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
public class EmployeeRepository extends BaseRepository<Employee> {

    public EmployeeRepository() {super();
    }

    @Override
    public int getId(Employee entity) {
        return entity.getId();
    }

    @Override
    protected Class<Employee> getEntityClass() {
        return Employee.class;
    }

    //find employee by username
    public Employee findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Employee WHERE username = :username", Employee.class)
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    // get Employee form database returns employeesList
    public List<Employee> getEmployee()
    {
        List<Employee> data=new ArrayList<>();
        Transaction tx=null;
        try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            tx=session.beginTransaction();
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
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("An error occured, changes have been rolled back.");
            exception.printStackTrace();
        }
        return data;
    }

    public void populate(List<Employee> employees) {
        for (Employee employee : employees) {
            save(employee);
        }
    }
}
