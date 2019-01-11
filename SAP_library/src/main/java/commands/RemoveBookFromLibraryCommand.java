package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.Book;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveBookFromLibraryCommand implements Command {

    private static final String EXIT = "exit";
    private static final String DELIMITER = " ";
    private static final String NO_ANSWER = "n";
    private static final String YES_ANSWER = "y";
    private static final String OPTION_ID = "id";
    private static final String EMPTY_STRING = "";
    private static final String ONE_ANSWER = "one";
    private static final String ALL_ANSWER = "all";
    private static final String OPTION_TITLE = "title";
    private static final String ANSWER_REPLY_MESSAGE = "-Answer: ";

    private static final String ENTER_BOOK_ID_MESSAGE = "-Enter book id:";
    private static final String ENTER_BOOK_TITLE_MESSAGE = "-Enter book title: ";
    private static final String ENTER_VALID_ALL_OR_ONE_MESSAGE
            = "-Please enter a valid answer (all/one): ";
    private static final String ENTER_TITLE_OR_ID_SEARCH_KEYWORD
            = "-Type \'title\' or \'id\' to continue removing. Type (title/id): ";
    private static final String ENTER_VALID_VALUE_FOR_ID_REPLY_MESSAGE
            = "-Please enter a valid value for id or type exit (id/exit): ";
    private static final String ENTER_VALID_TITLE_OR_ANSWER_ANSWER_MESSAGE
            = "-Please enter a valid answer (title/id): ";

    private static final String CHANGED_MY_MIND_MESSAGE = "-No book is removed.";
    private static final String NO_BOOKS_REMOVED_MESSAGE = "-No book is removed from library.";
    private static final String BOOK_WITH_TITLE_NOT_EXIST = "-Book \'%s\' does not exist.";
    private static final String ENTER_VALID_YES_NO_ANSWER = "-Please enter a valid answer (y/n): ";
    private static final String REMOVE_BOOK_QUESTION_MESSAGE
            = "-Book \'%s\' by %s is with id = %d. Are you sure you want to remove it? Type (y/n): ";
    private static final String BOOK_WITH_ID_NOT_EXIST_MESSAGE = "-Book with id = %d does not exist.";
    private static final String REMOVE_ALL_OR_ONE_BOOK_MESSAGE
            = "-There are %d copies of book \'%s\'. Remove all books or a single one? Type (all/one): ";
    private static final String SUCCESSFULLY_REMOVED_BOOK_MESSAGE
            = "-Successfully removed book \'%s\' with id = %d from library.";
    private static final String SUCCESSFULLY_REMOVED_ALL_AVAILABLE_COPIES_MESSAGE
            = "-Successfully removed all copies of book \'%s\' from library.";
    private static final String SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE
            = "- %d copies of book \'%s\' with the following id's: ";
    private static final String BOOKS_NOT_RETURNED_YET_MESSAGE_FORMATTED
            = "-Book \'%s\' with id = %d has not been returned yet.";
    private static final String BOOKS_NOT_RETURNED_YET_MESSAGE = "-Selected book/books not returned yet.";
    private static final String ADMIN_CAN_REMOVE_BOOKS_MESSAGE = "-Only admin can remove available books.";
    private static final String ADMIN_NOT_LOGGED_MESSAGE = "-Admin not logged in.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(ADMIN_NOT_LOGGED_MESSAGE);
            return;
        }
        if (!LibraryEngine.loggedInUser.getIsRoleAdmin()) {
            writer.println(ADMIN_CAN_REMOVE_BOOKS_MESSAGE);
            return;
        }

        writer.print(ENTER_TITLE_OR_ID_SEARCH_KEYWORD);
        String option = reader.readLine().trim().toLowerCase();
        while (!option.equals(OPTION_TITLE) && !option.equals(OPTION_ID)) {
            writer.print(ENTER_VALID_TITLE_OR_ANSWER_ANSWER_MESSAGE);
            option = reader.readLine().trim().toLowerCase();
        }

        if (option.equals(OPTION_TITLE)) {
            removeBooksByTitle(bookRepo, reader, writer);

        } else {
            removeBookById(bookRepo, reader, writer);

        }
    }

    private void removeBookById(BookRepository bookRepo, BufferedReader reader, Writer writer)
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
                    writer.println(NO_BOOKS_REMOVED_MESSAGE);
                    return;
                }
            }
        }

        List<Book> books = bookRepo.selectBookById(id);

        if (books.isEmpty()) {
            writer.println(String.format(BOOK_WITH_ID_NOT_EXIST_MESSAGE, id));
            return;
        }
        Book book = books.get(0);

        if (!book.getIsAvailable()) {
            writer.println(String.format(BOOKS_NOT_RETURNED_YET_MESSAGE_FORMATTED, book.getTitle(), book.getId()));
            return;
        }
        writer.println(String.format(REMOVE_BOOK_QUESTION_MESSAGE, book.getTitle(), book.getAuthor(), id));
        writer.print(ANSWER_REPLY_MESSAGE);
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals(YES_ANSWER) && !answer.equals(NO_ANSWER)) {
            writer.print(ENTER_VALID_YES_NO_ANSWER);
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals(YES_ANSWER)) {
            bookRepo.remove(book);
            writer.println(String.format(SUCCESSFULLY_REMOVED_BOOK_MESSAGE, book.getTitle(), id));

        } else {
            writer.println(CHANGED_MY_MIND_MESSAGE);
        }

    }

    private void removeBooksByTitle(BookRepository bookRepo, BufferedReader reader, Writer writer)
            throws IOException {

        writer.print(ENTER_BOOK_TITLE_MESSAGE);
        String option = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));
        List<Book> books = bookRepo.selectBookByTitle(option);

        if (books.isEmpty()) {
            writer.println(String.format(BOOK_WITH_TITLE_NOT_EXIST, option));
            return;
        }
        books = books.stream().filter(Book::getIsAvailable).collect(Collectors.toList());
        if (books.isEmpty()) {
            writer.println(BOOKS_NOT_RETURNED_YET_MESSAGE);
            return;
        }

        writer.print(String.format(REMOVE_ALL_OR_ONE_BOOK_MESSAGE, books.size(),  books.get(0).getTitle()));
        String answer = reader.readLine().toLowerCase();
        while (!answer.equals(ALL_ANSWER) && !answer.equals(ONE_ANSWER)) {
            writer.print(ENTER_VALID_ALL_OR_ONE_MESSAGE);
            answer = reader.readLine().toLowerCase();
        }

        if (answer.equals(ALL_ANSWER)) {
            bookRepo.removeList(books);
            String removedBookTitle = books.get(0).getTitle();

            writer.println(String.format(SUCCESSFULLY_REMOVED_ALL_AVAILABLE_COPIES_MESSAGE, removedBookTitle));
        } else {
            writer.println(String.format(SO_MANY_COPIES_WITH_FOLLOWING_IDENTITIES_MESSAGE, books.size(), books.get(0).getTitle()));

            int i;
            for (i = 0; i < books.  size(); i++) {
                if ((i + 1) % 20 == 0) {
                    writer.println(EMPTY_STRING);
                }
                writer.print(String.format((i != books.size() - 1) ? " %d, " : " %d", books.get(i).getId()));
            }
            if (i % 20 != 0) {
                writer.println(EMPTY_STRING);
            }

            removeBookById(bookRepo, reader, writer);
        }
    }
}
