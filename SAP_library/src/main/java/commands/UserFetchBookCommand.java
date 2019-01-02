package commands;

import engine.LibraryEngine;
import entities.Book;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class UserFetchBookCommand implements Command {

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
            fetchBookByTitle(entityManager, reader, writer, option);

        } else {
            fetchBookById(entityManager, reader, writer, option);

        }
    }

    private void fetchBookById(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
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
                    writer.println("-No book was fetched from library.");
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

        if (!book.getIsAvailable()) {
            writer.println("-Book \'" + book.getTitle() + "\' has not been returned yet. Unavailable at the moment.");
            return;
        }

        writer.println("Book \'" + book.getTitle() + "\' is with id = " + id + ". Do you want to fetch it? (y/n): ");
        writer.print("Answer: ");
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals("y") && !answer.equals("n")) {
            writer.print("-Please enter a valid answer (y/n): ");
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals("y")) {
            writer.println("Librarian, do you want to set a return date /default is 30 days/? (y/n): ");
            writer.print("Answer: ");
            String returnDateAnswer = reader.readLine().toLowerCase();
            while (!returnDateAnswer.equals("y") && !returnDateAnswer.equals("n")) {
                writer.print("Please enter a valid answer (y/n): ");
                returnDateAnswer = reader.readLine().toLowerCase();
            }

            if (returnDateAnswer.equals("y")) {
                LocalDate returnDate = null;
                writer.print("-Enter date in the format /yyyy-MM-dd/: ");
                while (returnDate == null) {
                    try {
                        returnDate = LocalDate
                                .parse(reader.readLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        if (returnDate.compareTo(LocalDate.now()) <= 0) {
                            writer.print("-Please enter a future date: ");
                            returnDate = null;
                        }
                    } catch (DateTimeParseException dtpe) {
                        writer.print("-Please enter a date in the specified format /yyyy-MM-dd/: ");
                    }
                }
                book.setReturnDeadline(returnDate);
            } else {
                book.setReturnDeadline(LocalDate.now().plusDays(30L));
            }

            entityManager.getTransaction().begin();
            book.setIsAvailable(false);
            entityManager.persist(book);
            LibraryEngine.loggedInUser.getBooks().add(book);
            entityManager.getTransaction().commit();

            writer.println("Successfully fetched book \'" + book.getTitle() +
                    "\' with id = " + id + " by user \'" + LibraryEngine.loggedInUser.getUsername() + "\'.");
        } else {
            writer.println("User changed their mind, no book has been fetched.");
        }
    }

    private void fetchBookByTitle(EntityManager entityManager, BufferedReader reader, Writer writer, String option)
            throws IOException {


        writer.print("-Enter book title: ");
        option = String.join(" ", reader.readLine().trim().toLowerCase().split("\\s+"));
        Query query = entityManager
                .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) = ?1", Book.class);
        query.setParameter(1, String.join(" ", option.trim().toLowerCase().split("\\s+")));
        List<Book> books = query.getResultList();

        int booksCount = books.size();
        books = books.stream().filter(Book::getIsAvailable).collect(Collectors.toList());

        if (books.isEmpty()) {
            writer.println((booksCount > 0) ? "-Book has not been returned yet." : "-Book does not exist in library.");
            return;
        }

        writer.println(books.size() + " copies of book \'" + books.get(0).getTitle() +
                "\' with the following inventory identities: ");

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

        fetchBookById(entityManager, reader, writer, null);
    }
}
