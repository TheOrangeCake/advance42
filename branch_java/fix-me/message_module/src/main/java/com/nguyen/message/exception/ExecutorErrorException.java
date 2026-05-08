package com.nguyen.message.exception;

import com.nguyen.fix.Colors;

public class ExecutorErrorException extends RuntimeException {
    public ExecutorErrorException(String message) {
        super(Colors.RED + "Error: " + Colors.RESET + message);
    }
}
