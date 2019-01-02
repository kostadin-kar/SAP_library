package commands;

import engine.LibraryEngine;
import entities.Book;
import entities.User;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class UserReturnBookCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println("No user logged in.");
            return;
        }

        writer.print("--Enter word title or id to continue with fetching (title/id): ");
        String option = reader.readLine().toLowerCase();
        while (!option.equals("title") && !option.equals("id")) {
            writer.print("Please enter a valid answer (title/id): ");
            option = reader.readLine().toLowerCase();
        }

        if (option.equals("title")) {
            returnBookByTitle(entityManager, reader, writer, option);

        } else {
            returnBookById(entityManager, reader, writer, option);

        }
    }

    private void returnBookById(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
            throws NumberFormatException, IOException {

        Integer id = null;
        writer.print("-Enter book id to return: ");
        option = reader.readLine();
        while (id == null) {
            try {
                id = Integer.parseInt(option);

            } catch (NumberFormatException nfe) {
                writer.print("-Please enter a valid value for id or type exit (id/exit): ");
                option = reader.readLine().trim().toLowerCase();
                if (option.equals("exit")) {
                    writer.println("-No book was returned from user inventory.");
                    return;
                }
            }
        }

        User user = LibraryEngine.loggedInUser;

        Book bookToReturn = null;
        for (Book book : user.getBooks()) {
            if (book.getId().equals(id)) {
                bookToReturn = book;
                break;
            }
        }

        if (bookToReturn == null) {
            writer.println("Book with id = " + id + " is not in inventory of user \'" + user.getUsername() + "\'.");
            return;
        }

        writer.println("Book \'" + bookToReturn.getTitle() + "\' is with id = " + id + ". Do you want to return it? (y/n): ");
        writer.print("Answer: ");
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals("y") && !answer.equals("n")) {
            writer.print("-Please enter a valid answer (y/n): ");
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals("y")) {
            entityManager.getTransaction().begin();
            user.getBooks().remove(bookToReturn);
            bookToReturn.setIsAvailable(true);
            bookToReturn.setReturnDeadline(null);
            entityManager.persist(user);
            entityManager.persist(bookToReturn);
            entityManager.getTransaction().commit();

            writer.println("Successfully returned book \'" + bookToReturn.getTitle() +
                    "\' with id = " + id + " by user \'" + user.getUsername() + "\'.");
        } else {
            writer.println("User changed their mind, no book has been returned.");
        }
    }

    private void returnBookByTitle(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
            throws IOException {

        writer.print("-Enter book title: ");
        option = String.join(" ", reader.readLine().trim().toLowerCase().split("\\s+"));
        Query query = entityManager
                .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) = ?1", Book.class);
        query.setParameter(1, String.join(" ", option.trim().toLowerCase().split("\\s+")));
        List<Book> books = query.getResultList();

        if (books.isEmpty()) {
            writer.println("Book \'" + option + "\' does not exist.");
            return;
        }

        writer.println(books.size() + " copies of book \'" + books.get(0).getTitle() +
                "\' in user's inventory with the following identities: ");

        int i;
        for (i = 0; i < books.size(); i++) {
            if ((i + 1) % 20 == 0) {
                writer.println("");
            }
            writer.print(String.format((i != books.size() - 1) ? " %d, " : " %d", books.get(i).getId()));
        }
        if (i % 20 != 0) {
            writer.println("");
        }

        returnBookById(entityManager, reader, writer, null);
    }
}
