package com.nguyen.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClientServer {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String uid;
    // pass handler here

    public TCPClientServer(String ipv4, int port) throws IOException {
        socket = new Socket(ipv4, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println(Colors.YELLOW + "Connected to " + ipv4 + ":" + port + Colors.RESET);
    }

    public void fetchUid() throws IOException {
        while(true) {
            String data = in.readLine();
            if (data == null) {
                throw new IOException();
            }
            if (data.matches("\\d{6}")) {
                out.println("ok");
                uid = data;
                return;
            }
            out.println("Invalid uid");
        }
    }

    public void run() {
        // loop for activity
        // call handler
    }

    public void close() throws IOException {
        socket.close();
        System.out.println(Colors.YELLOW + "Connection closed. Bye." + Colors.RESET);
    }

    public String getUid() {
        return uid;
    }
}
