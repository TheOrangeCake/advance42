package com.nguyen.database;

import com.nguyen.colors.Colors;

import java.sql.SQLException;

public class Database {
    static void main() {
        try {
            H2TcpServer server = H2TcpServer.getInstance();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
            }));
            while (true) {
                // run until ctrl c
            }
        } catch (SQLException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "H2 TCP Server failed");
        }
    }
}
