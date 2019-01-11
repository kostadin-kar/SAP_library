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
import java.util.List;
import java.util.stream.Collectors;

public class UserGetBookCommand implements Command {

    private static final String EXIT = "exit";
    private static final String DELIMITER = " ";
    private static final String NO_ANSWER = "n";
    private static final String YES_ANSWER = "y";
    private static final String ANSWER_ID = "id";
    private static final String EMPTY_STRING = "";
    private static final String ANSWER_TITLE = "title";
    private static final String ANSWER_REPLY_MESSAGE = "-Answer: ";

    private static final String ENTER_DATE_MESSAGE = "-Enter date in the format /yyyy-MM-dd/: ";
    private static final String ENTER_BOOK_ID_MESSAGE = "-Enter book id: ";
    private static final String ENTER_BOOK_TITLE_MESSAGE = "-Enter book title: ";
    private static final String ENTER_VALID_YES_NO_ANSWER = "-Please enter a valid answer (y/n): ";
    private static final String ENTER_VALID_FUTURE_DATE_MESSAGE = "-Please enter a future date: ";
    private static final String ENTER_VALID_DATE_IN_GIVEN_FORMAT
            = "-Please enter a date in the specified format /yyyy-MM-dd/: ";
    private static final String ENTER_TITLE_OR_ID_SEARCH_KEYWORD
            = "-Type \'title\' or \'id\' to continue removing. Type (title/id): ";
    private static final String ENTER_VALID_VALUE_FOR_ID_REPLY_MESSAGE
            = "-Please enter a valid value for id or type exit (id/exit): ";
    private static final String ENTER_VALID_TITLE_OR_ANSWER_ANSWER_MESSAGE
            = "Please enter a valid answer (title/id): ";

    private static final String BOOK_WITH_ID_NOT_EXIST = "-Book with id = %d does not exist.";
    private static final String NO_BOOK_FETCHED_MESSAGE = "-No book was fetched from library.";
    private static final String BOOK_NOT_RETURNED_MESSAGE = "-Book has not been returned yet.";
    private static final String NO_USER_LOGGED_IN_MESSAGE = "-No user logged in.";
    private static final String BOOK_NOT_RETURNED_YET_MESSAGE
            = "-Book \'%s\' has not been returned yet. Unavailable at the moment.";
    private static final String BOOK_NOT_EXIST_IN_LIBRARY_MESSAGE = "-Book does not exist in library.";

