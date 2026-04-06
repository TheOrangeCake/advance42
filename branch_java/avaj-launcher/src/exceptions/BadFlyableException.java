package src.exceptions;

import java.lang.Exception;

public class BadFlyableException extends Exception {
    public BadFlyableException(String message) {
        super(message);
    }
}