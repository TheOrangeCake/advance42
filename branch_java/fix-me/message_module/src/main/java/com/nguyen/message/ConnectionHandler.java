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
import com.nguyen.helper.Colors;

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

            sendUid(routingTable);

            while (!socket.isClosed()) {
                StringBuilder originalMessage = new StringBuilder();
                int c;
                while (true) {
                    c = in.read();
                    if (c == -1) {
                        routingTable.removeFromRoutingTable(uid, port);
                        socket.close();
                        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Connection " + uid + " disconnected");
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
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Error IO from socket. Close connection");
            try {
                routingTable.removeFromRoutingTable(uid, port);
                socket.close();
            } catch (IOException ex) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Error closing socket");
            }
        } catch (IllegalArgumentException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET +  "Close connection. Program problem: " + e.getMessage());
        }
    }

    private void sendUid(RoutingTable routingTable) throws IOException {
        int attempt = 1;
        while (true) {
            if (attempt > 5) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Fail to assign uid");
                throw new IOException();
            }

            String newUid = routingTable.generateUid();
            out.println(newUid);

            String status = in.readLine();
            if (status == null) {
                throw new IOException();
            }

            if (status.equals("ok")) {
                uid = newUid;
                routingTable.addToRoutingTable(uid, socket, port);
                return;
            }
            attempt++;
        }
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
