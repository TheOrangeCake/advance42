package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixParser;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;
import com.nguyen.market.model.FixTransaction;
import com.nguyen.market.model.Instrument;
import com.nguyen.market.model.MessageStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Market {
    static void main() {
        System.out.println(Colors.YELLOW + "Hello, this is Market" + Colors.RESET);
        SessionFactory sf;
        try {
            sf = HibernateSession.init("market.cfg.xml").getSessionFactory();
            if (DbSeeding.isFirstRun(sf)) {
                DbSeeding.seedMockData(sf);
            }
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error. Exit");
            System.err.println(e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        InputReader inputReader = new InputReader(scanner);
        ConnectionHandler handler = new ConnectionHandler();

        try {
            while (true) {
                String ipv4 = inputReader.readIP();
                if (ipv4 == null) {
                    System.err.println(Colors.RED + "Fail to get Server IPv4 address. Exit" + Colors.RESET);
                    break;
                }
                int port = inputReader.readPort();
                if (port == -1) {
                    System.err.println(Colors.RED + "Fail to get Port. Exit" + Colors.RESET);
                    break;
                }
                String uid = inputReader.readUid("market");
                if (uid == null) {
                    System.err.println(Colors.RED + "Fail to get UID. Exit" + Colors.RESET);
                    break;
                }

                TCPClientServer client = null;
                try {
                    client = new TCPClientServer(ipv4, port);
                    client.sendLogon(uid);
                    client.fetchUid(uid);
                    System.out.println(Colors.YELLOW + "Info: " + Colors.RESET +
                            "This market UID is " + client.getUid());

                    try (Session session = sf.openSession()) {
                        List<Instrument> instruments =
                                session.createQuery("FROM Instrument", Instrument.class).list();
                        Instrument.printAll(instruments);
                    }

                    retry(client, handler);
                    mainLoop(client, handler);
                    break;
                } catch (IOException e) {
                    System.err.println(Colors.RED + "Connection lost: " + Colors.RESET + e.getMessage());
                    System.out.println(Colors.YELLOW + "Will prompt for reconnect..." + Colors.RESET);
                } catch (HibernateException e) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
                    System.err.println(e.getMessage());
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

    private static void retry(TCPClientServer client, ConnectionHandler handler) throws IOException {
        try (Session session = HibernateSession.getInstance().getSessionFactory().openSession()) {
            List<FixTransaction> pending = session.createQuery(
                            "FROM FixTransaction WHERE status = :status AND marketId = :uid",
                            FixTransaction.class)
                    .setParameter("status", MessageStatus.PENDING)
                    .setParameter("uid", client.getUid())
                    .list();

            if (!pending.isEmpty()) {
                System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Found pending transaction, resume");
            }
            for (FixTransaction ft : pending) {
                try {
                    String responseMessage = handler.handle(ft.getFixRequestMessage(), client.getUid());
                    if (responseMessage != null) {
                        client.send(responseMessage);
                    }
                } catch (HibernateException e) {
                    String targetId = FixParser.extractRawTargetId(ft.getFixRequestMessage());
                    client.send(new FixBuilder.Builder()
                            .beginString("FIX.4.4")
                            .messageType("3")
                            .senderId(client.getUid())
                            .targetId(targetId)
                            .sendingTime(new Date())
                            .text(e.getMessage())
                            .build()
                            .getFixMessage());
                }
            }
        }
    }

    private static void mainLoop(TCPClientServer client, ConnectionHandler handler) throws IOException {
        while (true) {
            String message = client.receive();
            if (message == null) {
                throw new IOException("Server disconnected");
            }
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Request received: " + message);
            try {
                String responseMessage = handler.handle(message, client.getUid());
                if (responseMessage != null) {
                    client.send(responseMessage);
                }
            } catch (HibernateException e) {
                String targetId = FixParser.extractRawTargetId(message);
                client.send(new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("3")
                        .senderId(client.getUid())
                        .targetId(targetId)
                        .sendingTime(new Date())
                        .text(e.getMessage())
                        .build()
                        .getFixMessage());
                break;
            }
        }
    }
}