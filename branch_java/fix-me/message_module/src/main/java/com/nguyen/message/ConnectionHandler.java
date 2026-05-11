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
import com.nguyen.fix.InvalidFixFormatException;

public class ConnectionHandler implements Runnable {
    private final Socket socket;
    private final Integer port;
    private String uid;

    public ConnectionHandler(Socket socket, Integer port) {
        this.socket = socket;
        this.port = port;
    }

    @Override
    public void run() {
        RoutingTable routingTable = RoutingTable.getInstance();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            uid = routingTable.generateUid();
            routingTable.addToRoutingTable(uid, socket, port);
            sendUid(out);

            while (!socket.isClosed()) {
                StringBuilder originalMessage = new StringBuilder();
                int c;
                while (true) {
                    c = in.read();
                    if (c == -1) {
                        routingTable.removeFromRoutingTable(uid, port);
                        socket.close();
                        return;
                    }
                    originalMessage.append((char) c);
                    if (originalMessage.toString().contains(FixParser.SOH + "10=") &&
                            originalMessage.toString().endsWith(String.valueOf(FixParser.SOH))) {
                        break;
                    }
                }

                try {
                    Map<FixTag, String> parsedMessage = FixParser.parse(originalMessage.toString());
                    String targetId = parsedMessage.get(FixTag.TARGET_COMP_ID);
                    Socket targetSocket = routingTable.getSocket(targetId, port);
                    forward(originalMessage.toString(), targetSocket);
                } catch (InvalidFixFormatException | IllegalArgumentException e) {
                    sendReject(e.getMessage(), out);
                }
            }
        } catch (IOException e) {
            System.err.println("Error IO from socket. Close connection");
            try {
                routingTable.removeFromRoutingTable(uid, port);
                socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing socket");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Close connection. Program problem: " + e.getMessage());
        }
    }

    private void sendUid(PrintWriter out) throws IOException {
        out.println(uid);
    }

    private void sendReject(String reason, PrintWriter out) throws IOException {
        String rejectMessage = new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("3")
                .senderId("000000")
                .targetId(uid)
                .sendingTime(new Date())
                .symbol("N/A")
                .orderQuantity(1)
                .price(1)
                .text(reason)
                .build()
                .getFixMessage();
        out.print(rejectMessage);
    }

    private void forward(String originalMessage, Socket targetSocket) throws IOException {
        PrintWriter out = new PrintWriter(targetSocket.getOutputStream(), true);
        out.print(originalMessage);
    }
}
