package com.nguyen.message;

import com.nguyen.fix.Colors;
import com.nguyen.message.exception.SocketErrorException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class TCPServer implements Callable<Void> {
    private final ServerSocket serverSocket;
    private final int port;
    private final String name;
    private final Consumer<Socket> connectionHandler;

    public TCPServer(int port, String name, Consumer<Socket> connectionHandler) throws SocketErrorException {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println(Colors.YELLOW +
                    "Info: " + Colors.RESET +
                    "Server " + name + " is bound to port " + Colors.CYAN +
                    port + Colors.RESET);
            this.port = port;
            this.name = name;
            this.connectionHandler = connectionHandler;
        } catch (IOException e) {
            close();
            throw new SocketErrorException("Server " + name + ":" + port + " fails to create server: " + e.getMessage());
        }
    }

    public Void call() throws SocketErrorException {
        try {
            System.out.println(Colors.YELLOW +
                    "Info: " + Colors.RESET +
                    "Server " + name + ":" + port +
                    " is listening to port " + Colors.CYAN +
                    port + Colors.RESET);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(Colors.YELLOW +
                        "Info: " + Colors.RESET +
                        "Server " + name + ":" + port +
                        ": Client connected: " + socket.getInetAddress());
                connectionHandler.accept(socket);
            }
        } catch (IOException e) {
            close();
            throw new SocketErrorException("Server " + name + ":" + port + " fails to create socket: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Server " + name + ":" + port + " fails to close TCP server: " + e.getMessage());
        }
    }
}
