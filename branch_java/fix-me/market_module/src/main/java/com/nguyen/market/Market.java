package com.nguyen.market;

import com.nguyen.helper.Colors;
import com.nguyen.helper.InputReader;
import com.nguyen.helper.TCPClientServer;

import java.io.IOException;
import java.util.Scanner;

public class Market {
    static void main() {
        System.out.println(Colors.YELLOW + "Hello, this is Market" + Colors.RESET);

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
            System.err.println(Colors.RED + "Client server socket error or closed. Exit" + Colors.RESET);
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
