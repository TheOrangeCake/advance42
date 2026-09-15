package src.exceptions;

import java.lang.Exception;

public class BadProgrammerException extends Exception {
    public BadProgrammerException() {
        super("What are you doing?!");
    }
}