package commands;

import entities.Book;
import entities.User;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CheckBookAvailabilityCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print("--Enter book to check: ");
        String title = String.join(" ", reader.readLine().trim().toLowerCase().split("\\s+"));

        Query bookQuery = entityManager
                .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :title AND b.isAvailable = true", Book.class);
        bookQuery.setParameter("title", "%" + title + "%");
        List<Book> books = bookQuery.getResultList();

        books = books.stream()
                .filter(Book::getIsAvailable)
                .collect(Collectors.toList());

        if (books == null) {
            writer.println("-Book is not available at library.");
        } else if (!books.isEmpty()) {
            writer.println("-Book \'" + books.get(0).getTitle() + "\' with id = " + books.get(0).getId() + " is available at library.");
        }
        writer.println("-Asking users if they have copies of the book...");

        Query userQuery = entityManager
                .createQuery("SELECT u FROM User AS u", User.class);
        List<User> users = userQuery.getResultList();
        if (users.isEmpty()) {
            writer.println("There are no users to ask...");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (User user : users) {
            for (Book book : user.getBooks()) {
                if (book.getTitle().toLowerCase().contains(title)) {
                    builder.append("--User \'")
                            .append(user.getUsername())
                            .append("\' has \'")
                            .append(book.getTitle())
                            .append("\' in their inventory.")
                            .append(System.lineSeparator());
                }
            }
        }
        writer.println((builder.length() > 0) ? builder.toString() : "--No user has a copy of book \'" + title + "\'.");
    }
}
