package commands;

import entities.User;
import utils.Notifier;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class AlertUserDeadlineCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print("Enter username to notify: ");
        String username = reader.readLine();
        Query bookQuery = entityManager
                .createQuery("SELECT u FROM User AS u WHERE u.username = ?1", User.class);
        bookQuery.setParameter(1, username);
        List<User> users = bookQuery.getResultList();

        if (users.isEmpty()) {
            writer.println("User \'" + username + "\' does not exist.");
            return;
        }

        Notifier.addUserNotification(users.get(0));
        writer.println("User \'" + users.get(0).getUsername() + "\' will be notified when they log in.  ");
    }
}
