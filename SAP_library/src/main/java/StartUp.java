import engine.LibraryEngine;
import utils.Writer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class StartUp {
    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Writer writer = new Writer(System.out);

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("sap_library");
        EntityManager em = emf.createEntityManager();

        LibraryEngine engine = new LibraryEngine(reader, writer, em);
        engine.run();
    }
}
