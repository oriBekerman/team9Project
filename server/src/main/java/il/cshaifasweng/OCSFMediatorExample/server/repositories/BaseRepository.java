package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import com.sun.glass.ui.Clipboard;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static il.cshaifasweng.OCSFMediatorExample.server.SimpleServer.session;


public abstract class BaseRepository<T> {
    protected final List<T> storage = new ArrayList<>(); // Simulated in-memory storage
    protected final SessionFactory sessionFactory;

    public BaseRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void saveToStorage(T entity) {
        storage.add(entity);
    }

    public void deleteFromStorage(T entity) {
        storage.remove(entity);
    }

    public T findById(int id) {
        // First, search in in-memory storage
        T result = storage.stream()
                .filter(entity -> getId(entity) == id)
                .findFirst()
                .orElse(null);

        if (result != null) {
            return result;
        }
        // If not found, search in the database
        Session session = null;
        try {
            session = openSession();
            return session.get(getEntityClass(), id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find entity by ID", e);
        } finally {
            closeSession(session);
        }
    }

    public List<T> findAll() {
        // Merge in-memory and database storage
        Session session = null;
        try {
            session =openSession();
            List<T> databaseResults = session.createQuery("from " + getEntityClass().getName(), getEntityClass()).list();
            List<T> combinedResults = new ArrayList<>(databaseResults);
            combinedResults.addAll(storage);
            return combinedResults;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find all entities", e);
        }
    }

    public void deleteById(int id) {
        Session session = null;
        try {
            session = openSession();  // Open Hibernate session
            session.beginTransaction(); // Start transaction

            // Fetch the entity to ensure it exists before attempting to delete
            T entity = session.get(getEntityClass(), id);
            if (entity == null) {
                System.out.println("Entity with ID " + id + " not found. Skipping deletion.");
                session.getTransaction().rollback(); // Rollback to prevent unnecessary commit
                return;
            }

            // Delete the entity
            session.delete(entity);
            session.getTransaction().commit(); // Commit transaction
            System.out.println("Entity with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            if (session != null && session.getTransaction().isActive()) {
                session.getTransaction().rollback(); // Rollback in case of error
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to delete entity by ID: " + id, e);
        } finally {
            if (session != null) {
                session.close(); // Ensure session is closed
            }
        }
    }


    public abstract int getId(T entity);


    public T getByName(String name) {
        Session session = null;
        try {
            session = openSession();
            return session.get(getEntityClass(),name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save entity", e);
        } finally {
            closeSession(session);
        }
    }

    protected abstract Class<T> getEntityClass();

    protected void save(T entity) {
        Session session = null;
        try {
            session =openSession();
            session.beginTransaction();
            session.save(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save entity", e);
        }finally {
            closeSession(session);
        }
    }
    public boolean checkIfEmpty()
    {
        return findAll().isEmpty();
    }
    public void populate(List<T> entities) {
        Session session = null;
        try {
            session =openSession();
            session.beginTransaction();
            for(T entity : entities) {
                save(entity);
            }
            session.flush();
            // Save items to the database
            session.getTransaction().commit();
            System.out.println("Data initialization completed!");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize data", e);
        }finally {
            closeSession(session);
        }
    }
    protected Session openSession() {
        return sessionFactory.openSession();
    }

    protected void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

}