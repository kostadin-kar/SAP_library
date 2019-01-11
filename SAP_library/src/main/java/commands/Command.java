package commands;

import db.BookRepository;
import db.UserRepository;
import utils.Writer;

import java.io.BufferedReader;
import java.io.IOException;

public interface Command {

    void execute(UserRepository userRepo,
                 BookRepository bookRepo,
                 BufferedReader reader,
                 Writer writer) throws IOException;
}
