package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.database.H2TcpClient;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Market {
    static void main() {
        System.out.println(Colors.YELLOW + "Hello, this is Market" + Colors.RESET);

        try {
            H2TcpClient db = H2TcpClient.getInstance();
        } catch (SQLException ex) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error. Exit");
            System.err.println(ex.getMessage());
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
        } catch (IOException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Client server socket error or closed. Exit");
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                System.err.println(Colors.RED + "Fail to close client socket" + Colors.RESET);
            }
        }
    }
}
