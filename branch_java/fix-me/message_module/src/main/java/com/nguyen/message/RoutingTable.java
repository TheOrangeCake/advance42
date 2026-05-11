package com.nguyen.message;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RoutingTable {
    private static RoutingTable instance;
    private static final AtomicLong uidCounter = new AtomicLong(1);
    private final ConcurrentHashMap<String, Socket> brokerTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Socket> marketTable = new ConcurrentHashMap<>();

    private RoutingTable() {

    }

    public static synchronized RoutingTable getInstance() {
        if (instance == null) {
            instance = new RoutingTable();
        }
        return instance;
    }

    public String generateUid() {
        long uid = uidCounter.getAndIncrement();
        if (uid > 999999) {
            throw new IllegalStateException("UID pool exhausted — maximum connections reached");
        }
        return String.format("%06d", uid);
    }

    public void addToRoutingTable(String uid, Socket socket, Integer port) {
        if (port.equals(5001)) {
            marketTable.put(uid, socket);
        } else if (port.equals(5000)) {
            brokerTable.put(uid, socket);
        } else {
            throw new IllegalArgumentException("Invalid Port");
        }
    }

    public void removeFromRoutingTable(String uid, Integer port) {
        if (port.equals(5001)) {
            marketTable.remove(uid);
        } else if (port.equals(5000)) {
            brokerTable.remove(uid);
        } else {
            throw new IllegalArgumentException("Invalid Port");
        }
    }

    public Socket getSocket(String targetId, Integer senderPort) {
        if (senderPort.equals(5000)) {
            Socket s = marketTable.get(targetId);
            if (s == null) {
                throw new IllegalArgumentException("Market not found: " + targetId);
            }
            return s;
        } else if (senderPort.equals(5001)) {
            Socket s = brokerTable.get(targetId);
            if (s == null) {
                throw new IllegalArgumentException("Broker not found: " + targetId);
            }
            return s;
        } else {
            throw new IllegalArgumentException("Invalid port: " + senderPort);
        }
    }
}
