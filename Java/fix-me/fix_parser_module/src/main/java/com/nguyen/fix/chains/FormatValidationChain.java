package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.util.Map;

public class FormatValidationChain extends ChainAbstract {
    @Override
    public void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException {
        String beginString = fields.get(FixTag.BEGIN_STRING);
        if (!beginString.equals("FIX.4.4")) {
            throw new InvalidFixFormatException("Invalid BeginString or bad FIX version (only 4.4): " + beginString);
        }

        String bodyLength = fields.get(FixTag.BODY_LENGTH);
        try {
            if (Integer.parseInt(bodyLength) <= 0) {
                throw new InvalidFixFormatException("BodyLength must be positive: " + bodyLength);
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid BodyLength: " + bodyLength);
        }

        String senderCompId = fields.get(FixTag.SENDER_COMP_ID);
        if (!senderCompId.matches("\\d{6}")) {
            throw new InvalidFixFormatException("Invalid SenderCompID: " + senderCompId);
        }

        String targetCompId = fields.get(FixTag.TARGET_COMP_ID);
        if (targetCompId != null && !targetCompId.matches("\\d{6}")) {
            throw new InvalidFixFormatException("Invalid TargetCompID: " + targetCompId);
        }

        String orderId = fields.get(FixTag.ORDER_ID);
        if (orderId != null) {
            try {
                if (Long.parseLong(orderId) <= 0) {
                    throw new InvalidFixFormatException("OrderID must be positive: " + orderId);
                }
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid OrderID, must be a positive number: " + orderId);
            }
        }

        String orderQty = fields.get(FixTag.ORDER_QTY);
        if (orderQty != null) {
            try {
                if (Double.parseDouble(orderQty) <= 0) {
                    throw new InvalidFixFormatException("OrderQty must be positive: " + orderQty);
                }
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid OrderQty: " + orderQty);
            }
        }

        String price = fields.get(FixTag.PRICE);
        if (price != null) {
            try {
                if (Double.parseDouble(price) <= 0) {
                    throw new InvalidFixFormatException("Price must be positive: " + price);
                }
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid Price: " + price);
            }
        }

        String side = fields.get(FixTag.SIDE);
        if (side != null && !side.equals("1") && !side.equals("2")) {
            throw new InvalidFixFormatException("Invalid Side: " + side);
        }

        String ordStatus = fields.get(FixTag.ORD_STATUS);
        if (ordStatus != null && !ordStatus.equals("2") && !ordStatus.equals("8")) {
            throw new InvalidFixFormatException("Invalid OrdStatus: " + ordStatus);
        }

        String checksum = fields.get(FixTag.CHECKSUM);
        try {
            int checksumInt = Integer.parseInt(checksum);
            if (checksumInt < 0 || checksumInt > 255) {
                throw new InvalidFixFormatException("Checksum out of range: " + checksum);
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid Checksum: " + checksum);
        }

        if (next != null) {
            next.handle(rawFix, fields);
        }
    }
}
