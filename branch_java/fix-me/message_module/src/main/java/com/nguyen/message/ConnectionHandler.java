package com.nguyen.message;

import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final Integer port;

    public ConnectionHandler(Socket socket, Integer port) {
        this.socket = socket;
        this.port = port;
    }

    @Override
    public void run() {

    }
}
