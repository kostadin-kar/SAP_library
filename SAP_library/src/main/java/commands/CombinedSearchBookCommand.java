package commands;

import db.BookRepository;
import db.UserRepository;
import entities.Book;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class CombinedSearchBookCommand implements Command {

    private static final String EMPTY_STRING = "";
    private static final CharSequence DELIMITER = " ";
    private static final String AND_MESSAGE = " and ";
    private static final String BOOK_TITLE_MESSAGE = "--\'%s\';";
    private static final String BOOKS_FROM_MESSAGE = "-Books from %s%s%s:";
    private static final String TITLE_SEARCH_MESSAGE = "-Title to search: ";
    private static final String AUTHOR_SEARCH_MESSAGE = "-Author to search: ";
    private static final String RELEASE_YEAR_SEARCH = "-Release year to search: ";
    private static final String NO_BOOKS_AVAILABLE_MESSAGE = "-No such books available.";
    private static final String BOOKS_IN_LIBRARY_MESSAGE = "-Books available in library:";
    private static final String BOOK_AVAILABLE_MESSAGE = "-Book \'%s\' available at the library.";
    private static final String YEAR_NOT_INCLUDE_MESSAGE = "-Year will not be included in search";
    private static final String ENTER_BOOK_INFO_MESSAGE
            = "-Enter info of book to search on (leave search field empty if you want to exclude it from search):";

    private String title;
    private String author;
    private Integer releaseYear;

    public void execute(UserRepository userRepo,
                        BookRepository bookRepo,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        initializeBookTokens(reader, writer);

        List<Book> booksByTitle = null;
        List<Book> books = null;

        boolean isTitleEmpty = this.title.equals(EMPTY_STRING);
        boolean isAuthorEmpty = this.author.equals(EMPTY_STRING);
        boolean isReleaseDateEmpty = this.releaseYear == -1;
        if (!isTitleEmpty && !isAuthorEmpty && !isReleaseDateEmpty) {
            booksByTitle = bookRepo.selectBookByAllFieldsAvailable(title, author, releaseYear);

        } else if (!isAuthorEmpty && !isReleaseDateEmpty) {
            books = bookRepo.selectBookByAuthorAndYearAvailable(author, releaseYear);

        } else if (!isTitleEmpty && !isAuthorEmpty) {
            booksByTitle = bookRepo.selectBookByTitleAvailable(title);
            books = bookRepo.selectBookByAuthorAvailable(author);

        } else if (!isTitleEmpty && !isReleaseDateEmpty) {
            booksByTitle = bookRepo.selectBookByTitleAvailable(title);
            books = bookRepo.selectBookByDateAvailable(releaseYear);

        } else if (!isReleaseDateEmpty) {
            books = bookRepo.selectBookByDateAvailable(releaseYear);

        } else if (!isAuthorEmpty) {
            books = bookRepo.selectBookByAuthorAvailable(author);

        } else if (!isTitleEmpty) {
            booksByTitle = bookRepo.selectBookByTitleAvailable(title);

        } else {
            books = bookRepo.selectAvailableBooks();

        }

        if ((booksByTitle != null && booksByTitle.isEmpty()) || (books != null && books.isEmpty())) {
            writer.println(NO_BOOKS_AVAILABLE_MESSAGE);
            return;
        }
        if (booksByTitle != null) {
            writer.println(String.format(BOOK_AVAILABLE_MESSAGE, booksByTitle.get(0).getTitle()));
        }
        if (books != null) {
            String authorBooks = (!this.author.equals(EMPTY_STRING)) ? books.get(0).getAuthor() : EMPTY_STRING;
            String releaseDateBooks =
                    (this.releaseYear != -1) ? this.releaseYear.toString() : EMPTY_STRING;

            if (!authorBooks.equals(EMPTY_STRING) || !releaseDateBooks.equals(EMPTY_STRING)) {
                String message = String.format(BOOKS_FROM_MESSAGE,
                        authorBooks,
                        (!authorBooks.equals(EMPTY_STRING) && !releaseDateBooks.equals(EMPTY_STRING)) ? AND_MESSAGE : EMPTY_STRING,
                        releaseDateBooks);
                writer.println(message);
            } else {
                writer.println(BOOKS_IN_LIBRARY_MESSAGE);
            }

            for (Book book : books) {
                if (book.getIsAvailable()) {
                    writer.println(String.format(BOOK_TITLE_MESSAGE, book.getTitle()));
                }
            }
        }

    }

    private void initializeBookTokens(BufferedReader reader, Writer writer) throws IOException {
        writer.println(ENTER_BOOK_INFO_MESSAGE);
        writer.print(TITLE_SEARCH_MESSAGE);
        this.title = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));
        writer.print(AUTHOR_SEARCH_MESSAGE);
        this.author = String.join(DELIMITER, reader.readLine().trim().toLowerCase().split("\\s+"));
        writer.print(RELEASE_YEAR_SEARCH);

        try {
            this.releaseYear = Integer.parseInt(reader.readLine().trim());

        } catch (NumberFormatException nfe) {
            writer.println(YEAR_NOT_INCLUDE_MESSAGE);
            this.releaseYear = -1;
        }
    }
}
