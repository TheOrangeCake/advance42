package com.nguyen.message;

import com.nguyen.fix.Colors;
import com.nguyen.message.exception.SocketErrorException;

import java.util.concurrent.*;

public class Message {
    static void main() {
        ExecutorService serverService = Executors.newFixedThreadPool(2);
        ExecutorService connectionHandlerService = Executors.newCachedThreadPool();

        try {
            TCPServer brokerServer = new TCPServer(
                    5000,
                    "broker server",
                    (socket, port) -> connectionHandlerService.submit(new ConnectionHandler(socket, port)));
            TCPServer marketServer = new TCPServer(
                    5001,
                    "market server",
                    (socket, port) -> connectionHandlerService.submit(new ConnectionHandler(socket, port)));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                brokerServer.close();
                marketServer.close();
                serverService.shutdown();
                connectionHandlerService.shutdown();
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
        } catch (SocketErrorException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println(Colors.YELLOW + "WARN: " + Colors.RESET + "Server interrupted: " + e.getMessage());
            System.exit(0);
        }
    }
}
