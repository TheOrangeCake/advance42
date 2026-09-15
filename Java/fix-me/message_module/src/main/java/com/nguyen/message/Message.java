package com.nguyen.message;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.message.exception.SocketErrorException;
import org.hibernate.HibernateException;

import java.util.concurrent.*;

public class Message {
    static void main() {
        try {
            HibernateSession.init("message.cfg.xml");
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
            System.err.println(e.getMessage());
            return;
        }
        ExecutorService serverService = Executors.newFixedThreadPool(2);
        ExecutorService connectionHandlerService = Executors.newCachedThreadPool();

        try {
            TCPServer brokerServer = new TCPServer(
                    5000,
                    "Broker",
                    (socket, port) -> connectionHandlerService.submit(new ConnectionHandler(socket, port)));
            TCPServer marketServer = new TCPServer(
                    5001,
                    "Market",
                    (socket, port) -> connectionHandlerService.submit(new ConnectionHandler(socket, port)));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                brokerServer.close();
                marketServer.close();
                serverService.shutdown();
                connectionHandlerService.shutdown();
                HibernateSession.getInstance().stop();
            }));

            Future<Void> brokerFuture = serverService.submit(brokerServer);
            Future<Void> marketFuture = serverService.submit(marketServer);
            try {
                brokerFuture.get();
                marketFuture.get();
            } catch (ExecutionException e) {
                System.err.println(Colors.RED + "Error: " + Colors.RESET + "Server crashed: " + e.getCause().getMessage());
                System.exit(1);
            }
        } catch (NullPointerException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Another Message server already running");
            System.exit(1);
        } catch (SocketErrorException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println(Colors.YELLOW + "WARN: " + Colors.RESET + "Server interrupted: " + e.getMessage());
        }
    }
}
