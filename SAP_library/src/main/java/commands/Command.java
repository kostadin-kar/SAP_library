package commands;

import utils.Writer;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;

public interface Command {

    void execute(EntityManager entityManager, BufferedReader reader, Writer writer) throws IOException;
}
