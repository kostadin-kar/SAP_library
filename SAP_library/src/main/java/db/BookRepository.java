package db;

import entities.Book;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class BookRepository {

    private static final String SELECT_BOOKS
            = "SELECT b FROM Book AS b";

    private static final String IS_AVAILABLE
            = " b.isAvailable = true";

    private static final String SELECT_BOOK_BY_ID
            = SELECT_BOOKS + " WHERE b.id = ?1";

    private static final String SELECT_BOOK_BY_TITLE
            = SELECT_BOOKS + " WHERE LOWER(b.title) = ?1";

    private static final String SELECT_BOOK_BY_YEAR_AVAILABILITY
            = SELECT_BOOKS + " WHERE YEAR(b.releaseDate) LIKE ?1 AND " + IS_AVAILABLE;

    private static final String SELECT_BOOK_BY_TITLE_AVAILABILITY
            = SELECT_BOOKS + " WHERE LOWER(b.title) LIKE ?1 AND " + IS_AVAILABLE;

    private static final String SELECT_BOOK_BY_AUTHOR_AVAILABILITY
            = SELECT_BOOKS + " WHERE LOWER(b.author) LIKE ?1 AND " + IS_AVAILABLE;

    private static final String SELECT_BOOK_BY_AUTHOR_YEAR_AVAILABILITY
            = SELECT_BOOKS + " WHERE LOWER(b.author) LIKE ?1 AND YEAR(b.releaseDate) LIKE ?2  AND " + IS_AVAILABLE;

    private static final String SELECT_AVAILABLE_BOOKS_ORDERED_BY_TITLE
            = SELECT_BOOKS + " WHERE " + IS_AVAILABLE + " ORDER BY b.title";

    private static final String SELECT_BOOK_BY_TITLE_AUTHOR_YEAR_AVAILABILITY
            = SELECT_BOOKS + " WHERE LOWER(b.title) LIKE ?1 AND LOWER(b.author) LIKE ?2 AND YEAR(b.releaseDate) LIKE ?3 AND " + IS_AVAILABLE;


    private EntityManager manager;

    public BookRepository(EntityManager bookEm) {
        this.manager = bookEm;
    }

    public List<Book> selectBooks() {
        Query query = manager
                .createQuery(SELECT_BOOKS);
        return query.getResultList();
    }

    public List<Book> selectBookById(int id) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_ID);
        query.setParameter(1, id);
        return query.getResultList();
    }

    public List<Book> selectBookByTitle(String title) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_TITLE);
        query.setParameter(1, title);
        return query.getResultList();
    }

    public List<Book> selectBookByTitleAvailable(String title) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_TITLE_AVAILABILITY);
        query.setParameter(1, "%" + title + "%");
        return query.getResultList();
    }

    public List<Book> selectBookByAuthorAvailable(String author) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_AUTHOR_AVAILABILITY);
        query.setParameter(1,"%" + author + "%");
        return query.getResultList();
    }

    public List<Book> selectBookByDateAvailable(Integer date) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_YEAR_AVAILABILITY);
        query.setParameter(1, date);
        return query.getResultList();
    }

    public List<Book> selectBookByAuthorAndYearAvailable(String author, Integer date) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_AUTHOR_YEAR_AVAILABILITY);
        query.setParameter(1, "%" + author + "%");
        query.setParameter(2, date);
        return query.getResultList();
    }

    public List<Book> selectAvailableBooks() {
        Query query = manager
                .createQuery(SELECT_AVAILABLE_BOOKS_ORDERED_BY_TITLE);
        return query.getResultList();
    }

    public List<Book> selectBookByAllFieldsAvailable(String title, String author, Integer date) {
        Query query = manager
                .createQuery(SELECT_BOOK_BY_TITLE_AUTHOR_YEAR_AVAILABILITY);
        query.setParameter(1, "%" + title + "%");
        query.setParameter(2, "%" + author + "%");
        query.setParameter(3, date);
        return query.getResultList();
    }

    public void persist(Book book) {
        manager.getTransaction().begin();
        manager.persist(book);
        manager.getTransaction().commit();
    }

    public void merge(Book book) {
        manager.getTransaction().begin();
        manager.merge(book);
        manager.getTransaction().commit();
    }

    public void remove(Book book) {
        manager.getTransaction().begin();
        manager.remove(book);
        manager.getTransaction().commit();
    }

    public void removeList(List<Book> books) {
        manager.getTransaction().begin();
        for (Book book : books) {
            manager.remove(book);
        }
        manager.getTransaction().commit();
    }
}
