import db.BookRepository;
import db.NotificationRepo;
import db.UserRepository;
import engine.LibraryEngine;
import entities.Book;
import entities.Notification;
import utils.Notifier;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StartUp {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Writer writer = new Writer(System.out);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sap_library");
        EntityManager userEm = emf.createEntityManager();
        EntityManager bookEm = emf.createEntityManager();
        EntityManager notificationEm = emf.createEntityManager();

        UserRepository userRepository = new UserRepository(userEm);
        BookRepository bookRepository = new BookRepository(bookEm);
        Notifier.setRepository(new NotificationRepo(notificationEm));

        LibraryEngine engine = new LibraryEngine(reader, writer, bookRepository, userRepository );
        engine.run();
    }
}
