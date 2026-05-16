package com.nguyen.database;

import com.nguyen.colors.Colors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2TcpClient {
    private static H2TcpClient instance;
    private final Connection connection;
    private final String jdbcUrl = "jdbc:h2:tcp://localhost:4242/file:./data/h2/fixMeDb";
    private final String username = "sa"; // Default username
    private final String password = "";

    private H2TcpClient() throws SQLException {
        connection = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "Connected to H2 TCP Server port 4242");
    }

    public static H2TcpClient getInstance() throws SQLException {
        if (instance == null) {
            instance = new H2TcpClient();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
