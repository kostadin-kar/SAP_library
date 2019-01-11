package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.Book;
import entities.User;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class UserReturnBookCommand implements Command {

    private static final String EXIT = "exit";
    private static final String DELIMITER = " ";
    private static final String NO_ANSWER = "n";
    private static final String YES_ANSWER = "y";
    private static final String ANSWER_ID = "id";
    private static final String ANSWER_TITLE = "title";
    private static final String ANSWER_REPLY_MESSAGE = "-Answer: ";

    private static final String ENTER_TITLE_MESSAGE = "-Enter book title: ";
    private static final String ENTER_VALID_YES_NO_ANSWER = "-Please enter a valid answer (y/n): ";
    private static final String ENTER_TITLE_OR_ID_SEARCH_KEYWORD
            = "-Type \'title\' or \'id\' to continue removing. Type (title/id): ";
    private static final String ENTER_VALID_VALUE_FOR_ID_REPLY_MESSAGE
            = "-Please enter a valid value for id or type exit (id/exit): ";
    private static final String ENTER_VALID_TITLE_OR_ANSWER_ANSWER_MESSAGE
            = "-Please enter a valid answer (title/id): ";

    private static final String BOOK_NOT_EXIST_MESSAGE = "-Book \'%s\' does not exist.";
    private static final String CHANGED_MY_MIND_MESSAGE = "-User changed their mind, no book has been returned.";
    private static final String NO_BOOK_RETURNED_MESSAGE = "-No book was returned from user inventory.";
    private static final String NO_USER_LOGGED_IN_MESSAGE = "-No user logged in.";
    private static final String BOOK_ID_TO_RETURN_MESSAGE = "-Enter book id to return: ";
    private static final String BOOK_NOT_IN_USER_INVENTORY_MESSAGE
            = "-Book with id = %d is not in inventory of user \'%s\'.";

    private static final String SUCCESSFUL_BOOK_RETURN_MESSAGE
            = "-Successfully returned book \'%s\' with id = %d by user \'%s\'.";
    private static final String BOOK_TO_RETURN_QUESTION_MESSAGE
            = "-Book \'%s\' is with id = %d. Do you want to return it? (y/n): ";
    private static final String SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE_FORMATTED
            = "-There are %d copies of book \'%s\' in user's inventory with the following identities: ";

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
            returnBookByTitle(bookRepo, userRepo, reader, writer);

        } else {
            returnBookById(bookRepo, userRepo, reader, writer);

        }
    }

    private void returnBookById(BookRepository bookRepo, UserRepository userRepo, BufferedReader reader, Writer writer)
            throws NumberFormatException, IOException {

        Integer id = null;
        writer.print(BOOK_ID_TO_RETURN_MESSAGE);
        String option = reader.readLine();
        while (id == null) {
            try {
                id = Integer.parseInt(option);

            } catch (NumberFormatException nfe) {
                writer.print(ENTER_VALID_VALUE_FOR_ID_REPLY_MESSAGE);
                option = reader.readLine().trim().toLowerCase();
                if (option.equals(EXIT)) {
                    writer.println(NO_BOOK_RETURNED_MESSAGE);
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
            writer.println(String.format(BOOK_NOT_IN_USER_INVENTORY_MESSAGE, id, user.getUsername()));
            return;
        }

        writer.println(String.format(BOOK_TO_RETURN_QUESTION_MESSAGE, bookToReturn.getTitle(), id));
        writer.print(ANSWER_REPLY_MESSAGE);
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals(YES_ANSWER) && !answer.equals(NO_ANSWER)) {
            writer.print(ENTER_VALID_YES_NO_ANSWER);
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals("y")) {
            bookToReturn.setIsAvailable(true);
            bookToReturn.setReturnDeadline(null);
            bookRepo.merge(bookToReturn);

            user.getBooks().remove(bookToReturn);
            userRepo.persist(user);

            writer.println(String.format(SUCCESSFUL_BOOK_RETURN_MESSAGE, bookToReturn.getTitle(), id, user.getUsername()));
        } else {
            writer.println(CHANGED_MY_MIND_MESSAGE);
        }
    }

    private void returnBookByTitle(BookRepository bookRepo, UserRepository userRepo, BufferedReader reader, Writer writer)
            throws IOException {

        writer.print(ENTER_TITLE_MESSAGE);
        String option = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));
        List<Book> books = bookRepo.selectBookByTitle(option);

        if (books.isEmpty()) {
            writer.println(String.format(BOOK_NOT_EXIST_MESSAGE, option));
            return;
        }

        writer.println(String.format(SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE_FORMATTED, books.size(), books.get(0).getTitle() ));

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

        returnBookById(bookRepo, userRepo, reader, writer);
    }
}
