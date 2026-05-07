package com.nguyen.fix;

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
    private final String sequenceNumber;
    private final String sendingTime;
    private final String symbol;
    private final String orderQuantity;
    private final String side;
    private final String price;
    private final String orderId;
    private final String checksum;

    private static final char DELIMITER = '\u0001';

    private FixBuilder(Builder builder) {
        this.beginString = builder.beginString;
        this.bodyLength = builder.bodyLength;
        this.messageType = builder.messageType;
        this.senderId = builder.senderId;
        this.targetId = builder.targetId;
        this.sequenceNumber = builder.sequenceNumber;
        this.sendingTime = builder.sendingTime;
        this.symbol = builder.symbol;
        this.orderQuantity = builder.orderQuantity;
        this.side = builder.side;
        this.price = builder.price;
        this.orderId = builder.orderId;
        this.checksum = builder.checksum;
    }

    public String getFixMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("8=").append(beginString).append(DELIMITER);
        sb.append("9=").append(bodyLength).append(DELIMITER);
        sb.append("35=").append(messageType).append(DELIMITER);
        sb.append("49=").append(senderId).append(DELIMITER);
        sb.append("56=").append(targetId).append(DELIMITER);
        sb.append("34=").append(sequenceNumber).append(DELIMITER);
        sb.append("52=").append(sendingTime).append(DELIMITER);
        appendIfPresent(sb, "55", symbol);
        appendIfPresent(sb, "38", orderQuantity);
        appendIfPresent(sb, "54", side);
        appendIfPresent(sb, "44", price);
        appendIfPresent(sb, "37", orderId);
        sb.append("10=").append(checksum).append(DELIMITER);
        return sb.toString();
    }

    private static void appendIfPresent(StringBuilder sb, String tag, String value) {
        if (value != null) {
            sb.append(tag).append("=").append(value).append(DELIMITER);
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
    public String getSequenceNumber() {
        return sequenceNumber;
    }
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
    public String getOrderId() {
        return orderId;
    }
    public String getChecksum() {
        return checksum;
    }

    public static class Builder {
        private String beginString;
        private String messageType;
        private String senderId;
        private String targetId;
        private String sequenceNumber;
        private String sendingTime;
        private String bodyLength;
        private String symbol;
        private String orderQuantity;
        private String side;
        private String price;
        private String orderId;
        private String checksum;

        public Builder beginString(String beginString) {
            if (beginString == null || beginString.isBlank()) {
                throw new IllegalArgumentException("beginString (Tag 8) must not be blank");
            }
            if (!beginString.matches("FIX\\.\\d+\\.\\d+")) {
                throw new IllegalArgumentException("beginString must be in format FIX.X.X, got: " + beginString);
            }
            this.beginString = beginString;
            return this;
        }

        public Builder messageType(String messageType) {
            if (messageType == null || messageType.isBlank()) {
                throw new IllegalArgumentException("messageType (Tag 35) must not be blank");
            }
            this.messageType = messageType;
            return this;
        }

        public Builder senderId(String senderId) {
            if (senderId == null || senderId.isBlank()) {
                throw new IllegalArgumentException("senderId (Tag 49) must not be blank");
            }
            this.senderId = senderId;
            return this;
        }

        public Builder targetId(String targetId) {
            if (targetId == null || targetId.isBlank()) {
                throw new IllegalArgumentException("targetId (Tag 56) must not be blank");
            }
            this.targetId = targetId;
            return this;
        }

        public Builder sequenceNumber(String sequenceNumber) {
            if (sequenceNumber == null || sequenceNumber.isBlank()) {
                throw new IllegalArgumentException("sequenceNumber (Tag 34) must not be blank");
            }
            if (!sequenceNumber.matches("\\d+") || Integer.parseInt(sequenceNumber) <= 0) {
                throw new IllegalArgumentException("sequenceNumber must be a positive integer, got: " + sequenceNumber);
            }
            this.sequenceNumber = sequenceNumber;
            return this;
        }

        public Builder sequenceNumber(int sequenceNumber) {
            if (sequenceNumber <= 0) {
                throw new IllegalArgumentException("sequenceNumber must be a positive integer, got: " + sequenceNumber);
            }
            this.sequenceNumber = String.valueOf(sequenceNumber);
            return this;
        }

        public Builder sendingTime(String sendingTime) {
            if (sendingTime == null || sendingTime.isBlank()) {
                throw new IllegalArgumentException("sendingTime (Tag 52) must not be blank");
            }
            if (!sendingTime.matches("\\d{8}-\\d{2}:\\d{2}:\\d{2}(\\.\\d{3})?")) {
                throw new IllegalArgumentException("sendingTime must be in FIX format yyyyMMdd-HH:mm:ss[.SSS], got: " + sendingTime);
            }
            this.sendingTime = sendingTime;
            return this;
        }

        public Builder sendingTime(Date date) {
            if (date == null) {
                throw new IllegalArgumentException("sendingTime (Tag 52) date must not be null");
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            this.sendingTime = sdf.format(date);
            return this;
        }

        public Builder bodyLength(String bodyLength) {
            if (bodyLength != null && !bodyLength.matches("\\d+")) {
                throw new IllegalArgumentException("bodyLength (Tag 9) must be numeric, got: " + bodyLength);
            }
            this.bodyLength = bodyLength;
            return this;
        }

        public Builder symbol(String symbol) {
            if (symbol != null && symbol.isBlank()) {
                throw new IllegalArgumentException("symbol (Tag 55) must not be blank");
            }
            this.symbol = symbol;
            return this;
        }

        public Builder orderQuantity(String orderQuantity) {
            if (orderQuantity != null && !orderQuantity.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("orderQuantity (Tag 38) must be numeric, got: " + orderQuantity);
            }
            this.orderQuantity = orderQuantity;
            return this;
        }

        public Builder orderQuantity(double orderQuantity) {
            if (orderQuantity <= 0) {
                throw new IllegalArgumentException("orderQuantity must be positive, got: " + orderQuantity);
            }
            this.orderQuantity = String.valueOf(orderQuantity);
            return this;
        }

        public Builder side(String side) {
            if (side != null && !side.matches("[12]")) {
                throw new IllegalArgumentException("side (Tag 54) must be '1' (Buy) or '2' (Sell), got: " + side);
            }
            this.side = side;
            return this;
        }

        public Builder price(String price) {
            if (price != null && !price.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("price (Tag 44) must be numeric, got: " + price);
            }
            this.price = price;
            return this;
        }

        public Builder price(double price) {
            if (price <= 0) {
                throw new IllegalArgumentException("price must be positive, got: " + price);
            }
            this.price = String.valueOf(price);
            return this;
        }

        public Builder orderId(String orderId) {
            if (orderId != null && orderId.isBlank()) {
                throw new IllegalArgumentException("orderId (Tag 37) must not be blank");
            }
            this.orderId = orderId;
            return this;
        }

        public Builder checksum(String checksum) {
            if (checksum != null && !checksum.matches("\\d{3}")) {
                throw new IllegalArgumentException("checksum (Tag 10) must be a 3-digit number, got: " + checksum);
            }
            this.checksum = checksum;
            return this;
        }

        private void validate() {
            if (beginString == null) {
                throw new IllegalStateException("beginString (Tag 8) is required");
            }
            if (messageType == null) {
                throw new IllegalStateException("messageType (Tag 35) is required");
            }
            if (senderId == null) {
                throw new IllegalStateException("senderId (Tag 49) is required");
            }
            if (targetId == null) {
                throw new IllegalStateException("targetId (Tag 56) is required");
            }
            if (sequenceNumber == null) {
                throw new IllegalStateException("sequenceNumber (Tag 34) is required");
            }
            if (sendingTime == null) {
                throw new IllegalStateException("sendingTime (Tag 52) is required");
            }
        }

        private String calculateBodyLength() {
            StringBuilder sb = new StringBuilder();
            sb.append("35=").append(messageType).append(DELIMITER);
            sb.append("49=").append(senderId).append(DELIMITER);
            sb.append("56=").append(targetId).append(DELIMITER);
            sb.append("34=").append(sequenceNumber).append(DELIMITER);
            sb.append("52=").append(sendingTime).append(DELIMITER);
            appendIfPresent(sb, "55", symbol);
            appendIfPresent(sb, "38", orderQuantity);
            appendIfPresent(sb, "54", side);
            appendIfPresent(sb, "44", price);
            appendIfPresent(sb, "37", orderId);
            return String.valueOf(sb.toString().getBytes(StandardCharsets.US_ASCII).length);
        }

        private String calculateChecksum() {
            StringBuilder sb = new StringBuilder();
            sb.append("8=").append(beginString).append(DELIMITER);
            sb.append("9=").append(bodyLength).append(DELIMITER);
            sb.append("35=").append(messageType).append(DELIMITER);
            sb.append("49=").append(senderId).append(DELIMITER);
            sb.append("56=").append(targetId).append(DELIMITER);
            sb.append("34=").append(sequenceNumber).append(DELIMITER);
            sb.append("52=").append(sendingTime).append(DELIMITER);
            appendIfPresent(sb, "55", symbol);
            appendIfPresent(sb, "38", orderQuantity);
            appendIfPresent(sb, "54", side);
            appendIfPresent(sb, "44", price);
            appendIfPresent(sb, "37", orderId);
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
