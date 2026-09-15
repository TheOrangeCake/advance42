package com.nguyen.database;

import com.nguyen.colors.Colors;
import org.h2.tools.Server;
import java.sql.SQLException;

public class H2TcpServer {
    private static H2TcpServer instance;
    private final Server tcpServer;
    private final Server webServer;

    private H2TcpServer() throws  SQLException{
        tcpServer = Server.createTcpServer("-tcpPort", "4242", "-ifNotExists").start();
        System.out.println("H2 TCP Server started at: " + tcpServer.getURL());
        webServer = Server.createWebServer("-webPort", "8082").start();
        System.out.println("H2 Web Console at: " + webServer.getURL());
    }

    public static H2TcpServer getInstance() throws SQLException {
        if (instance == null) {
            instance = new H2TcpServer();
        }
        return instance;
    }

    public void stop() {
        if (webServer != null) {
            webServer.stop();
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "H2 Web Console stopped");
        }
        if (tcpServer != null) {
            tcpServer.stop();
            System.out.println(Colors.YELLOW + "Info: " + Colors.RESET + "H2 TCP Server stopped");
        }

    }
}
