package com.nguyen.fix;

import java.util.Map;

public class FixTranslator {
    private FixTranslator() {
    }

    public static String translate(String rawMessage) {
        try {
            return describe(FixParser.parse(rawMessage));
        } catch (RuntimeException e) {
            return "(unparseable: " + e.getMessage() + ")";
        }
    }

    private static String describe(Map<FixTag, String> message) {
        String msgType = message.get(FixTag.MSG_TYPE);
        String sender = message.get(FixTag.SENDER_COMP_ID);
        String target = message.get(FixTag.TARGET_COMP_ID);
        return switch (msgType) {
            case "A" -> "Logon: " + sender + " <-> " + target;
            case "D" -> describeOrder(message, sender, target);
            case "8" -> describeStatus(message, sender, target);
            case "3" -> describeReject(message, sender, target);
            default -> "Unknown MsgType=" + msgType;
        };
    }

    private static String describeOrder(Map<FixTag, String> message, String sender, String target) {
        return String.format("Order #%s: %s %s %s @ %s | %s -> %s",
                message.get(FixTag.ORDER_ID),
                decodeSide(message.get(FixTag.SIDE)),
                message.get(FixTag.ORDER_QTY),
                message.get(FixTag.SYMBOL),
                message.get(FixTag.PRICE),
                sender, target);
    }

    private static String describeStatus(Map<FixTag, String> message, String sender, String target) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(message.get(FixTag.ORDER_ID))
                .append(' ').append(decodeOrdStatus(message.get(FixTag.ORD_STATUS)));
        String text = message.get(FixTag.TEXT);
        if (text != null) {
            sb.append(" (").append(text).append(')');
        }
        sb.append(" | ").append(sender).append(" -> ").append(target);
        return sb.toString();
    }

    private static String describeReject(Map<FixTag, String> message, String sender, String target) {
        StringBuilder sb = new StringBuilder("Reject");
        String orderId = message.get(FixTag.ORDER_ID);
        if (orderId != null) {
            sb.append(" #").append(orderId);
        }
        String text = message.get(FixTag.TEXT);
        sb.append(": ").append(text != null ? text : "no details");
        sb.append(" | ").append(sender).append(" -> ").append(target);
        return sb.toString();
    }

    private static String decodeSide(String side) {
        if (side == null) return "?";
        return switch (side) {
            case "1" -> "BUY";
            case "2" -> "SELL";
            default -> side;
        };
    }

    private static String decodeOrdStatus(String status) {
        if (status == null) return "?";
        return switch (status) {
            case "2" -> "FILLED";
            case "8" -> "REJECTED";
            default -> status;
        };
    }
}