    private static final String CHANGED_MY_MIND_MESSAGE = "-User changed their mind, no book has been taken.";
    private static final String FETCH_BOOK_QUESTION_MESSAGE
            = "-Book \'%s\' is with id = %d. Do you want to fetch it? (y/n): ";
    private static final String SET_RETURN_DATE_QUESTION_MESSAGE
            = "-User, do you want to set a return date /default is 30 days/? (y/n): ";
    private static final String SUCCESSFULLY_TAKEN_BOOK_MESSAGE
            = "-Successfully book \'%s\' with id = %d by user \'%s\'.";
    private static final String SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE_FORMATTED
            = "-There are %d copies of book \'%s\' with the following inventory identities: ";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(NO_USER_LOGGED_IN_MESSAGE);
            return;
        }

        writer.print(ENTER_TITLE_OR_ID_SEARCH_KEYWORD);
        String option = reader.readLine().toLowerCase();
        while (!option.equals(ANSWER_TITLE) && !option.equals(ANSWER_ID)) {
            writer.print(ENTER_VALID_TITLE_OR_ANSWER_ANSWER_MESSAGE);
            option = reader.readLine().toLowerCase();
        }

        if (option.equals(ANSWER_TITLE)) {
            fetchBookByTitle(bookRepo, userRepo, reader, writer);

        } else {
            fetchBookById(bookRepo, userRepo, reader, writer);

        }
    }

    private void fetchBookById(BookRepository bookRepo, UserRepository userRepo, BufferedReader reader, Writer writer)
            throws NumberFormatException, IOException {

        Integer id = null;
        writer.print(ENTER_BOOK_ID_MESSAGE);
        String option = reader.readLine();
        while (id == null) {
            try {
                id = Integer.parseInt(option);

            } catch (NumberFormatException nfe) {
                writer.print(ENTER_VALID_VALUE_FOR_ID_REPLY_MESSAGE);
                option = reader.readLine().trim().toLowerCase();
                if (option.equals(EXIT)) {
                    writer.println(NO_BOOK_FETCHED_MESSAGE);
                    return;
                }
            }
        }

        List<Book> books = bookRepo.selectBookById(id);

        if (books.isEmpty()) {
            writer.println(String.format(BOOK_WITH_ID_NOT_EXIST, id));
            return;
        }
        Book book = books.get(0);

        if (!book.getIsAvailable()) {
            writer.println(String.format(BOOK_NOT_RETURNED_YET_MESSAGE, book.getTitle()));
            return;
        }

        writer.println(String.format(FETCH_BOOK_QUESTION_MESSAGE, book.getTitle(), id));
        writer.print(ANSWER_REPLY_MESSAGE);
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals(YES_ANSWER) && !answer.equals(NO_ANSWER)) {
            writer.print(ENTER_VALID_YES_NO_ANSWER);
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals(YES_ANSWER)) {
            writer.println(SET_RETURN_DATE_QUESTION_MESSAGE);
            writer.print(ANSWER_REPLY_MESSAGE);
            String returnDateAnswer = reader.readLine().toLowerCase();
            while (!returnDateAnswer.equals(YES_ANSWER) && !returnDateAnswer.equals(NO_ANSWER)) {
                writer.print(ENTER_VALID_YES_NO_ANSWER);
                returnDateAnswer = reader.readLine().toLowerCase();
            }

            if (returnDateAnswer.equals(YES_ANSWER)) {
                LocalDate returnDate = null;
                writer.print(ENTER_DATE_MESSAGE);
                while (returnDate == null) {
                    try {
                        returnDate = LocalDate
                                .parse(reader.readLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                        if (returnDate.compareTo(LocalDate.now()) <= 0) {
                            writer.print(ENTER_VALID_FUTURE_DATE_MESSAGE);
                            returnDate = null;
                        }
                    } catch (DateTimeParseException dtpe) {
                        writer.print(ENTER_VALID_DATE_IN_GIVEN_FORMAT);
                    }
                }
                book.setReturnDeadline(returnDate);
            } else {
                book.setReturnDeadline(LocalDate.now().plusDays(30L));
            }

            book.setIsAvailable(false);
            bookRepo.persist(book);
            LibraryEngine.loggedInUser.getBooks().add(book);
            userRepo.persist(LibraryEngine.loggedInUser);

            writer.println(String.format(
                    SUCCESSFULLY_TAKEN_BOOK_MESSAGE,
                    book.getTitle(), id, LibraryEngine.loggedInUser.getUsername())
            );
        } else {
            writer.println(CHANGED_MY_MIND_MESSAGE);
        }
    }

    private void fetchBookByTitle(BookRepository bookRepo, UserRepository userRepo, BufferedReader reader, Writer writer)
            throws IOException {


        writer.print(ENTER_BOOK_TITLE_MESSAGE);
        String option = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));
        List<Book> books = bookRepo.selectBookByTitle(option);

        int booksCount = books.size();
        books = books.stream().filter(Book::getIsAvailable).collect(Collectors.toList());

        if (books.isEmpty()) {
            writer.println((booksCount > 0) ? BOOK_NOT_RETURNED_MESSAGE : BOOK_NOT_EXIST_IN_LIBRARY_MESSAGE);
            return;
        }

        writer.println(String.format(SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE_FORMATTED, books.size(), books.get(0).getTitle()));

        int i;
        for (i = 0; i < books.size(); i++) {
            if ((i + 1) % 20 == 0) {
                writer.println(EMPTY_STRING);
            }
            writer.print(String.format((i != books.size() - 1) ? " %d, " : " %d", books.get(i).getId()));
        }
        if (i % 20 != 0) {
            writer.println(EMPTY_STRING);
        }

        fetchBookById(bookRepo, userRepo, reader, writer);
    }
}
