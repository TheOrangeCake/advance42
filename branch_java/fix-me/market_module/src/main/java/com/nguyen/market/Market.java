package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;

import java.io.IOException;
import java.util.Scanner;

public class Market {
    static void main() {
        System.out.println(Colors.YELLOW + "Hello, this is Market" + Colors.RESET);
        SessionFactory sf;
        try {
            sf = HibernateSession.getInstance().getSessionFactory();
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
        String ipv4 = inputReader.readIP();
        if (ipv4 == null) {
            System.err.println(Colors.RED + "Fail to get Server IPv4 address. Exit" + Colors.RESET);
            HibernateSession.getInstance().stop();
            return;
        }
        int port = inputReader.readPort();
        if (port == -1) {
            System.err.println(Colors.RED + "Fail to get Port. Exit" + Colors.RESET);
            HibernateSession.getInstance().stop();
            return;
        }

        TCPClientServer client = null;
        ConnectionHandler handler = new ConnectionHandler();
        try {
            client = new TCPClientServer(ipv4, port, handler);
            client.fetchUid();
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "This market UID is " + client.getUid());
            client.run();
        } catch (IOException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Client server socket error or closed. Exit");
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
