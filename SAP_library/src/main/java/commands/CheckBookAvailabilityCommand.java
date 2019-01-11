package commands;

import db.BookRepository;
import db.UserRepository;
import entities.Book;
import entities.User;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class CheckBookAvailabilityCommand implements Command {

    private static final String DELIMITER = " ";
    private static final String NO_USERS_REPLY_MESSAGE = "There are no users to ask...";
    private static final String ENTER_BOOK_TO_CHECK_MESSAGE = "-Enter book title to check: ";
    private static final String BOOK_NOT_AVAILABLE_MESSAGE = "-Book is not available at library.";
    private static final String NO_USER_HAS_COPIES_MESSAGE = "--No user has a copy of book \'%s\'.";
    private static final String USER_HAS_BOOK_MESSAGE = "--User \'%s\' has \'%s\' in their inventory.";
    private static final String ASKING_USERS_MESSAGE = "-Asking users if they have copies of the book...";
    private static final String BOOK_AVAILABLE_MESSAGE = "-Book \'%s\' with id = %d is available at library.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print(ENTER_BOOK_TO_CHECK_MESSAGE);
        String title = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));

        List<Book> books = bookRepo.selectBookByTitleAvailable(title);

        if (books == null || books.isEmpty()) {
            writer.println(BOOK_NOT_AVAILABLE_MESSAGE);
        } else {
            for (Book book : books) {
                writer.println(String.format(
                        BOOK_AVAILABLE_MESSAGE,
                        book.getTitle(), book.getId())
                );
            }
        }
        writer.println(ASKING_USERS_MESSAGE);

        List<User> users = userRepo.selectUsers();

        if (users.isEmpty()) {
            writer.println(NO_USERS_REPLY_MESSAGE);
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (User user : users) {
            for (Book book : user.getBooks()) {
                if (book.getTitle().toLowerCase().contains(title)) {
                    builder.append(
                            String.format(
                                    USER_HAS_BOOK_MESSAGE,
                                    user.getUsername(),
                                    book.getTitle()
                            )
                    ).append(System.lineSeparator());
                }
            }
        }
        writer.println((builder.length() > 0)
                ? builder.toString() : String.format(NO_USER_HAS_COPIES_MESSAGE, title));
    }
}
