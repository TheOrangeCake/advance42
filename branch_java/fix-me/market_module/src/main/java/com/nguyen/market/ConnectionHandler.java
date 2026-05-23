package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.database.HibernateSession;
import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixParser;
import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;
import com.nguyen.market.model.FixTransaction;
import com.nguyen.market.model.Instrument;
import com.nguyen.market.model.MessageStatus;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConnectionHandler {
    public String handle(String rawMessage, String uid) {
        try {
            Map<FixTag, String> fixMessage = FixParser.parse(rawMessage);
            String msgType = fixMessage.get(FixTag.MSG_TYPE);
            switch (msgType) {
                // Error
                case "3" -> {
                    String error = fixMessage.get(FixTag.TEXT);
                    if (error == null) {
                        String orderId = fixMessage.get(FixTag.ORDER_ID);
                        String errorMessage = orderId == null ?
                                "Something went wrong, no Order ID specified"
                                : "Something went wrong with order " + orderId;
                        System.err.println(Colors.RED + "Error: " + Colors.RESET + errorMessage);
                    } else {
                        System.err.println(Colors.RED + "Error: " + Colors.RESET + error);
                    }
                    return null;
                }
                // Buy / Sell
                case "D" -> {
                    return handleTrade(fixMessage, rawMessage);
                }
                // Status
                case "8" -> throw new InvalidFixFormatException("I am market, Message Type 8 is invalid");
                // Logon
                case "A" -> throw new InvalidFixFormatException("Already logged in, Message Type A is invalid");
                // RTFM
                default -> throw new InvalidFixFormatException("Unknown Message Type");
            }

        } catch (HibernateException e) {
            throw e;
        } catch (RuntimeException e) {
            String targetId = FixParser.extractRawTargetId(rawMessage);
            return new FixBuilder.Builder()
                    .beginString("FIX.4.4")
                    .messageType("3")
                    .senderId(uid)
                    .targetId(targetId)
                    .sendingTime(new Date())
                    .text(e.getMessage())
                    .build()
                    .getFixMessage();
        }

    }

    private String handleTrade(Map<FixTag, String> fixMessage, String rawMessage) {
        SessionFactory sf;
        try {
            sf = HibernateSession.getInstance().getSessionFactory();
        } catch (HibernateException e) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Database connection error");
            System.err.println(e.getMessage());
            throw new RuntimeException("Market internal error");
        }
        long orderId;
        try {
            orderId = Long.parseLong(fixMessage.get(FixTag.ORDER_ID));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Order Id is invalid");
        }
        try (Session session = sf.openSession()) {
            FixTransaction transaction = session.find(FixTransaction.class, orderId);
            if (transaction == null) {
                Transaction tx = session.beginTransaction();
                FixTransaction ft = new FixTransaction(rawMessage, orderId);
                session.persist(ft);
                tx.commit();
                return doTrade(session, orderId, fixMessage);
            }
            MessageStatus status = transaction.getStatus();
            switch (status) {
                case EXECUTED, REJECTED ->  {
                    return transaction.getFixResponseMessage();
                }
                case PENDING -> {
                    return doTrade(session, orderId, fixMessage);
                }
                default -> {
                    System.err.println(Colors.RED + "Error: " + Colors.RESET + "Unknown transaction status");
                    throw new RuntimeException("Market internal error");
                }
            }
        }
    }

    private String doTrade(Session session, long orderId, Map<FixTag, String> fixMessage) {
        Transaction tx = session.beginTransaction();
        try {
            String side = fixMessage.get(FixTag.SIDE);
            if (side == null) {
                throw new RuntimeException("Empty Side (54) when Message Type (35) is D");
            }
            String responseMessage = switch (side) {
                case "1" -> doBuy(session, orderId, fixMessage);
                case "2" -> doSell(session, orderId, fixMessage);
                default -> throw new RuntimeException("Unknown Side Tag: " + side);
            };
            tx.commit();
            printInstruments(session);
            return responseMessage;
        } catch (HibernateException e) {
            tx.rollback();
            throw e;
        }
    }

    private String doBuy(Session session, long orderId, Map<FixTag, String> fixMessage) {
        try {
            String symbol = fixMessage.get(FixTag.SYMBOL);
            double price = Double.parseDouble(fixMessage.get(FixTag.PRICE));
            double quantity = Double.parseDouble(fixMessage.get(FixTag.ORDER_QTY));
            if (symbol == null) {
                String response = createFix(fixMessage, "8", "Missing Symbol (tag 55)");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }

            Instrument instrument = session.find(Instrument.class, symbol.toUpperCase());
            String response;
            if (instrument == null) {
                response = createFix(fixMessage, "8", "Instrument (tag 55) does not exist");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }
            if (instrument.getStock() < quantity) {
                response = createFix(fixMessage, "8", "Quantity (tag 38) is more than stock");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }
            if (instrument.getPrice() > price) {
                response = createFix(fixMessage, "8", "Price (tag 44) is lower than current price");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }
            instrument.decreaseStock(quantity);
            session.merge(instrument);
            response = createFix(fixMessage, "2", "Order executed");
            updateFixTransaction(session, orderId, response, MessageStatus.EXECUTED);
            return  response;

        } catch (RuntimeException e) {
            String response = createFix(fixMessage, "8", "Price or Quantity might be invalid");
            updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
            return response;
        }
    }

    private String doSell(Session session, long orderId, Map<FixTag, String> fixMessage) {
        try {
            String symbol = fixMessage.get(FixTag.SYMBOL);
            double price = Double.parseDouble(fixMessage.get(FixTag.PRICE));
            double quantity = Double.parseDouble(fixMessage.get(FixTag.ORDER_QTY));
            if (symbol == null) {
                String response = createFix(fixMessage, "8", "Missing Symbol (tag 55)");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }

            Instrument instrument = session.find(Instrument.class, symbol.toUpperCase());
            String response;
            if (instrument == null) {
                response = createFix(fixMessage, "8", "Instrument (tag 55) does not exist");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }
            if (instrument.getPrice() < price) {
                response = createFix(fixMessage, "8", "Price (tag 44) is higher than current price");
                updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
                return response;
            }
            instrument.increaseStock(quantity);
            session.merge(instrument);
            response = createFix(fixMessage, "2", "Order executed");
            updateFixTransaction(session, orderId, response, MessageStatus.EXECUTED);
            return  response;

        } catch (RuntimeException e) {
            String response = createFix(fixMessage, "8", "Price or Quantity might be invalid");
            updateFixTransaction(session, orderId, response, MessageStatus.REJECTED);
            return response;
        }
    }

    private void updateFixTransaction(Session session, long orderId, String response, MessageStatus status) {
        FixTransaction ft = session.find(FixTransaction.class, orderId);
        ft.setStatus(status);
        ft.setFixResponseMessage(response);
        session.merge(ft);
    }

    private String createFix(
            Map<FixTag, String> fixMessage,
            String status,
            String message
    ) {
        return new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("8")
                .senderId(fixMessage.get(FixTag.TARGET_COMP_ID))
                .targetId(fixMessage.get(FixTag.SENDER_COMP_ID))
                .orderId(fixMessage.get(FixTag.ORDER_ID))
                .sendingTime(new Date())
                .symbol(fixMessage.get(FixTag.SYMBOL))
                .orderQuantity(fixMessage.get(FixTag.ORDER_QTY))
                .price(fixMessage.get(FixTag.PRICE))
                .side(fixMessage.get(FixTag.SIDE))
                .orderStatus(status)
                .text(message)
                .build()
                .getFixMessage();
    }

    private void printInstruments(Session session) {
        List<Instrument> instruments = session.createQuery("FROM Instrument", Instrument.class).list();
        Instrument.printAll(instruments);
    }
}
