package commands;

import entities.Book;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class DeleteBookFromLibraryCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print("--Enter word title or id to continue with removing (title/id): ");
        String option = reader.readLine().toLowerCase();
        while (!option.equals("title") && !option.equals("id")) {
            writer.print("Please enter a valid answer (title/id): ");
            option = reader.readLine().toLowerCase();
        }

        if (option.equals("title")) {
            removeBooksByTitle(entityManager, reader, writer, option);

        } else {
            removeBookById(entityManager, reader, writer, option);

        }
    }

    private void removeBookById(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
            throws NumberFormatException, IOException {

        Integer id = null;
        writer.print("-Enter book id: ");
        option = reader.readLine();
        while (id == null) {
            try {
                id = Integer.parseInt(option);

            } catch (NumberFormatException nfe) {
                writer.print("-Please enter a valid value for id or type exit (id/exit): ");
                option = reader.readLine().trim().toLowerCase();
                if (option.equals("exit")) {
                    writer.println("-No book is removed from library.");
                    return;
                }
            }
        }
        Query query = entityManager
                .createQuery("SELECT b FROM Book AS b WHERE b.id = ?1", Book.class);
        query.setParameter(1, id);
        List<Book> books = query.getResultList();

        if (books.isEmpty()) {
            writer.println("Book with id = " + id + " does not exist.");
            return;
        }
        Book book = books.get(0);

        writer.println("Book \'" + book.getTitle() + "\' is with id = " + id + ". Are you sure you want to remove it? (y/n): ");
        writer.print("Answer: ");
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals("y") && !answer.equals("n")) {
            writer.print("-Please enter a valid answer (y/n): ");
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals("y")) {
            entityManager.getTransaction().begin();
            entityManager.remove(book);
            entityManager.getTransaction().commit();

            writer.println("Successfully removed book \'" + book.getTitle() + "\' with id = " + id + " from library.");
        } else {
            writer.println("Librarian changed their mind, no book is removed.");
        }

    }

    private void removeBooksByTitle(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
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
        writer.print("There are " + books.size() + " copies of book \'" +
                books.get(0).getTitle() + "\'. Do you want to remove all books or single one? (all/one): ");
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals("all") && !answer.equals("one")) {
            writer.print("Please enter a valid answer (all/one): ");
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals("all")) {
            String removedBookTitle = books.get(0).getTitle();
            entityManager.getTransaction().begin();
            for (Book book : books) {
                entityManager.remove(book);
            }
            entityManager.getTransaction().commit();

            writer.println("Successfully removed all copies of book \'" + removedBookTitle + "\' from library.");
        } else {
            removeBookById(entityManager, reader, writer, null);
        }
    }
}
