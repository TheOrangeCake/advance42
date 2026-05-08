package com.nguyen.fix;

public class InvalidFixFormatException extends RuntimeException {
    public InvalidFixFormatException(String message) {
        super(Colors.RED + "Error: " + Colors.RESET +  message);
    }
}
