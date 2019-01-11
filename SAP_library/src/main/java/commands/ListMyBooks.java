package commands;

import db.BookRepository;
import db.UserRepository;
import engine.LibraryEngine;
import entities.Book;
import utils.Writer;

import java.io.BufferedReader;

public class ListMyBooks implements Command {

    private static final String NO_USERS_LOGGED_IN_MESSAGE = "-No user logged in.";
    private static final String NO_BOOKS_IN_INVENTORY_MESSAGE = "-No books in inventory.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) {

        if (LibraryEngine.loggedInUser == null) {
            writer.println(NO_USERS_LOGGED_IN_MESSAGE);
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (Book book : LibraryEngine.loggedInUser.getBooks()) {
            builder.append(book.toString()).append(System.lineSeparator());
        }
        writer.println(builder.length() > 1 ? builder.toString() : NO_BOOKS_IN_INVENTORY_MESSAGE);
    }
}
