package com.nguyen.message;

import com.nguyen.fix.Colors;
import com.nguyen.message.exception.SocketErrorException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;

public class TCPServer implements Callable<Void> {
    private final ServerSocket serverSocket;
    private final int port;
    private final String name;
    private final BiConsumer<Socket, Integer> connectionHandler;

    public TCPServer(int port, String name, BiConsumer<Socket, Integer> connectionHandler) throws SocketErrorException {
        try {
            this.serverSocket = new ServerSocket(port);
            // add server ip address as well
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
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println(Colors.YELLOW +
                    "Info: " + Colors.RESET +
                    "Server " + name + " is listening on " + Colors.CYAN +
                    ip + ":" + port + Colors.RESET);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println(Colors.YELLOW +
                        "Info: " + Colors.RESET +
                        "Server " + name + ":" + port +
                        ": Client connected: " + socket.getInetAddress());
                connectionHandler.accept(socket, port);
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
