package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import java.util.List;
import java.util.function.Consumer;
import static jm.task.core.jdbc.util.Util.*;

public class UserDaoHibernateImpl implements UserDao {

    private final SessionFactory sessionFactory = Util.getHibernateSessionFactory();

    public UserDaoHibernateImpl() {}

    private void executeInTransaction(Consumer<Session> action) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            action.accept(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    private void ExecuteQueries(String sql) {
        executeInTransaction(session -> {
            NativeQuery<?> query = session.createNativeQuery(sql);
            query.executeUpdate();
        });
    }

    @Override
    public void createUsersTable() {
        ExecuteQueries(getSQL(CREATE));
    }

    @Override
    public void dropUsersTable() {
        ExecuteQueries(getSQL(DROP));
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        executeInTransaction(session -> {
            User user = new User(name, lastName, age);
            session.persist(user);
        });
    }

    @Override
    public void removeUserById(long id) {
        executeInTransaction(session -> {
            User user = session.get(User.class, id);
            if (user != null) session.remove(user);
        });
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(getSQL(GET), User.class).list();
        }
    }

    @Override
    public void cleanUsersTable() {
        executeInTransaction(session -> {
            session.createNativeQuery(getSQL(CLEAN)).executeUpdate();
        });
    }
}

