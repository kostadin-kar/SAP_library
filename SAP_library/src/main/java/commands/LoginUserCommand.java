package commands;

import engine.LibraryEngine;
import entities.User;
import utils.Notifier;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class LoginUserCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser != null) {
            writer.println("A user is already logged in.");
            return;
        }

        writer.print("--Enter username: ");
        String username = reader.readLine();
        writer.print("--Enter password: ");
        String password = reader.readLine();

        Query query = entityManager
                .createQuery("SELECT u FROM User AS u WHERE u.username = ?1 AND u.password = ?2");
        query.setParameter(1, username);
        query.setParameter(2, password);

        List<User> users = query.getResultList();
        if (users.isEmpty()) {
            writer.println("Incorrect username or password");
            return;
        }

        LibraryEngine.loggedInUser = users.get(0);
        writer.println("Logged in as " + username);

        //Notify user when they log in
        writer.print(Notifier.notify(users.get(0)));
    }
}
