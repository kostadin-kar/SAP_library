package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.User;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class RegisterUserCommand implements Command {
    private static final String ENTER_USERNAME_MESSAGE = "-Enter username: ";
    private static final String ENTER_PASSWORD_MESSAGE = "-Enter password: ";
    private static final String USER_ALREADY_EXISTS_MESSAGE = "-User %s already exists.";
    private static final String SUCCESSFULLY_REGISTERED_MESSAGE = "-Successfully registered user %s";
    private static final String INVALID_USERNAME_LENGTH_MESSAGE = "-Username must be at least 4 characters long.";
    private static final String INVALID_PASSWORD_LENGTH_MESSAGE = "-Password must be at least 4 characters long.";
    private static final String CANNOT_REGISTER_WHEN_LOGGED_IN_MESSAGE = "-User cannot register if they are logged in.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser != null) {
            writer.println(CANNOT_REGISTER_WHEN_LOGGED_IN_MESSAGE);
            return;
        }

        writer.print(ENTER_USERNAME_MESSAGE);
        String username = reader.readLine();
        writer.print(ENTER_PASSWORD_MESSAGE);
        String password = reader.readLine();

        if (username.length() < 4) {
            writer.println(INVALID_USERNAME_LENGTH_MESSAGE);
            return;
        }
        if (password.length() < 4) {
            writer.println(INVALID_PASSWORD_LENGTH_MESSAGE);
            return;
        }

        List<User> users = userRepo.selectUsersByUsername(username);

        if (!users.isEmpty()) {
            writer.println(String.format(USER_ALREADY_EXISTS_MESSAGE, username));
            return;
        }

        User userEntity = new User(username, password);
        if (userRepo.selectUsers().isEmpty()) {
            userEntity.setIsRoleAdmin(true);
        }

        userRepo.persist(userEntity);

        writer.println(String.format(SUCCESSFULLY_REGISTERED_MESSAGE, username));
    }
}
