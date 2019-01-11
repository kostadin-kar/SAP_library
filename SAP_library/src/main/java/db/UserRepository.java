package db;

import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class UserRepository {
    private EntityManager manager;

    public UserRepository(EntityManager manager) {
        this.manager = manager;
    }

    private static final String SELECT_USERS
            = "SELECT u FROM User AS u";

    private static final String SELECT_USER_BY_USERNAME
            = SELECT_USERS + " WHERE u.username = ?1";

    private static final String SELECT_USER_BY_NAME_AND_PASSWORD
            = SELECT_USER_BY_USERNAME + " AND u.password = ?2";

    public List<User> selectUsers() {
        Query query = manager
                .createQuery(SELECT_USERS);
        return query.getResultList();
    }

    public List<User> selectUsersByUsername(String username) {
        Query query = manager
                .createQuery(SELECT_USER_BY_USERNAME);
        query.setParameter(1, username);
        return query.getResultList();
    }

    public List<User> selectUsersByUsernameAndPassword(String username, String password) {
        Query query = manager
                .createQuery(SELECT_USER_BY_NAME_AND_PASSWORD);
        query.setParameter(1, username);
        query.setParameter(2, password);

        return query.getResultList();
    }

    public void persist(User user) {
        manager.getTransaction().begin();
        manager.persist(user);
        manager.getTransaction().commit();
    }
}
