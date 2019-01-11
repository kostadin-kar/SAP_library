package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.User;
import utils.Notifier;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class LoginUserCommand implements Command {

    private static final String LOGGED_IN_AS_MESSAGE = "-Logged in as %s";
    private static final String ENTER_USERNAME_MESSAGE = "-Enter username: ";
    private static final String ENTER_PASSWORD_MESSAGE = "-Enter password: ";
    private static final String INCORRECT_USER_PASS_MESSAGE = "-Incorrect username or password";
    private static final String USER_ALREADY_LOGGED_IN_MESSAGE = "-A user is already logged in.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser != null) {
            writer.println(USER_ALREADY_LOGGED_IN_MESSAGE);
            return;
        }

        writer.print(ENTER_USERNAME_MESSAGE);
        String username = reader.readLine();
        writer.print(ENTER_PASSWORD_MESSAGE);
        String password = reader.readLine();

        List<User> users = userRepo.selectUsersByUsernameAndPassword(username, password);

        if (users == null || users.isEmpty()
                || !(users.get(0).getUsername().equals(username) && users.get(0).getPassword().equals(password))) {
            writer.println(INCORRECT_USER_PASS_MESSAGE);
            return;
        }

        LibraryEngine.loggedInUser = users.get(0);
        writer.println(String.format(LOGGED_IN_AS_MESSAGE, username));

        //Notify user when they log in
        writer.print(Notifier.notify(users.get(0)));
    }
}
