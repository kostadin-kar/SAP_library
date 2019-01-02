package utils;

import java.io.*;

public class Writer {
    private PrintStream writer;

    public Writer(PrintStream writer) {
        this.writer = writer;
    }

    public void println(String message) throws IOException {
        writer.println(message);
//        writer.write(message + System.lineSeparator());
    }

    public void print(String message) throws IOException {
        writer.print(message);
//        writer.write(message);
    }
}
