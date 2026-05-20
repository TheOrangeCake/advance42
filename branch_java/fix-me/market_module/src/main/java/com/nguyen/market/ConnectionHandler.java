package com.nguyen.market;

import com.nguyen.colors.Colors;
import com.nguyen.fix.FixBuilder;
import com.nguyen.fix.FixParser;
import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;
import com.nguyen.helper.ClientFixHandler;

import java.util.Date;
import java.util.Map;

// Validator: https://docs.hibernate.org/validator/9.1/reference/en-US/html_single/#validator-gettingstarted-whatsnext
public class ConnectionHandler implements ClientFixHandler {
    @Override
    public String handle(String message, String uid) {
        try {
            Map<FixTag, String> fixMessage = FixParser.parse(message);
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
                    return handleTrade(fixMessage);
                }
                // Status
                case "8" -> {
                    throw new InvalidFixFormatException("I am market, Message Type 8 is invalid");
                }
                // Logon
                case "A" -> {
                    throw new InvalidFixFormatException("Already logged in, Message Type A is invalid");
                }
                // RTFM
                default -> {
                    throw new InvalidFixFormatException("Unknown Message Type");
                }
            }

        } catch (InvalidFixFormatException e) {
            String targetId = FixParser.extractRawTargetId(message);
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

    private String handleTrade(Map<FixTag, String> fixMessage) {
        // Get the data from fixMessage
        // Check if transaction already exists by orderId
        //   -> Not found: create new transaction as PENDING, execute order, update to EXECUTED/REJECTED, return response
        //   -> Found + PENDING: market crashed mid-execution, re-execute, update status, return response
        //   -> Found + EXECUTED: duplicate request from broker retry, return the stored response directly (idempotent)
        //   -> Found + REJECTED: duplicate request, return the stored rejection directly
    }
}
