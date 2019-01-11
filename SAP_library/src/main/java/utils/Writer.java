package utils;

import java.io.*;

public class Writer {
    private PrintStream writer;

    public Writer(PrintStream writer) {
        this.writer = writer;
    }

    public void println(String message) {
        writer.println(message);
    }

    public void print(String message) {
        writer.print(message);
    }
}
