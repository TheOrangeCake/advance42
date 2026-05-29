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

    public void sendLogon(String inputUid) throws IOException {
        try {
            String logonMessage = new FixBuilder.Builder()
                    .beginString("FIX.4.4")
                    .messageType("A")
                    .senderId(inputUid)
                    .targetId("000000")
                    .sendingTime(new Date())
                    .build()
                    .getFixMessage();
            out.print(logonMessage);
            out.flush();
        } catch (InvalidFixFormatException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to send UID");
            throw new IOException();
        }
    }

    public void fetchUid(String inputUid) throws IOException {
        String logonMessage = receive();
        if (logonMessage == null) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to retrieve UID");
            throw new IOException();
        }

        try {
            Map<FixTag, String> fixMessage = FixParser.parse(logonMessage);
            String msgType = fixMessage.get(FixTag.MSG_TYPE);
            if (!msgType.equals("A")) {
                if (msgType.equals("3")) {
                    String error = fixMessage.get(FixTag.TEXT);
                    if (error != null) {
                        System.err.println(Colors.RED + "Error: " + Colors.RESET + error);
                    } else {
                        System.err.println(Colors.RED + "Error: " + Colors.RESET + "Error during logon");
                    }
                } else {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Expected Logon (35=A)");
                }
                throw new IOException();
            }
            String newUid = fixMessage.get(FixTag.TARGET_COMP_ID);
            if (!newUid.equals(inputUid) && !inputUid.equals("000000")) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Server did not echo back the requested UID");
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "A new UID is assigned: " + Colors.PURPLE + newUid + Colors.RESET);
            }
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

    public void send(String message) throws IOException {
        out.print(message);
        out.flush();
        if (out.checkError()) {
            throw new IOException("Write to server failed");
        }
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
