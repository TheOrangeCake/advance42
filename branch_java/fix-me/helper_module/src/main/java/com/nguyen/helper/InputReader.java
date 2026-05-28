package com.nguyen.helper;

import com.nguyen.colors.Colors;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readIP() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter server IPv4 to connect:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                if (!IPv4Parser.parseIPv4(input)) {
                    System.out.println(Colors.RED + "Invalid IPv4. Please try again" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return input;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public int readPort() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter server Port to connect:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                int port = Integer.parseInt(input);
                if (port < 1 || port > 65535) {
                    System.out.println(Colors.RED + "Invalid Port. Valid range is between 1 and 65535" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return port;
            } catch (NoSuchElementException e) {
                return -1;
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid Port. Please enter numeric value between 1 and 65535" + Colors.RESET);
                System.out.println();
            }
        }
    }

    public String readUid(String service) {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter " + service + " UID (format dddddd). Enter \"none\" to get new uid");
            try {
                String input = scanner.nextLine().trim();
                if (input.equals("none")) {
                    return "000000";
                }
                if (input.matches("\\d{6}")) {
                    return input;
                }
                System.out.println(Colors.RED + "Invalid UID. Please enter numeric value between 000001 and 999999" + Colors.RESET);
                System.out.println();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public String readSide() {
        while (true) {
            System.out.println(Colors.PURPLE + "Do you want to sell or buy?" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim().toLowerCase();
                if (input.equals("buy")) {
                    return "1";
                }
                if (input.equals("sell")) {
                    return "2";
                }
                System.out.println(Colors.RED + "Invalid command, enter either Buy or Sell" + Colors.RESET);
                System.out.println();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public String readSymbol() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter the instrument:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.isEmpty()) {
                    System.out.println(Colors.RED + "Symbol cannot be empty" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                if (!input.matches("[A-Z0-9]+")) {
                    System.out.println(Colors.RED + "Invalid symbol. Only letters and digits allowed" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return input;
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public String readQuantity() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter the quantity:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                double quantity = Double.parseDouble(input);
                if (quantity <= 0 || quantity > 999999) {
                    System.out.println(Colors.RED + "Invalid quantity. Must be higher than 0 and less than 999999" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return Double.toString(quantity);
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid quantity. Please enter numeric value" + Colors.RESET);
                System.out.println();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public String readPrice() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter the price point:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                double price = Double.parseDouble(input);
                if (price <= 0 || price > 999999) {
                    System.out.println(Colors.RED + "Invalid price. Must be higher than 0 and less than 999999" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return Double.toString(price);
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid price. Please enter numeric value" + Colors.RESET);
                System.out.println();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }

    public String readTargetUid() {
        while (true) {
            System.out.println(Colors.PURPLE + "Enter market id:" + Colors.RESET);
            try {
                String input = scanner.nextLine().trim();
                int targetUid = Integer.parseInt(input);
                if (targetUid <= 0 || targetUid > 999999) {
                    System.out.println(Colors.RED + "Invalid id. Must be between 1 and 999999" + Colors.RESET);
                    System.out.println();
                    continue;
                }
                return String.format("%06d", targetUid);
            } catch (NumberFormatException e) {
                System.out.println(Colors.RED + "Invalid id. Please enter numeric value between 1 and 999999" + Colors.RESET);
                System.out.println();
            } catch (NoSuchElementException e) {
                return null;
            }
        }
    }
}
