package il.cshaifasweng.OCSFMediatorExample.server.repositories;

import il.cshaifasweng.OCSFMediatorExample.server.HibernateUtil;
import org.hibernate.Session;


import org.hibernate.Transaction;

import java.util.List;


public abstract class BaseRepository<T> {


    public BaseRepository()
    {}
    public T findById(int id) {
        //search in the database
       try (Session session = HibernateUtil.getSessionFactory().openSession())
       {
           return session.get(getEntityClass(),id);
       }
         catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find entity by ID", e);
        }
//       finally {
//           HibernateUtil.closeSession();  //ensure session is closed
//       }
    }
    protected abstract Class<T> getEntityClass();

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.createQuery("FROM " + getEntityClass().getSimpleName(), getEntityClass()).list();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to find all entities", e);
        }
//        finally {
//            HibernateUtil.closeSession();  //ensure session is closed
//        }
    }

    public void deleteById(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction=session.beginTransaction(); // Start transaction
            // Fetch the entity to ensure it exists before attempting to delete
            T entity = session.get(getEntityClass(), id);
            if (entity == null) {
                System.out.println("Entity with ID " + id + " not found. skipping deletion.");
                session.getTransaction().rollback(); // Rollback to prevent unnecessary commit
                return;
            }
            session.delete(entity);
            transaction.commit();
            System.out.println("Entity with ID " + id + " deleted successfully.");
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to delete entity by ID: " + id, e);
        }
//        finally {
//            HibernateUtil.closeSession();  //ensure session is closed
//        }
    }

    public abstract int getId(T entity);


    public T getByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            return session.get(getEntityClass(),name);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to save entity", e);
        }
//        finally {
//            HibernateUtil.closeSession();  //ensure session is closed
//        }
    }


    protected void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (org.hibernate.exception.ConstraintViolationException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Duplicate entry error: " + e.getMessage());
        } catch (org.hibernate.exception.JDBCConnectionException e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Database connection failed! Please check MySQL.");
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("An unexpected error occurred: " + e.getMessage());
//        } finally {
//            HibernateUtil.closeSession();
//        }
        }
    }

    public boolean checkIfEmpty()
    {
        return findAll().isEmpty();
    }

//    public void populate(List<T> entities) {
//        Transaction transaction = null;
//        try (Session session = HibernateUtil.getSession())
//        {
//
//            for(T entity : entities) {
//                transaction=session.beginTransaction();
//                save(entity);
//                transaction.commit();
//            }
//            session.flush();
//            // Save items to the database
//            transaction.commit();
//        } catch (org.hibernate.exception.ConstraintViolationException e) {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
//            System.err.println("Duplicate entry error: " + e.getMessage());
//        } catch (org.hibernate.exception.JDBCConnectionException e) {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
//            System.err.println("Database connection failed! Please check MySQL.");
//        } catch (Exception e) {
//            if (transaction != null && transaction.isActive()) {
//                transaction.rollback();
//            }
//            System.err.println("An unexpected error occurred: " + e.getMessage());
//        } finally {
//            HibernateUtil.closeSession();
//        }
//    }
    protected void persist(T entity) {
        Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession())
        {
            transaction=session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        }  catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Failed to save entity",e);
        }
//        finally {
//            HibernateUtil.closeSession();  //ensure session is closed
//        }
    }
}