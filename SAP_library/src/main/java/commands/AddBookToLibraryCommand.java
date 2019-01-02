package commands;

import entities.Book;
import utils.Writer;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddBookToLibraryCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print("--Enter book title: ");
        String title = reader.readLine();
        writer.print("--Enter book author: ");
        String author = reader.readLine();
        writer.print("--Enter book release date in format \'yyyy-MM-dd\': ");
        LocalDate releaseDate = null;
        while (releaseDate == null) {
            try {
                releaseDate = LocalDate
                        .parse(reader.readLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException dtpe) {
                writer.print("-Please enter a date in the specified format /yyyy-MM-dd/: ");
            }
        }

        Book book = new Book(title, author, releaseDate);
        book.setIsAvailable(true);

        entityManager.getTransaction().begin();
        entityManager.persist(book);
        entityManager.getTransaction().commit();

        writer.println("Successfully added new book \'" + title + "\' with id = \'" + book.getId() + "\' to library.");
    }
}
