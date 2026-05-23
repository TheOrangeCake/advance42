package com.nguyen.broker;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;
import com.nguyen.broker.model.FixTransaction;
import com.nguyen.broker.model.MessageStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
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

            try (Session session = HibernateSession.getInstance().getSessionFactory().openSession()) {
                List<FixTransaction> pending = session.createQuery(
                                "FROM FixTransaction WHERE status = :status", FixTransaction.class)
                        .setParameter("status", MessageStatus.PENDING)
                        .list();

                if (!pending.isEmpty()) {
                    System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Found pending transaction, resume");
                }
                for (FixTransaction ft : pending) {
                    // TODO: send fix here
                }
            }

            ResponseHandler responseHandler = new ResponseHandler();
            while (true) {
                // get buy or sell
                // get symbol
                // get quantity
                // get price
                // get target uid
                // send fix request

                String message = client.receive();
                if (message == null) {
                    break;
                }
                try {
                    responseHandler.handle(message);
                } catch (RuntimeException e) {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Client server socket error or closed. Exit");
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
            System.err.println(e.getMessage());
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
}
