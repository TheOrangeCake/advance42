package com.nguyen.fix;

import com.nguyen.colors.Colors;

import java.util.Map;

public class FixTranslator {

    public void translateToBroker(Map<FixTag, String> message) {
        if (message == null) {
            System.out.println(Colors.RED + "Translation error: null message" + Colors.RESET);
            return;
        }

        String msgType = message.get(FixTag.MSG_TYPE);

        StringBuilder out = new StringBuilder()
                .append(Colors.CYAN)
                .append("Market -> Broker: ")
                .append(Colors.RESET);

        if (msgType == null) {
            System.out.println(out.append("Missing MsgType (35)"));
            return;
        }

        switch (msgType) {
            case "3" -> {
                String orderId = message.get(FixTag.ORDER_ID);
                String text = message.get(FixTag.TEXT);
                String status = decodeOrdStatus(message.get(FixTag.ORD_STATUS));

                out.append("Order error ")
                        .append(orderId)
                        .append(": ")
                        .append(text != null ? text : "No details")
                        .append(" | Status: ")
                        .append(status)
                        .append(" | From Market ")
                        .append(message.get(FixTag.SENDER_COMP_ID))
                        .append(" to Broker ")
                        .append(message.get(FixTag.TARGET_COMP_ID));
            }

            case "8" -> {
                String orderId = message.get(FixTag.ORDER_ID);
                String symbol = message.get(FixTag.SYMBOL);
                String qty = message.get(FixTag.ORDER_QTY);
                String price = message.get(FixTag.PRICE);

                String side = decodeSide(message.get(FixTag.SIDE));
                String status = decodeOrdStatus(message.get(FixTag.ORD_STATUS));
                String text = message.get(FixTag.TEXT);

                out.append("Order ")
                        .append(orderId)
                        .append(" ")
                        .append(side)
                        .append(" ")
                        .append(symbol)
                        .append(" ")
                        .append(qty)
                        .append(" @ ")
                        .append(price)
                        .append(" => ")
                        .append(status);

                if (text != null && !text.isEmpty()) {
                    out.append(" | ").append(text);
                }
            }

            case "A" -> out.append("Logon established: Message ")
                        .append(message.get(FixTag.SENDER_COMP_ID))
                        .append(" and Broker ")
                        .append(message.get(FixTag.TARGET_COMP_ID));

            case "D" -> out.append("Unexpected D message received by broker");

            default -> out.append("Unknown MsgType=").append(msgType);
        }

        System.out.println(out);
        System.out.println();
    }

    public void translateToMarket(Map<FixTag, String> message) {
        if (message == null) {
            System.out.println(Colors.RED + "Translation error: null message" + Colors.RESET);
            return;
        }

        String msgType = message.get(FixTag.MSG_TYPE);

        StringBuilder out = new StringBuilder()
                .append(Colors.CYAN)
                .append("Broker -> Market: ")
                .append(Colors.RESET);

        if (msgType == null) {
            System.out.println(out.append("Missing MsgType (35)"));
            return;
        }

        switch (msgType) {
            case "D" -> {
                String side = decodeSide(message.get(FixTag.SIDE));
                String symbol = message.get(FixTag.SYMBOL);
                String qty = message.get(FixTag.ORDER_QTY);
                String price = message.get(FixTag.PRICE);
                String orderId = message.get(FixTag.ORDER_ID);

                out.append("New order: ")
                        .append(side)
                        .append(" ")
                        .append(qty)
                        .append(" ")
                        .append(symbol)
                        .append(" @ ")
                        .append(price)
                        .append(" (Order ID ")
                        .append(orderId)
                        .append(")");
            }

            case "A" -> out.append("Logon established: Message ")
                    .append(message.get(FixTag.SENDER_COMP_ID))
                    .append(" and Market ")
                    .append(message.get(FixTag.TARGET_COMP_ID));

            default -> {
                out.append("MsgType=")
                        .append(msgType)
                        .append(" forwarded to market");
            }
        }

        System.out.println(out);
        System.out.println();
    }

    private String decodeSide(String side) {
        return switch (side) {
            case "1" -> "BUY";
            case "2" -> "SELL";
            default -> side;
        };
    }

    private String decodeOrdStatus(String status) {
        return switch (status) {
            case "2" -> "FILLED";
            case "8" -> "REJECTED";
            default -> status;
        };
    }
}