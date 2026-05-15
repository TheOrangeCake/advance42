package com.nguyen.message.exception;

import com.nguyen.colors.Colors;

public class SocketErrorException extends Exception{
    public SocketErrorException(String message) {
        super(Colors.RED + "Error: " + Colors.RESET + message);
    }
}
