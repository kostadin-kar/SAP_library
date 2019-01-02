package commands;

import engine.LibraryEngine;
import utils.Writer;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;

public class LogoutUserCommand implements Command {

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        if (LibraryEngine.loggedInUser == null) {
            writer.println("No user is logged in.");
            return;
        }

        writer.println("User " + LibraryEngine.loggedInUser.getUsername() + " logged out.");
        LibraryEngine.loggedInUser = null;
    }
}
