package com.nguyen.broker;

import com.nguyen.broker.model.Portfolio;
import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixTranslator;
import com.nguyen.fix.InvalidFixFormatException;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;
import com.nguyen.broker.model.FixTransaction;
import com.nguyen.broker.model.MessageStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Broker {
    static void main() {
        System.out.println(Colors.YELLOW + "Hello, this is Broker" + Colors.RESET);
        try {
            HibernateSession.init("broker.cfg.xml");
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
            System.err.println(e.getMessage());
            return;
        }
        Scanner scanner = new Scanner(System.in);
        InputReader inputReader = new InputReader(scanner);
        Portfolio portfolio = new Portfolio();
        ResponseHandler responseHandler = new ResponseHandler(portfolio);

        try {
            while (true) {
                String ipv4 = inputReader.readIP();
                if (ipv4 == null) {
                    System.err.println(Colors.RED + "Fail to get Server IPv4 address. Exit" + Colors.RESET);
                    break;
                }
                int port = 5000;
                String uid = inputReader.readUid("broker");
                if (uid == null) {
                    System.err.println(Colors.RED + "Fail to get UID. Exit" + Colors.RESET);
                    break;
                }

                TCPClientServer client = null;
                try {
                    client = new TCPClientServer(ipv4, port);
                    client.sendLogon(uid);
                    client.fetchUid(uid);
                    System.out.println(Colors.YELLOW + "Info: " + Colors.RESET
                            + "This broker UID is " + client.getUid());

                    retry(client, responseHandler);
                    if (client.hasData()) {
                        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Receiving Market retry response");
                        while (client.hasData()) {
                            receiveResponse(client, responseHandler);
                        }
                    }

                    mainLoop(inputReader, client, responseHandler);
                    break;
                } catch (IOException e) {
                    System.err.println(Colors.RED + "Connection lost: " + Colors.RESET + e.getMessage());
                    System.out.println(Colors.YELLOW + "Will prompt for reconnect..." + Colors.RESET);
                } catch (InvalidFixFormatException e) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
                    break;
                } finally {
                    if (client != null) {
                        try { client.close(); } catch (IOException ex) {
                            System.err.println(Colors.RED + "Fail to close client socket" + Colors.RESET);
                        }
                    }
                }
            }
        } finally {
            HibernateSession.getInstance().stop();
        }
    }

    private static void retry(TCPClientServer client, ResponseHandler responseHandler) throws IOException {
        try (Session session = HibernateSession.getInstance().getSessionFactory().openSession()) {
            List<FixTransaction> pending = session.createQuery(
                            "FROM FixTransaction WHERE status = :status AND brokerId = :uid",
                            FixTransaction.class)
                    .setParameter("status", MessageStatus.PENDING)
                    .setParameter("uid", client.getUid())
                    .list();

            if (!pending.isEmpty()) {
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Found pending transaction, resume");
            }
            for (FixTransaction ft : pending) {
                String requestMessage = ft.getFixRequestMessage();
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Resending: " + requestMessage);
                System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(requestMessage) + Colors.RESET);
                client.send(requestMessage);
                String response = client.receive();
                if (response == null) {
                    throw new IOException("Server disconnected");
                }
                try {
                    responseHandler.handle(response);
                } catch (RuntimeException e) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
                }
            }
        }
    }

    private static void mainLoop(
            InputReader inputReader,
            TCPClientServer client,
            ResponseHandler responseHandler
    ) throws IOException {
        while (true) {
            String side = inputReader.readSide();
            if (side == null) {
                break;
            }
            String symbol = inputReader.readSymbol();
            if (symbol == null) {
                break;
            }
            String quantity = inputReader.readQuantity();
            if (quantity == null) {
                break;
            }
            String price = inputReader.readPrice();
            if (price == null) {
                break;
            }
            String targetUid = inputReader.readTargetUid();
            if (targetUid == null) {
                break;
            }

            FixTransaction ft = new FixTransaction();
            ft.setBrokerId(client.getUid());
            try (Session session = HibernateSession.getInstance().getSessionFactory().openSession()) {
                Transaction tx1 = session.beginTransaction();
                session.persist(ft);
                tx1.commit();

                Long ftId = ft.getId();
                String requestMessage =  new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("D")
                        .senderId(client.getUid())
                        .targetId(targetUid)
                        .orderId(ftId)
                        .sendingTime(new Date())
                        .symbol(symbol)
                        .orderQuantity(quantity)
                        .price(price)
                        .side(side)
                        .build()
                        .getFixMessage();
                Transaction tx2 = session.beginTransaction();
                ft.setFixRequestMessage(requestMessage);
                session.merge(ft);
                tx2.commit();

                if (client.hasData()) {
                    System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Receiving Market retry response");
                    while (client.hasData()) {
                        receiveResponse(client, responseHandler);
                    }
                }
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Sending: " + requestMessage);
                System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(requestMessage) + Colors.RESET);
                client.send(requestMessage);
            }

            receiveResponse(client, responseHandler);

            if (client.hasData()) {
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Receiving Market retry response");
                while (client.hasData()) {
                    receiveResponse(client, responseHandler);
                }
            }
        }
    }

    private static void receiveResponse(TCPClientServer client, ResponseHandler responseHandler) throws IOException {
        String message = client.receive();
        if (message == null) {
            throw new IOException("Server disconnected");
        }
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Response received: " + message);
        System.out.println(Colors.CYAN + "   -> " + FixTranslator.translate(message) + Colors.RESET);
        try {
            responseHandler.handle(message);
        } catch (RuntimeException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
        }
    }
}
