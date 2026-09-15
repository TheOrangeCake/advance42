package com.nguyen.fix;

import com.nguyen.colors.Colors;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FixBuilder {
    private final String beginString;
    private final String bodyLength;
    private final String messageType;
    private final String senderId;
    private final String targetId;
    private final String orderId;
    private final String sendingTime;
    private final String symbol;
    private final String orderQuantity;
    private final String side;
    private final String price;
    private final String orderStatus;
    private final String text;
    private final String checksum;

    private static final char SOH = '\u0001';

    private FixBuilder(Builder builder) {
        this.beginString = builder.beginString;
        this.bodyLength = builder.bodyLength;
        this.messageType = builder.messageType;
        this.senderId = builder.senderId;
        this.targetId = builder.targetId;
        this.orderId = builder.orderId;
        this.sendingTime = builder.sendingTime;
        this.symbol = builder.symbol;
        this.orderQuantity = builder.orderQuantity;
        this.side = builder.side;
        this.price = builder.price;
        this.orderStatus = builder.orderStatus;
        this.text = builder.text;
        this.checksum = builder.checksum;
    }

    public String getFixMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("8=").append(beginString).append(SOH);
        sb.append("9=").append(bodyLength).append(SOH);
        sb.append("35=").append(messageType).append(SOH);
        sb.append("49=").append(senderId).append(SOH);
        sb.append("56=").append(targetId).append(SOH);
        appendIfPresent(sb, "11", orderId);
        sb.append("52=").append(sendingTime).append(SOH);
        appendIfPresent(sb, "55", symbol);
        appendIfPresent(sb, "38", orderQuantity);
        appendIfPresent(sb, "54", side);
        appendIfPresent(sb, "44", price);
        appendIfPresent(sb, "39", orderStatus);
        appendIfPresent(sb, "58", text);
        sb.append("10=").append(checksum).append(SOH);
        return sb.toString();
    }

    private static void appendIfPresent(StringBuilder sb, String tag, String value) {
        if (value != null) {
            sb.append(tag).append("=").append(value).append(SOH);
        }
    }

    public String getBeginString() {
        return beginString;
    }
    public String getBodyLength() {
        return bodyLength;
    }
    public String getMessageType() {
        return messageType;
    }
    public String getSenderId() {
        return senderId;
    }
    public String getTargetId() {
        return targetId;
    }
    public String getOrderId() { return orderId; }
    public String getSendingTime() {
        return sendingTime;
    }
    public String getSymbol() {
        return symbol;
    }
    public String getOrderQuantity() {
        return orderQuantity;
    }
    public String getSide() {
        return side;
    }
    public String getPrice() {
        return price;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public String getText() { return text; }
    public String getChecksum() {
        return checksum;
    }

    public static class Builder {
        private String beginString;
        private String messageType;
        private String senderId;
        private String targetId;
        private String orderId;
        private String sendingTime;
        private String bodyLength;
        private String symbol;
        private String orderQuantity;
        private String side;
        private String price;
        private String orderStatus;
        private String text;
        private String checksum;

        public Builder beginString(String beginString) {
            if (beginString == null || beginString.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "beginString (Tag 8) must not be blank");
            }
            if (!beginString.matches("FIX\\.\\d+\\.\\d+")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET +  "beginString must be in format FIX.X.X, got: " + beginString);
            }
            this.beginString = beginString;
            return this;
        }

        public Builder messageType(String messageType) {
            if (messageType == null || !messageType.matches("[AD83]")) {
                throw new InvalidFixFormatException("messageType must be A, D, 8, or 3, got: " + messageType);
            }
            this.messageType = messageType;
            return this;
        }

        public Builder senderId(String senderId) {
            if (senderId == null || senderId.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "senderId (Tag 49) must not be blank");
            }
            if (!senderId.matches("\\d{6}")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "senderId (Tag 49) must be 6 digits");
            }
            this.senderId = senderId;
            return this;
        }

        public Builder targetId(String targetId) {
            if (targetId == null || targetId.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "targetId (Tag 56) must not be blank");
            }
            if (!targetId.matches("\\d{6}")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "targetId (Tag 49) must be 6 digits");
            }
            this.targetId = targetId;
            return this;
        }

        public Builder orderId(String orderId) {
            if (orderId == null || orderId.isBlank()) {
                throw new InvalidFixFormatException("orderId (Tag 11) must not be blank");
            }
            try {
                if (Long.parseLong(orderId) <= 0) {
                    throw new InvalidFixFormatException("OrderID must be positive: " + orderId);
                }
            } catch (NumberFormatException e) {
                throw new InvalidFixFormatException("Invalid OrderID, must be a positive number: " + orderId);
            }
            this.orderId = orderId;
            return this;
        }

        public Builder orderId(Long orderId) {
            if (orderId == null || orderId <= 0) {
                throw new InvalidFixFormatException("orderId (Tag 11) must be positive");
            }
            this.orderId = String.valueOf(orderId);
            return this;
        }

        public Builder sendingTime(String sendingTime) {
            if (sendingTime == null || sendingTime.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "sendingTime (Tag 52) must not be blank");
            }
            if (!sendingTime.matches("\\d{8}-\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "sendingTime must be in FIX format yyyyMMdd-HH:mm:ss[.SSS], got: " + sendingTime);
            }
            this.sendingTime = sendingTime;
            return this;
        }

        public Builder sendingTime(Date date) {
            if (date == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "sendingTime (Tag 52) date must not be null");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            this.sendingTime = sdf.format(date);
            return this;
        }

        public Builder bodyLength(String bodyLength) {
            if (bodyLength != null && !bodyLength.matches("\\d+")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "bodyLength (Tag 9) must be numeric, got: " + bodyLength);
            }
            this.bodyLength = bodyLength;
            return this;
        }

        public Builder symbol(String symbol) {
            if (symbol != null && symbol.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "symbol (Tag 55) must not be blank if specified");
            }
            this.symbol = symbol;
            return this;
        }

        public Builder orderQuantity(String orderQuantity) {
            if (orderQuantity != null && !orderQuantity.matches("\\d+(\\.\\d+)?")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "orderQuantity (Tag 38) must be numeric, got: " + orderQuantity);
            }
            this.orderQuantity = orderQuantity;
            return this;
        }

        public Builder orderQuantity(double orderQuantity) {
            if (orderQuantity <= 0) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "orderQuantity must be positive, got: " + orderQuantity);
            }
            this.orderQuantity = String.valueOf(orderQuantity);
            return this;
        }

        public Builder side(String side) {
            if (side != null && !side.matches("[12]")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "side (Tag 54) must be '1' (Buy) or '2' (Sell), got: " + side);
            }
            this.side = side;
            return this;
        }

        public Builder price(String price) {
            if (price != null && !price.matches("\\d+(\\.\\d+)?")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "price (Tag 44) must be numeric, got: " + price);
            }
            this.price = price;
            return this;
        }

        public Builder price(double price) {
            if (price <= 0) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "price must be positive, got: " + price);
            }
            this.price = String.valueOf(price);
            return this;
        }

        public Builder orderStatus(String orderStatus) {
            if (orderStatus != null && !orderStatus.matches("[28]")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "orderStatus (Tag 39) must be '2' (Executed) or '8' (Rejected), got: " + orderStatus);
            }
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder text(String text) {
            if (text != null && text.isBlank()) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "text (Tag 58) must not be blank if specified");
            }
            this.text = text;
            return this;
        }

        public Builder checksum(String checksum) {
            if (checksum != null && !checksum.matches("\\d{3}")) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "checksum (Tag 10) must be a 3-digit number, got: " + checksum);
            }
            this.checksum = checksum;
            return this;
        }

        private void validate() {
            if (beginString == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "beginString (Tag 8) is required");
            }
            if (messageType == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "messageType (Tag 35) is required");
            }
            if (senderId == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "senderId (Tag 49) is required");
            }
            if (targetId == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "targetId (Tag 56) is required");
            }
            if (sendingTime == null) {
                throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "sendingTime (Tag 52) is required");
            }
            switch (messageType) {
                case "3" -> {
                    if (text == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "text (Tag 58) is required when messageType is 3");
                    }
                }
                case "D" -> {
                    if (side == null || (!side.equals("1") && !side.equals("2"))) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "side (Tag 54) is missing or bad format when messageType is D");
                    }
                    if (symbol == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "symbol (Tag 55) is required");
                    }
                    if (orderQuantity == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "orderQty (Tag 38) is required");
                    }
                    if (price == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "price (Tag 44) is required");
                    }
                    if (orderId == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "price (Tag 11) is required");
                    }
                }
                case "8" -> {
                    if (orderStatus == null || (!orderStatus.equals("2") && !orderStatus.equals("8"))) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "ordStatus (Tag 39) is missing or bad format when messageType is 8");
                    }
                    if (orderId == null) {
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "price (Tag 11) is required");
                    }
                }
                case "A" -> {
                    // All good
                }
                default ->
                        throw new InvalidFixFormatException(Colors.RED + "Error: " + Colors.RESET + "messageType (Tag 35) must be '3' (error), 'D' (Buy/Sell) or '8' (Executed/Rejected)");
            }
        }

        private String calculateBodyLength() {
            StringBuilder sb = new StringBuilder();
            sb.append("35=").append(messageType).append(SOH);
            sb.append("49=").append(senderId).append(SOH);
            sb.append("56=").append(targetId).append(SOH);
            appendIfPresent(sb, "11", orderId);
            sb.append("52=").append(sendingTime).append(SOH);
            appendIfPresent(sb, "55", symbol);
            appendIfPresent(sb, "38", orderQuantity);
            appendIfPresent(sb, "54", side);
            appendIfPresent(sb, "44", price);
            appendIfPresent(sb, "39", orderStatus);
            appendIfPresent(sb, "58", text);
            return String.valueOf(sb.toString().getBytes(StandardCharsets.US_ASCII).length);
        }

        private String calculateChecksum() {
            StringBuilder sb = new StringBuilder();
            sb.append("8=").append(beginString).append(SOH);
            sb.append("9=").append(bodyLength).append(SOH);
            sb.append("35=").append(messageType).append(SOH);
            sb.append("49=").append(senderId).append(SOH);
            sb.append("56=").append(targetId).append(SOH);
            appendIfPresent(sb, "11", orderId);
            sb.append("52=").append(sendingTime).append(SOH);
            appendIfPresent(sb, "55", symbol);
            appendIfPresent(sb, "38", orderQuantity);
            appendIfPresent(sb, "54", side);
            appendIfPresent(sb, "44", price);
            appendIfPresent(sb, "39", orderStatus);
            appendIfPresent(sb, "58", text);
            int sum = 0;
            for (byte b : sb.toString().getBytes(StandardCharsets.US_ASCII)) {
                sum += b;
            }
            return String.format("%03d", sum % 256);
        }


        public FixBuilder build() {
            validate();
            if (bodyLength == null) {
                bodyLength = calculateBodyLength();
            }
            if (checksum == null) {
                checksum = calculateChecksum();
            }
            return new FixBuilder(this);
        }
    }
}
