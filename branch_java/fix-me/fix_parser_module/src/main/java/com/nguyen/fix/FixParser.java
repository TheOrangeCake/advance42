package com.nguyen.fix;

import com.nguyen.colors.Colors;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FixParser {
    public static final char SOH = '\u0001';
    private static final FixTag[] requiredMainTags = {
            FixTag.BEGIN_STRING,
            FixTag.BODY_LENGTH,
            FixTag.MSG_TYPE,
            FixTag.SENDER_COMP_ID,
            FixTag.SENDING_TIME,
            FixTag.TARGET_COMP_ID,
            FixTag.CHECKSUM
    };
    private static final FixTag[] requiredOrderTags = {
            FixTag.ORDER_ID,
            FixTag.SIDE,
            FixTag.SYMBOL,
            FixTag.ORDER_QTY,
            FixTag.PRICE,
    };
    private static final FixTag[] requiredStatusTags = {
            FixTag.ORDER_ID,
            FixTag.ORD_STATUS,
    };

    public static Map<FixTag, String> parse(String message) throws InvalidFixFormatException {
        if (message == null || message.isEmpty()) {
            throw new InvalidFixFormatException("FIX message is empty");
        }
        Map<FixTag, String> fields = new LinkedHashMap<>();
        String[] pairs = message.split(String.valueOf(SOH));

        for (String pair : pairs) {
            if (pair.isEmpty()) {
                continue;
            }

            int equalIndex = pair.indexOf('=');
            if (equalIndex <= 0) {
                throw new InvalidFixFormatException("Invalid pair format: " + pair);
            }

            String key = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            if (value.isEmpty()) {
                throw new InvalidFixFormatException("Invalid value for: " + key);
            }

            int tagInt;
            try {
                tagInt = Integer.parseInt(key);
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid tag: " + key);
            }
            FixTag fixTag = FixTag.checkTag(tagInt);

            if (fields.containsKey(fixTag)) {
                throw new InvalidFixFormatException("Duplicate tag: " + tagInt);
            }
            fields.put(fixTag, value);
        }
        validateRequired(fields);
        validateFieldsFormat(fields);
        validateOrder(fields);
        validateBodyLength(message, fields);
        validateChecksum(message, fields);
        return fields;
    }

    private static void validateRequired(Map<FixTag, String> fields) {
        for (FixTag fixTag : requiredMainTags) {
            if (!fields.containsKey(fixTag)) {
                throw new InvalidFixFormatException("Missing required tag: " + fixTag);
            }
        }
        String msgType = fields.get(FixTag.MSG_TYPE);
        if (msgType.equals("D")) {
            for (FixTag fixTag : requiredOrderTags) {
                if (!fields.containsKey(fixTag)) {
                    throw new InvalidFixFormatException("Missing required tag: " + fixTag);
                }
            }
        } else if (msgType.equals("8")) {
            for (FixTag fixTag : requiredStatusTags) {
                if (!fields.containsKey(fixTag)) {
                    throw new InvalidFixFormatException("Missing required tag: " + fixTag);
                }
            }
        } else if (!msgType.equals("3") && !msgType.equals("A")) {
            throw new InvalidFixFormatException("Unknown MsgType: " + msgType);
        }
    }

    private static void validateFieldsFormat(Map<FixTag, String> fields) {
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
    }

    private static void validateOrder(Map<FixTag, String> fields) {
        List<FixTag> keys = new ArrayList<>(fields.keySet());
        if (keys.getFirst() != FixTag.BEGIN_STRING) {
            throw new InvalidFixFormatException("Tag 8 (BeginString) must be first");
        }
        if (keys.get(1) != FixTag.BODY_LENGTH) {
            throw new InvalidFixFormatException("Tag 9 (BodyLength) must be second");
        }
        if (keys.get(2) != FixTag.MSG_TYPE) {
            throw new InvalidFixFormatException("Tag 35 (MessageType) must be third");
        }
        if (keys.get(3) != FixTag.SENDER_COMP_ID) {
            throw new InvalidFixFormatException("Tag 49 (SenderCompId) must be Fourth");
        }
        if (keys.getLast() != FixTag.CHECKSUM)
            throw new InvalidFixFormatException("Tag 10 (Checksum) must be last");
    }

    private static void validateChecksum(String message, Map<FixTag, String> fields) {
        try {
            int receivedChecksum = Integer.parseInt(fields.get(FixTag.CHECKSUM));
            int checksumIndex = message.lastIndexOf("10=");
            if (checksumIndex <= 0) {
                throw new InvalidFixFormatException("No Checksum field");
            }
            byte[] messageBytes = message.substring(0, checksumIndex).getBytes(StandardCharsets.US_ASCII);
            int calculatedChecksum = 0;
            for (byte b : messageBytes) {
                calculatedChecksum += b;
            }
            calculatedChecksum %= 256;
            if (calculatedChecksum != receivedChecksum) {
                throw new InvalidFixFormatException("Checksum incorrect: " + fields.get(FixTag.CHECKSUM));
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid checksum: " + fields.get(FixTag.CHECKSUM));
        }
    }

    private static void validateBodyLength(String message, Map<FixTag, String> fields) {
        try {
            int receivedBodyLength = Integer.parseInt(fields.get(FixTag.BODY_LENGTH));
            int messageTypeIndex = message.indexOf("35=");
            int checksumIndex = message.lastIndexOf("10=");
            if (messageTypeIndex <= 0 || checksumIndex <= 0) {
                throw new InvalidFixFormatException("Invalid FIX format");
            }
            int calculatedBodyLength = message.substring(messageTypeIndex, checksumIndex).getBytes(StandardCharsets.US_ASCII).length;
            if (receivedBodyLength != calculatedBodyLength) {
                throw new InvalidFixFormatException("Body Length incorrect: " + fields.get(FixTag.BODY_LENGTH));
            }
        } catch (NumberFormatException e) {
            throw new InvalidFixFormatException("Invalid Body Length: " + fields.get(FixTag.BODY_LENGTH));
        }
    }

    public static String extractRawTargetId(String message) {
        String search = '\u0001' + "56=";
        int start = message.indexOf(search);
        if (start == -1) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid Target Id, throw to the trash");
            return "000000";
        }
        start += search.length();
        int end = message.indexOf('\u0001', start);
        if (end == -1) {
            System.err.println(Colors.RED + "Error: " + Colors.RESET + "Invalid Target Id, throw to the trash");
            return "000000";
        }
        return message.substring(start, end);
    }
}
