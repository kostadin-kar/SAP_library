package commands;

import entities.User;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class ListUsersNotReturnedBooksCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {


        Query userQuery = entityManager
                .createQuery("SELECT u FROM User AS u", User.class);
        List<User> users = userQuery.getResultList();

        writer.println("--Listing users:");
        StringBuilder builder = new StringBuilder();
        for (User user : users) {
            if (user.getBooks().size() > 0) {
                builder.append("--User \'")
                        .append(user.getUsername())
                        .append("\' has not returned all books.")
                        .append(System.lineSeparator());
            }
        }
        writer.print((builder.length() > 0) ? builder.toString() : "-All books are returned and are available.");
    }
}
