package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import utils.Writer;

import java.io.BufferedReader;

public class LogoutUserCommand implements Command {

    private static final String USER_LOGGED_OUT_MESSAGE = "-User %s logged out.";
    private static final String NO_USER_LOGGED_IN_MESSAGE = "-No user is logged in.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(NO_USER_LOGGED_IN_MESSAGE);
            return;
        }

        writer.println(String.format(USER_LOGGED_OUT_MESSAGE, LibraryEngine.loggedInUser.getUsername()));
        LibraryEngine.loggedInUser = null;
    }
}
