package com.nguyen.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientServer {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    // pass handler here

    public TCPClientServer(String ipv4, int port) throws IOException {
        socket = new Socket(ipv4, port);
    }

    public void run() {
        // send first message to get uid
        // loop for activity
        // call handler
    }

    public void close() throws IOException {
        socket.close();
        System.out.println(Colors.YELLOW + "Connection closed. Bye." + Colors.RESET);
    }
}
