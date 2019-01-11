package engine;

import commands.*;
import db.BookRepository;
import db.UserRepository;
import entities.User;
import enums.Commands;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;

public class LibraryEngine implements Runnable {

    public static User loggedInUser = null;

    private final BufferedReader reader;
    private final Writer writer;
    private final BookRepository bookRepo;
    private final UserRepository userRepo;

    public LibraryEngine(BufferedReader reader, Writer writer, BookRepository bookRepo, UserRepository userRepo) {
        this.reader = reader;
        this.writer = writer;
        this.bookRepo = bookRepo;
        this.userRepo = userRepo;
    }

    public void run() {
        printHelpMenu();
        String commandLineArgs;
        Command command = null;
        while (true) {
            try {
                writer.print("-Enter command: ");
                commandLineArgs = this.reader.readLine();

                if (commandLineArgs.toUpperCase().equals("EXIT")) {
                    writer.println("Exiting program.");
                    break;
                }

                String[] commandLines = commandLineArgs.trim()
                        .replaceAll("\\s+", " ")
                        .split("\\s+");
                if (commandLines.length < 2) {
                    writer.println("Invalid command.");
                    continue;
                }
                String commandLine = commandLines[0].toUpperCase() + "_" + commandLines[1].toUpperCase();

                boolean isValidCommand = false;
                for (Commands commands : Commands.values()) {
                    if (commands.name().equals(commandLine)) {
                        isValidCommand = true;
                        break;
                    }
                }
                if (!isValidCommand) {
                    writer.println("Invalid command.");
                    continue;
                }

                switch (Commands.valueOf(commandLine)) {
                    case REGISTER_USER:
                        command = new RegisterUserCommand();
                        break;
                    case LOG_IN:
                        command = new LoginUserCommand();
                        break;
                    case LOG_OUT:
                        command = new LogoutUserCommand();
                        break;
                    case ADD_BOOK:
                        command = new AddBookToLibraryCommand();
                        break;
                    case REMOVE_BOOK:
                        command = new RemoveBookFromLibraryCommand();
                        break;
                    case GET_BOOK:
                        command = new UserGetBookCommand();
                        break;
                    case RETURN_BOOK:
                        command = new UserReturnBookCommand();
                        break;
                    case SEARCH_TITLE:
                        command = new SearchBookByTitleCommand();
                        break;
                    case COMBINED_SEARCH:
                        command = new CombinedSearchBookCommand();
                        break;
                    case CHECK_BOOK:
                        command = new CheckBookAvailabilityCommand();
                        break;
                    case LIST_USERS:
                        command = new ListUsersNotReturnedBooksCommand();
                        break;
                    case ALERT_USER:
                        command = new AlertUserDeadlineCommand();
                        break;
                    case MY_BOOKS:
                        command = new ListMyBooks();
                        break;
                    case HELP_MENU:
                        printHelpMenu();
                        continue;
                    default:
                        writer.println("Invalid command.");
                        continue;
                }

                if (command != null) {
                    command.execute(this.userRepo, this.bookRepo, this.reader, this.writer);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void printHelpMenu() {
        StringBuilder builder = new StringBuilder();
        builder.append("+------------------+").append(System.lineSeparator())
                .append("|   Help manual    |").append(System.lineSeparator())
                .append("| Type a command   |").append(System.lineSeparator())
                .append("| and follow the   |").append(System.lineSeparator())
                .append("| instructions     |").append(System.lineSeparator())
                .append("+------------------+").append(System.lineSeparator())
                .append("| register user;   |").append(System.lineSeparator())
                .append("| log in;          |").append(System.lineSeparator())
                .append("| log out;         |").append(System.lineSeparator())
                .append("| add book;        |").append(System.lineSeparator())
                .append("| remove book;     |").append(System.lineSeparator())
                .append("| get book;        |").append(System.lineSeparator())
                .append("| return book;     |").append(System.lineSeparator())
                .append("| search title;    |").append(System.lineSeparator())
                .append("| combined search; |").append(System.lineSeparator())
                .append("| check book;      |").append(System.lineSeparator())
                .append("| list users;      |").append(System.lineSeparator())
                .append("| alert user;      |").append(System.lineSeparator())
                .append("| my books;        |").append(System.lineSeparator())
                .append("| help menu        |").append(System.lineSeparator())
                .append("+------------------+").append(System.lineSeparator());

        this.writer.println(builder.toString());
    }
}
