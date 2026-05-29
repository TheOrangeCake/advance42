package com.nguyen.message;

import com.nguyen.database.HibernateSession;
import com.nguyen.message.model.UidCounter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.concurrent.ConcurrentHashMap;

public class RoutingTable {
    private static final Integer COUNTER_ROW_ID = 1;
    private static RoutingTable instance;
    private final Object uidLock = new Object();
    private long nextUid;
    private final ConcurrentHashMap<String, ConnectionHandler> brokerTable = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConnectionHandler> marketTable = new ConcurrentHashMap<>();

    private RoutingTable() {
        nextUid = loadOrInitCounter();
    }

    public static synchronized RoutingTable getInstance() {
        if (instance == null) {
            instance = new RoutingTable();
        }
        return instance;
    }

    public String generateUid() {
        synchronized (uidLock) {
            long uid = nextUid;
            if (uid > 999999) {
                throw new IllegalStateException("UID pool exhausted, maximum connections reached");
            }
            nextUid = uid + 1;
            persistCounter(nextUid);
            return String.format("%06d", uid);
        }
    }

    public boolean isActive(String providedUid, Integer port) {
        boolean isActive;
        if (port.equals(5001)) {
            isActive = marketTable.containsKey(providedUid);
        } else if (port.equals(5000)) {
            isActive = brokerTable.containsKey(providedUid);
        } else {
            throw new IllegalArgumentException("Invalid Port");
        }
        return isActive;
    }

    public void bumpUidCounter(String uid) {
        long presented = Long.parseLong(uid);
        synchronized (uidLock) {
            long updated = Math.max(nextUid, presented + 1);
            if (updated != nextUid) {
                nextUid = updated;
                persistCounter(nextUid);
            }
        }
    }

    private static long loadOrInitCounter() {
        SessionFactory sf = HibernateSession.getInstance().getSessionFactory();
        try (Session session = sf.openSession()) {
            UidCounter counter = session.find(UidCounter.class, COUNTER_ROW_ID);
            if (counter == null) {
                Transaction tx = session.beginTransaction();
                session.persist(new UidCounter(COUNTER_ROW_ID, 1L));
                tx.commit();
                return 1L;
            }
            return counter.getNextUid();
        }
    }

    private void persistCounter(long value) {
        SessionFactory sf = HibernateSession.getInstance().getSessionFactory();
        try (Session session = sf.openSession()) {
            UidCounter counter = session.find(UidCounter.class, COUNTER_ROW_ID);
            Transaction tx = session.beginTransaction();
            counter.setNextUid(value);
            session.merge(counter);
            tx.commit();
        }
    }

    public void addToRoutingTable(String uid, ConnectionHandler handler, Integer port) {
        if (port.equals(5001)) {
            marketTable.put(uid, handler);
        } else if (port.equals(5000)) {
            brokerTable.put(uid, handler);
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

    public ConnectionHandler getHandler(String targetId, Integer senderPort) {
        if (senderPort.equals(5000)) {
            ConnectionHandler handler = marketTable.get(targetId);
            if (handler == null) {
                throw new IllegalArgumentException("Market not found: " + targetId);
            }
            return handler;
        } else if (senderPort.equals(5001)) {
            ConnectionHandler handler = brokerTable.get(targetId);
            if (handler == null) {
                throw new IllegalArgumentException("Broker not found: " + targetId);
            }
            return handler;
        } else {
            throw new IllegalArgumentException("Invalid port: " + senderPort);
        }
    }
}
