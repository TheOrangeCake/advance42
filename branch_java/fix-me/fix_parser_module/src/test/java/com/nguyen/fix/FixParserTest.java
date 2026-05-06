package com.nguyen.fix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.Map;

public class FixParserTest {
    private static final char DELIMITER = '\u0001';

    private String buildValidMessage(String msgType, String sender, String target, String extra) {
        String body = "35=" + msgType + DELIMITER
                + "49=" + sender + DELIMITER
                + "56=" + target + DELIMITER
                + (extra != null ? extra : "");

        int bodyLength = body.length();

        String head = "8=FIX.4.4" + DELIMITER + "9=" + bodyLength + DELIMITER;
        String full = head + body;

        int sum = 0;
        for (byte b : full.getBytes()) sum += (b & 0xFF);
        String checksum = String.format("%03d", sum % 256);

        return full + "10=" + checksum + DELIMITER;
    }

    @Test
    void testValidMessageParsesSuccessfully() {
        String message = buildValidMessage("D", "BROKER_01", "MARKET_01", null);
        Map<FixTag, String> fields = FixParser.parse(message);

        Assertions.assertEquals("FIX.4.4", fields.get(FixTag.BEGIN_STRING));
        Assertions.assertEquals("D",         fields.get(FixTag.MSG_TYPE));
        Assertions.assertEquals("BROKER_01", fields.get(FixTag.SENDER_COMP_ID));
        Assertions.assertEquals("MARKET_01", fields.get(FixTag.TARGET_COMP_ID));
    }

    @Test
    void testValidMessageReturnsAllRequiredTags() {
        String message = buildValidMessage("0", "BROKER_01", "MARKET_01", null);
        Map<FixTag, String> fields = FixParser.parse(message);

        Assertions.assertTrue(fields.containsKey(FixTag.BEGIN_STRING));
        Assertions.assertTrue(fields.containsKey(FixTag.BODY_LENGTH));
        Assertions.assertTrue(fields.containsKey(FixTag.MSG_TYPE));
        Assertions.assertTrue(fields.containsKey(FixTag.SENDER_COMP_ID));
        Assertions.assertTrue(fields.containsKey(FixTag.TARGET_COMP_ID));
        Assertions.assertTrue(fields.containsKey(FixTag.CHECKSUM));
    }

    @Test
    void testNullMessageThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(null));
    }

    @Test
    void testEmptyMessageThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(""));
    }

    // Without tag 49 SENDER_COMP_ID
    @Test
    void testMissingSenderThrows() {
        String body = "35=D" + DELIMITER + "56=MARKET_01" + DELIMITER;
        int bodyLength = body.length();
        String full = "8=FIX.4.4" + DELIMITER + "9=" + bodyLength + DELIMITER + body;
        int sum = 0;
        for (byte b : full.getBytes()) sum += (b & 0xFF);
        String message = full + "10=" + String.format("%03d", sum % 256) + DELIMITER;

        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    // Without tag 56 TARGET_COMP_ID
    @Test
    void testMissingTargetThrows() {
        String body = "35=D" + DELIMITER + "49=BROKER_01" + DELIMITER;
        int bodyLength = body.length();
        String full = "8=FIX.4.4" + DELIMITER + "9=" + bodyLength + DELIMITER + body;
        int sum = 0;
        for (byte b : full.getBytes()) sum += (b & 0xFF);
        String message = full + "10=" + String.format("%03d", sum % 256) + DELIMITER;

        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testMissingEqualsSignThrows() {
        String body = "35=D" + DELIMITER + "49BROKER_01" + DELIMITER + "56=MARKET_01" + DELIMITER;
        int bodyLength = body.length();
        String full = "8=FIX.4.4" + DELIMITER + "9=" + bodyLength + DELIMITER + body;
        int sum = 0;
        for (byte b : full.getBytes()) sum += (b & 0xFF);
        String message = full + "10=" + String.format("%03d", sum % 256) + DELIMITER;

        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testUnknownTagThrows() {
        String message = "8=FIX.4.4" + DELIMITER + "9999=SOMETHING" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testEmptyValueThrows() {
        String message = "8=FIX.4.4" + DELIMITER + "49=" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testNonNumericTagThrows() {
        String message = "abc=FIX.4.4" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }


    // Tag 35 before tag 8
    @Test
    void testBeginStringNotFirstThrows() {
        String message = "35=D" + DELIMITER + "8=FIX.4.4" + DELIMITER + "9=5" + DELIMITER + "10=000" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testChecksumNotLastThrows() {
        String message = "8=FIX.4.4" + DELIMITER + "9=5" + DELIMITER + "10=000" + DELIMITER + "35=D" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(message));
    }

    @Test
    void testWrongChecksumThrows() {
        String message = buildValidMessage("D", "BROKER_01", "MARKET_01", null);
        // Replacing checksum
        String corrupted = message.substring(0, message.lastIndexOf("10=")) + "10=000" + DELIMITER;
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(corrupted));
    }

    @Test
    void testWrongBodyLengthThrows() {
        String message = buildValidMessage("D", "BROKER_01", "MARKET_01", null);
        // Replace body length
        String corrupted = message.replaceFirst("9=\\d+", "9=999");
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse(corrupted));
    }
}
