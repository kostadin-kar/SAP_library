package commands;

import db.BookRepository;
import db.UserRepository;
import entities.User;
import utils.Writer;

import java.io.BufferedReader;
import java.util.List;

public class ListUsersNotReturnedBooksCommand implements Command {

    private static final String LISTING_USERS_MESSAGE = "-Listing users:";
    private static final String NOT_ALL_BOOKS_RETURNED_MESSAGE = "-User \'%s\' has not returned all books.";
    private static final String ALL_BOOKS_ARE_RETURNED_MESSAGE = "-All books are returned and are available.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) {

        List<User> users = userRepo.selectUsers();

        writer.println(LISTING_USERS_MESSAGE);
        StringBuilder builder = new StringBuilder();
        for (User user : users) {
            if (user.getBooks().size() > 0) {
                builder.append(String.format(NOT_ALL_BOOKS_RETURNED_MESSAGE, user.getUsername()))
                        .append(System.lineSeparator());
            }
        }
        writer.print((builder.length() > 0) ? builder.toString() : ALL_BOOKS_ARE_RETURNED_MESSAGE + System.lineSeparator());
    }
}
