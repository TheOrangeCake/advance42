package com.nguyen.message;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RoutingTable {
    private static RoutingTable instance;
    private static final AtomicLong uidCounter = new AtomicLong(100000);
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
}
