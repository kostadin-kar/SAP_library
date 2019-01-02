package commands;

import entities.Book;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class CombinedSearchBookCommand implements Command {
    private static final String THREE_PARAM_STRING_FORMAT_QUERY
            = "SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :title AND LOWER(b.author) LIKE :author AND YEAR(b.releaseDate) LIKE :releaseYear  AND b.isAvailable = true";

    private String title;
    private String author;
    private Integer releaseYear;

    public void execute(EntityManager entityManager,
                        BufferedReader reader,
                        Writer writer) throws IOException {

        initializeBookTokens(reader, writer);

        List<Book> booksByTitle = null;
        List<Book> books = null;

        boolean isTitleEmpty = this.title.equals("");
        boolean isAuthorEmpty = this.author.equals("");
        boolean isReleaseDateEmpty = this.releaseYear == -1;
        if (!isTitleEmpty && !isAuthorEmpty && !isReleaseDateEmpty) {
            Query query = entityManager
                    .createQuery(THREE_PARAM_STRING_FORMAT_QUERY, Book.class);
            query.setParameter("title", "%" + title + "%");
            query.setParameter("author", "%" + author + "%");
            query.setParameter("releaseYear", releaseYear);
            booksByTitle = query.getResultList();
        } else if (!isAuthorEmpty && !isReleaseDateEmpty) {
            Query query = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.author) LIKE :param1_1 AND YEAR(b.releaseDate) LIKE :param2_2  AND b.isAvailable = true", Book.class);
            query.setParameter("param1_1", "%" + author + "%");
            query.setParameter("param2_2", releaseYear);
            books = query.getResultList();
        } else if (!isTitleEmpty && !isAuthorEmpty) {
            Query titleQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :param_0 AND b.isAvailable = true", Book.class);
            titleQuery.setParameter("param_0", "%" + title + "%");
            Query authorQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.author) LIKE :param_0 AND b.isAvailable = true", Book.class);
            authorQuery.setParameter("param_0", "%" + author + "%");
            booksByTitle = titleQuery.getResultList();
            books = authorQuery.getResultList();
        } else if (!isTitleEmpty && !isReleaseDateEmpty) {
            Query titleQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :param_0 AND b.isAvailable = true", Book.class);
            titleQuery.setParameter("param_0", "%" + title + "%");
            Query releaseDateQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE YEAR(b.releaseDate) LIKE :param_0 AND b.isAvailable = true", Book.class);
            releaseDateQuery.setParameter("param_0", releaseYear);
            booksByTitle = titleQuery.getResultList();
            books = releaseDateQuery.getResultList();
        } else if (!isReleaseDateEmpty) {
            Query releaseDateQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE YEAR(b.releaseDate) LIKE :param_0 AND b.isAvailable = true", Book.class);
            releaseDateQuery.setParameter("param_0", releaseYear);
            books = releaseDateQuery.getResultList();
        } else if (!isAuthorEmpty) {
            Query authorQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.author) LIKE :param_0 AND b.isAvailable = true", Book.class);
            authorQuery.setParameter("param_0", "%" + author + "%");
            books = authorQuery.getResultList();
        } else if (!isTitleEmpty) {
            Query titleQuery = entityManager
                    .createQuery("SELECT b FROM Book AS b WHERE LOWER(b.title) LIKE :param_0 AND b.isAvailable = true", Book.class);
            titleQuery.setParameter("param_0", "%" + title + "%");
            booksByTitle = titleQuery.getResultList();
        } else {
            Query query = entityManager.createQuery("SELECT b FROM Book AS b WHERE b.isAvailable = true ORDER BY b.title");
            books = query.getResultList();
        }

        if ((booksByTitle != null && booksByTitle.isEmpty()) || (books != null && books.isEmpty())) {
            writer.println("---No such books available.");
            return;
        }
        if (booksByTitle != null) {
            writer.println("---Book \'" + booksByTitle.get(0).getTitle() + "\' available at the library.");
        }
        if (books != null) {
            String authorBooks = (!this.author.equals("")) ? books.get(0).getAuthor() : "";
            String releaseDateBooks =
                    (this.releaseYear != -1) ? this.releaseYear.toString() : "";

            if (!authorBooks.equals("") || !releaseDateBooks.equals("")) {
                String message = String.format("---Books from %s%s%s:",
                        authorBooks,
                        (!authorBooks.equals("") && !releaseDateBooks.equals("")) ? " and " : "",
                        releaseDateBooks);
                writer.println(message);
            } else {
                writer.println("---Books in library:");
            }

            for (Book book : books) {
                if (book.getIsAvailable()) {
                    writer.println("--\'" + book.getTitle() + "\';");
                }
            }
        }

    }

    private void initializeBookTokens(BufferedReader reader, Writer writer) throws IOException {
        writer.println("--Enter info of book to search on (leave search field empty if you want to exclude it from search):");
        writer.print("----Title to search: ");
        this.title = String.join(" ", reader.readLine().trim().toLowerCase().split("\\s+"));
        writer.print("----Author to search: ");
        this.author = String.join(" ", reader.readLine().trim().toLowerCase().split("\\s+"));
        writer.print("----Release year to search: ");

        try {
            this.releaseYear = Integer.parseInt(reader.readLine().trim());

        } catch (NumberFormatException nfe) {
            writer.println("Year will not be included in search");
            this.releaseYear = -1;
        }
    }
}
