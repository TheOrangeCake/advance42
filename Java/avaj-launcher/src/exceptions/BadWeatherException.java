package src.exceptions;

import java.lang.Exception;

public class BadWeatherException extends Exception {
    public BadWeatherException() {
        super("Invalid weather");
    }
}