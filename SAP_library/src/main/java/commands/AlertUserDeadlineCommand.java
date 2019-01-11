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

public class AlertUserDeadlineCommand implements Command {

    private static final String ADMIN_NOT_LOGGED_MESSAGE = "-Admin not logged in.";
    private static final String USER_NOT_EXIST_MESSAGE = "-User \'%s\' does not exist.";
    private static final String ENTER_USERNAME_TO_NOTIFY = "-Enter username to notify: ";
    private static final String ADMIN_CAN_NOTIFY_MESSAGE = "-Only admin can notify users for return deadlines.";
    private static final String USER_WILL_BE_NOTIFIED_MESSAGE = "-User \'%s\' will be notified when they log in.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(ADMIN_NOT_LOGGED_MESSAGE);
            return;
        }
        if (!LibraryEngine.loggedInUser.getIsRoleAdmin()) {
            writer.println(ADMIN_CAN_NOTIFY_MESSAGE);
            return;
        }

        writer.print(ENTER_USERNAME_TO_NOTIFY);
        String username = reader.readLine();

        List<User> users = userRepo.selectUsersByUsername(username);

        if (users.isEmpty()) {
            writer.println(String.format(USER_NOT_EXIST_MESSAGE, username));
            return;
        }

        Notifier.addUserNotification(users.get(0));
        writer.println(String.format(USER_WILL_BE_NOTIFIED_MESSAGE, users.get(0).getUsername()));
    }
}
