package commands;

import db.BookRepository;
import db.UserRepository;
import entities.Book;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class SearchBookByTitleCommand implements Command {

    private static final String DELIMITER = " ";
    private static final String SEARCH_FOR_TITLE_MESSAGE = "--Enter title to search: ";
    private static final String NO_BOOK_WITH_SUCH_TITLE_MESSAGE = "-No book in library contains such title.";
    private static final String BOOK_WITH_ID_AND_TITLE_AVAILABLE_MESSAGE
            = "-Book \'%s\' with id = %d is available at library.";

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        writer.print(SEARCH_FOR_TITLE_MESSAGE);
        String[] titleTokens = reader.readLine().trim().toLowerCase().split("\\s+");
        String title = String.join(DELIMITER, titleTokens).trim();

        List<Book> books = bookRepo.selectBookByTitleAvailable(title);

        if (books.isEmpty()) {
            writer.println(NO_BOOK_WITH_SUCH_TITLE_MESSAGE);
        } else {

            StringBuilder builder = new StringBuilder();
            for (Book book : books) {
                builder.append(String.format(BOOK_WITH_ID_AND_TITLE_AVAILABLE_MESSAGE, book.getTitle(), book.getId()))
                        .append(System.lineSeparator());
            }
            writer.print(builder.toString());
        }

    }
}
