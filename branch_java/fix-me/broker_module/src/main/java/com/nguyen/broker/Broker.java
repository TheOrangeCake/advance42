package com.nguyen.broker;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.fix.FixBuilder;
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
        String ipv4 = inputReader.readIP();
        if (ipv4 == null) {
            System.err.println(Colors.RED + "Fail to get Server IPv4 address. Exit" + Colors.RESET);
            return;
        }
        int port = inputReader.readPort();
        if (port == -1) {
            System.err.println(Colors.RED + "Fail to get Port. Exit" + Colors.RESET);
            return;
        }

        TCPClientServer client = null;
        try {
            client = new TCPClientServer(ipv4, port);
            client.fetchUid();
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "This broker UID is " + client.getUid());

            retry(client);

            mainLoop(inputReader, client);

        } catch (IOException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Client server socket error or closed. Exit");
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
            System.err.println(e.getMessage());
        } catch (InvalidFixFormatException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
                HibernateSession.getInstance().stop();
            } catch (IOException e) {
                System.err.println(Colors.RED + "Fail to close client socket" + Colors.RESET);
            }
        }
    }

    private static void retry(TCPClientServer client) {
        try (Session session = HibernateSession.getInstance().getSessionFactory().openSession()) {
            List<FixTransaction> pending = session.createQuery(
                            "FROM FixTransaction WHERE status = :status", FixTransaction.class)
                    .setParameter("status", MessageStatus.PENDING)
                    .list();

            if (!pending.isEmpty()) {
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Found pending transaction, resume");
            }
            for (FixTransaction ft : pending) {
                String requestMessage = ft.getFixRequestMessage();
                client.send(requestMessage);
            }
        }
    }

    private static void mainLoop(InputReader inputReader, TCPClientServer client) throws IOException {
        ResponseHandler responseHandler = new ResponseHandler();
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

                client.send(requestMessage);
            }

            String message = client.receive();
            if (message == null) {
                break;
            }
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Response received: " + message);
            try {
                responseHandler.handle(message);
            } catch (RuntimeException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
            }
        }
    }
}
