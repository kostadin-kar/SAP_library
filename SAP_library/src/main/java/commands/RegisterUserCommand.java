package commands;

import entities.User;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class RegisterUserCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {
        writer.print("--Enter username: ");
        String username = reader.readLine();
        writer.print("--Enter password: ");
        String password = reader.readLine();

        Query query = entityManager
                .createQuery("SELECT u FROM User AS u WHERE u.username = ?1 AND u.password = ?2", User.class);
        query.setParameter(1, username);
        query.setParameter(2, password);

        List<User> users = query.getResultList();
        if (!users.isEmpty()) {
            writer.println("User " + username + " already exists.");
            return;
        }

        User userEntity = new User(username, password);

        entityManager.getTransaction().begin();
        entityManager.persist(userEntity);
        entityManager.getTransaction().commit();

        writer.println("Successfully registered user " + username);
    }
}
