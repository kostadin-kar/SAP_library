package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.Book;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AddBookToLibraryCommand implements Command {

    private static final String NO_ANSWER = "n";
    private static final String YES_ANSWER = "y";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String ENTER_BOOK_TITLE_MESSAGE = "-Enter book title: ";
    private static final String ENTER_BOOK_AUTHOR_MESSAGE = "-Enter book author: ";
    private static final String ENTER_VALID_DATE_MESSAGE
            = "-Please enter a date in the specified format /yyyy-MM-dd/: ";
    private static final String ENTER_BOOK_RELEASE_DATE_MESSAGE
            = "-Enter book release date in format \'yyyy-MM-dd\': ";
    private static final String SUCCESSFULLY_ADDED_BOOK_MESSAGE
            = "-Successfully added new book \'%s\' with id = \'%d\' to library.";
    private static final String ADD_BOOK_QUESTION_MESSAGE_FORMATTED
            = "-Add book \'%s\' by %s from %s? Type (y/n)";
    private static final String ADMIN_NOT_LOGGED_MESSAGE = "-Admin not logged in.";
    private static final String BOOK_NOT_ADDED_MESSAGE = "-Book was not added to library.";
    private static final String ADMIN_CAN_ADD_BOOKS_MESSAGE = "-Only admin can add books.";
    private static final String ENTER_VALID_YES_NO_ANSWER = "-Please enter a valid answer (y/n): ";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(ADMIN_NOT_LOGGED_MESSAGE);
            return;
        }
        if (!LibraryEngine.loggedInUser.getIsRoleAdmin()) {
            writer.println(ADMIN_CAN_ADD_BOOKS_MESSAGE);
            return;
        }

        writer.print(ENTER_BOOK_TITLE_MESSAGE);
        String title = reader.readLine();
        writer.print(ENTER_BOOK_AUTHOR_MESSAGE);
        String author = reader.readLine();
        writer.print(ENTER_BOOK_RELEASE_DATE_MESSAGE);
        LocalDate releaseDate = null;
        while (releaseDate == null) {
            try {
                releaseDate = LocalDate
                        .parse(reader.readLine(), DateTimeFormatter.ofPattern(DATE_PATTERN));
            } catch (DateTimeParseException dtpe) {
                writer.print(ENTER_VALID_DATE_MESSAGE);
            }
        }

        writer.println(String.format(ADD_BOOK_QUESTION_MESSAGE_FORMATTED, title, author, releaseDate.toString()));
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals(YES_ANSWER) && !answer.equals(NO_ANSWER)) {
            writer.print(ENTER_VALID_YES_NO_ANSWER);
            answer = reader.readLine().toLowerCase();
        }
        if (answer.equals(NO_ANSWER)) {
            writer.println(BOOK_NOT_ADDED_MESSAGE);
            return;
        }

        Book book = new Book(title, author, releaseDate);
        book.setIsAvailable(true);

        bookRepo.persist(book);

        writer.println(String.format(SUCCESSFULLY_ADDED_BOOK_MESSAGE, title, book.getId()));
    }
}
