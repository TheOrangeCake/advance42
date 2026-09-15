package com.nguyen.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Map;

import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixParser;
import com.nguyen.fix.FixTag;
import com.nguyen.fix.FixTranslator;
import com.nguyen.fix.InvalidFixFormatException;
import com.nguyen.colors.Colors;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final Integer port;
    private BufferedReader in;
    private PrintWriter out;
    private String uid;

    public ConnectionHandler(Socket socket, Integer port) {
        this.socket = socket;
        this.port = port;
    }

    @Override
    public void run() {
        RoutingTable routingTable = RoutingTable.getInstance();

        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);

            handleLogon(routingTable);

            while (!socket.isClosed()) {
                String originalMessage = receive(routingTable);
                if (originalMessage == null) {
                    return;
                }
                Map<FixTag, String> parsedMessage = null;
                try {
                    parsedMessage = FixParser.parse(originalMessage);
                    String targetId = parsedMessage.get(FixTag.TARGET_COMP_ID);
                    ConnectionHandler targetHandler = routingTable.getHandler(targetId, port);
                    try {
                        forward(originalMessage, targetHandler);
                    } catch (IOException e) {
                        System.out.println(Colors.YELLOW + "Warn: " + Colors.RESET
                                + "Forward to " + targetId + " failed, target unreachable. Sender retry will redeliver.");
                    }
                } catch (InvalidFixFormatException | IllegalArgumentException e) {
                    String orderId = parsedMessage == null ? null : parsedMessage.get(FixTag.ORDER_ID);
                    sendReject(e.getMessage(), orderId);
                }
            }
        } catch (IOException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Error IO from socket. Close connection");
            try {
                if (uid != null) {
                    routingTable.removeFromRoutingTable(uid, port);
                }
                socket.close();
            } catch (IOException ex) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Error closing socket");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET +  "Close connection. Program problem: " + e.getMessage());
        }
    }

    private void handleLogon(RoutingTable routingTable) throws IOException {
        String firstHandShake = receive(routingTable);
        if (firstHandShake == null) {
            throw new IOException();
        }
        try {
            Map<FixTag, String> firstLogonFix = FixParser.parse(firstHandShake);
            String senderUid = firstLogonFix.get(FixTag.SENDER_COMP_ID);
            if (senderUid.equals("000000")) {
                uid = routingTable.generateUid();
                routingTable.addToRoutingTable(uid, this, port);
            } else {
                boolean isActive = routingTable.isActive(senderUid, port);
                if (isActive) {
                    sendReject("UID already logged on", null);
                    throw new IOException();
                }
                uid = senderUid;
                routingTable.bumpUidCounter(uid);
                routingTable.addToRoutingTable(uid, this, port);
            }
            String secondHandShake = new FixBuilder.Builder()
                    .beginString("FIX.4.4")
                    .messageType("A")
                    .senderId("000000")
                    .targetId(uid)
                    .sendingTime(new Date())
                    .build()
                    .getFixMessage();
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Sent Logon " + uid + ", " + secondHandShake);
            System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(secondHandShake) + Colors.RESET);
            send(secondHandShake);
        } catch (InvalidFixFormatException e) {
            sendReject(e.getMessage(), null);
            throw new IOException();
        }

        String thirdHandShake = receive(routingTable);
        if (thirdHandShake == null) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to confirm assign uid");
            throw new IOException();
        }

        try {
            Map<FixTag, String> fixMessage = FixParser.parse(thirdHandShake);
            if (!fixMessage.get(FixTag.MSG_TYPE).equals("A")) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Expected Logon (35=A)");
                throw new IOException();
            }
            if (!fixMessage.get(FixTag.SENDER_COMP_ID).equals(uid)) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to assign uid");
                throw new IOException();
            }
        } catch (InvalidFixFormatException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to assign uid");
            throw new IOException();
        }
    }

    public synchronized void send(String message) throws IOException {
        out.print(message);
        out.flush();
        if (out.checkError()) {
            throw new IOException("Write to client failed");
        }
    }

    private void sendReject(String reason, String orderId) throws IOException {
        FixBuilder.Builder builder = new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("3")
                .senderId("000000")
                .targetId(uid == null ? "000000" : uid)
                .sendingTime(new Date())
                .text(reason);
        if (orderId != null) {
            builder.orderId(orderId);
        }
        String rejectMessage = builder.build().getFixMessage();
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Sending reject: " + rejectMessage);
        System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(rejectMessage) + Colors.RESET);
        send(rejectMessage);
    }

    private void forward(String originalMessage, ConnectionHandler targetHandler) throws IOException {
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Forwarding " + originalMessage);
        System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(originalMessage) + Colors.RESET);
        targetHandler.send(originalMessage);
    }

    private String receive(RoutingTable routingTable) throws IOException {
        StringBuilder originalMessage = new StringBuilder();
        int c;
        while (true) {
            c = in.read();
            if (c == -1) {
                if (uid != null) {
                    routingTable.removeFromRoutingTable(uid, port);
                }
                socket.close();
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Connection " + uid + " disconnected");
                return null;
            }
            originalMessage.append((char) c);
            if (originalMessage.toString().contains(FixParser.SOH + "10=") &&
                    originalMessage.toString().endsWith(String.valueOf(FixParser.SOH))) {
                return originalMessage.toString();
            }
        }
    }
}
