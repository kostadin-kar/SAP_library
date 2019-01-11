package db;

import entities.Notification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class NotificationRepo {

    private EntityManager manager;

    public NotificationRepo(EntityManager manager) {
        this.manager = manager;
    }

    public List<Notification> selectNotificationByUserId(int userId) {
        Query query = manager
                .createQuery("SELECT n FROM Notification AS n WHERE n.userId = ?1");
        query.setParameter(1, userId);
        return query.getResultList();
    }

    public void persist(Notification notification) {
        manager.getTransaction().begin();
        manager.persist(notification);
        manager.getTransaction().commit();
    }

    public void remove(Notification notification) {
        manager.getTransaction().begin();
        manager.remove(notification);
        manager.getTransaction().commit();
    }
}
