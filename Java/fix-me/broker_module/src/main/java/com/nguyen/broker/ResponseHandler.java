package com.nguyen.broker;

import com.nguyen.broker.model.Portfolio;
import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.fix.FixParser;
import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;
import com.nguyen.broker.model.FixTransaction;
import com.nguyen.broker.model.MessageStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Map;

public class ResponseHandler {
    private final Portfolio portfolio;

    public ResponseHandler(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
    public void handle(String rawMessage) {
        Map<FixTag, String> fixMessage = FixParser.parse(rawMessage);
        String msgType = fixMessage.get(FixTag.MSG_TYPE);
        switch (msgType) {
            // Error
            case "3" -> {
                String error = fixMessage.get(FixTag.TEXT);
                String orderId = fixMessage.get(FixTag.ORDER_ID);
                if (error == null) {
                    String errorMessage = orderId == null ?
                            "Something went wrong, no Order ID specified"
                            : "Something went wrong with order " + orderId;
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + errorMessage);
                } else {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + error);
                }
                if (orderId != null) {
                    handleStatus(fixMessage, rawMessage);
                }
            }
            // Buy / Sell
            case "D" -> throw new InvalidFixFormatException("I am broker, Message Type D is invalid");
            // Status
            case "8" -> handleStatus(fixMessage, rawMessage);
            // Logon
            case "A" -> throw new InvalidFixFormatException("Already logged in, Message Type A is invalid");
            // RTFM
            default -> throw new InvalidFixFormatException("Unknown Message Type");
        }
    }

    private void handleStatus(Map<FixTag, String> fixMessage, String rawMessage) {
        SessionFactory sf;
        sf = HibernateSession.getInstance().getSessionFactory();

        long orderId;
        try {
            orderId = Long.parseLong(fixMessage.get(FixTag.ORDER_ID));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Order Id is invalid");
        }
        try (Session session = sf.openSession()) {
            if (fixMessage.get(FixTag.MSG_TYPE).equals("3")) {
                updateFixTransaction(session, orderId, rawMessage, MessageStatus.REJECTED);
                return;
            }
            String status = fixMessage.get(FixTag.ORD_STATUS);
            if (status == null) {
                throw new InvalidFixFormatException("No order status (39)");
            }
            switch (status) {
                case "2" ->  {
                    updateFixTransaction(session, orderId, rawMessage, MessageStatus.EXECUTED);
                    updatePortfolio(fixMessage);
                    portfolio.printPortfolio();
                }
                case "8" -> updateFixTransaction(session, orderId, rawMessage, MessageStatus.REJECTED);
                default -> {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Unknown transaction status");
                    throw new RuntimeException("Broker internal error");
                }
            }
        }
    }

    private void updateFixTransaction(
            Session session,
            long orderId,
            String response,
            MessageStatus status
    ) throws  InvalidFixFormatException, HibernateException {
        FixTransaction ft = session.find(FixTransaction.class, orderId);
        if (ft == null) {
            throw new InvalidFixFormatException("No matching local order for response");
        }
        Transaction tx = session.beginTransaction();
        try {
            ft.setStatus(status);
            ft.setFixResponseMessage(response);
            session.merge(ft);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }
    }

    private void updatePortfolio(Map<FixTag, String> fixMessage) {
        String symbol = fixMessage.get(FixTag.SYMBOL);
        String side = fixMessage.get(FixTag.SIDE);
        double quantity;
        try {
            quantity = Double.parseDouble(fixMessage.get(FixTag.ORDER_QTY));
        } catch (NumberFormatException e) {
            return;
        }
        if ("1".equals(side)) {
            portfolio.add(symbol, quantity);
        } else if ("2".equals(side)) {
            portfolio.remove(symbol, quantity);
        }
    }
}
