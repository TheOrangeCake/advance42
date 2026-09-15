package src.exceptions;

import java.lang.Exception;

public class BadFileException extends Exception {
    public BadFileException(String message) {
        super("Scenario File Error: " + message);
    }
}