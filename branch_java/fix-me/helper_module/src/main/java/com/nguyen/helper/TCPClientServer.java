package com.nguyen.helper;

import com.nguyen.colors.Colors;
import com.nguyen.fix.FixParser;
import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

public class TCPClientServer {
    public static final char SOH = '\u0001';
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String uid;

    public TCPClientServer(String ipv4, int port) throws IOException {
        socket = new Socket(ipv4, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println(Colors.YELLOW + "Connected to " + ipv4 + ":" + port + Colors.RESET);
    }

    public void fetchUid() throws IOException {
        String logonMessage = receive();
        if (logonMessage == null) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to retrieve UID");
            throw new IOException();
        }

        try {
            Map<FixTag, String> fixMessage = FixParser.parse(logonMessage);
            if (!fixMessage.get(FixTag.MSG_TYPE).equals("A")) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Expected Logon (35=A)");
                throw new IOException();
            }
            String newUid = fixMessage.get(FixTag.TARGET_COMP_ID);
            String logonConfirm = new FixBuilder.Builder()
                    .beginString("FIX.4.4")
                    .messageType("A")
                    .senderId(newUid)
                    .targetId("000000")
                    .sendingTime(new Date())
                    .build()
                    .getFixMessage();
            out.print(logonConfirm);
            out.flush();
            uid = newUid;
        } catch (InvalidFixFormatException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to retrieve UID");
            throw new IOException();
        }
    }

    public void send(String message) {
        out.print(message);
        out.flush();
    }

    public void close() throws IOException {
        if (!socket.isClosed()) {
            socket.close();
            System.out.println(Colors.YELLOW + "Connection closed. Bye." + Colors.RESET);
        }
    }

    public String getUid() {
        return uid;
    }

    public String receive() throws IOException {
        StringBuilder originalMessage = new StringBuilder();
        int c;
        while (true) {
            c = in.read();
            if (c == -1) {
                socket.close();
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Connection to server disconnected");
                return null;
            }
            originalMessage.append((char) c);
            if (originalMessage.toString().contains(SOH + "10=") &&
                    originalMessage.toString().endsWith(String.valueOf(SOH))) {
                return originalMessage.toString();
            }
        }
    }
}
