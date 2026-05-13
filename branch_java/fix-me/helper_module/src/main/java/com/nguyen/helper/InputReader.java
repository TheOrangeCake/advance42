package com.nguyen.helper;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readIP() {
        while(true) {
            System.out.println(Colors.PURPLE + "Enter server IPv4 to connect:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                if (!IPv4Parser.parseIPv4(input)) {
                    System.out.println(Colors.RED + "Invalid IPv4. Please try again" + Colors.RESET);
                    continue;
                }
                return input;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public int readPort() {
        while(true) {
            System.out.println(Colors.PURPLE + "Enter server Port to connect:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                int port = Integer.parseInt(input);
                if (port < 1 || port > 65535) {
                    System.out.println(Colors.RED + "Invalid Port. Please try again" + Colors.RESET);
                    continue;
                }
                return port;
            } catch (NoSuchElementException e) {
                return -1;
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid Port. Please try again" + Colors.RESET);
            }
        }
    }
}
