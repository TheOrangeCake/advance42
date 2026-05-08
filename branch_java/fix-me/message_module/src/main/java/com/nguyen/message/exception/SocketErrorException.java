package com.nguyen.message.exception;

import com.nguyen.fix.Colors;

public class SocketErrorException extends Exception{
    public SocketErrorException(String message) {
        super(Colors.RED + "Error: " + Colors.RESET + message);
    }
}
