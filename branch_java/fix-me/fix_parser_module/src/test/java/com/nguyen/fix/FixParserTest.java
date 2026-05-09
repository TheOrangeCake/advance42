package com.nguyen.fix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.Map;

public class FixParserTest {
    private static final char SOH = '\u0001';

    private String buildMessage(String msgType, String sender, String target, String extra) {
        String body = "35=" + msgType + SOH
                + "49=" + sender + SOH
                + "56=" + target + SOH
                + "52=20240101-12:00:00.000" + SOH
                + "55=AAPL" + SOH
                + "38=100" + SOH
                + "44=150.0" + SOH
                + (extra != null ? extra : "");

        int bodyLen = body.getBytes().length;
        String head = "8=FIX.4.4" + SOH + "9=" + bodyLen + SOH;
        String full = head + body;

        int sum = 0;
        for (byte b : full.getBytes()) sum += (b & 0xFF);
        return full + "10=" + String.format("%03d", sum % 256) + SOH;
    }

    private String validOrder() {
        return buildMessage("D", "123456", "654321", "54=1" + SOH);
    }

    private String validExecution() {
        return buildMessage("8", "654321", "123456", "39=2" + SOH);
    }

    @Test
    void testValidOrderMessageParsesSuccessfully() {
        Map<FixTag, String> fields = FixParser.parse(validOrder());
        Assertions.assertEquals("FIX.4.4", fields.get(FixTag.BEGIN_STRING));
        Assertions.assertEquals("D",       fields.get(FixTag.MSG_TYPE));
        Assertions.assertEquals("123456",  fields.get(FixTag.SENDER_COMP_ID));
        Assertions.assertEquals("654321",  fields.get(FixTag.TARGET_COMP_ID));
        Assertions.assertEquals("AAPL",    fields.get(FixTag.SYMBOL));
        Assertions.assertEquals("1",       fields.get(FixTag.SIDE));
    }

    @Test
    void testValidExecutionReportParsesSuccessfully() {
        Map<FixTag, String> fields = FixParser.parse(validExecution());
        Assertions.assertEquals("8", fields.get(FixTag.MSG_TYPE));
        Assertions.assertEquals("2", fields.get(FixTag.ORD_STATUS));
    }

    @Test
    void testAllRequiredTagsPresentAfterParse() {
        Map<FixTag, String> fields = FixParser.parse(validOrder());
        Assertions.assertTrue(fields.containsKey(FixTag.BEGIN_STRING));
        Assertions.assertTrue(fields.containsKey(FixTag.BODY_LENGTH));
        Assertions.assertTrue(fields.containsKey(FixTag.MSG_TYPE));
        Assertions.assertTrue(fields.containsKey(FixTag.SENDER_COMP_ID));
        Assertions.assertTrue(fields.containsKey(FixTag.TARGET_COMP_ID));
        Assertions.assertTrue(fields.containsKey(FixTag.SENDING_TIME));
        Assertions.assertTrue(fields.containsKey(FixTag.SYMBOL));
        Assertions.assertTrue(fields.containsKey(FixTag.ORDER_QTY));
        Assertions.assertTrue(fields.containsKey(FixTag.PRICE));
        Assertions.assertTrue(fields.containsKey(FixTag.CHECKSUM));
    }


    @Test
    void testNullMessageThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(null));
    }

    @Test
    void testEmptyMessageThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(""));
    }

    @Test
    void testMissingSenderThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH)
                .replace("49=123456" + SOH, "");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMissingTargetThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH)
                .replace("56=654321" + SOH, "");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMissingSideForMsgTypeDThrows() {
        String msg = buildMessage("D", "123456", "654321", null);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMissingOrdStatusForMsgType8Throws() {
        String msg = buildMessage("8", "654321", "123456", null);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMissingSymbolThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH)
                .replace("55=AAPL" + SOH, "");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMissingEqualsSignThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH)
                .replace("49=123456", "49NODLM123456");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testUnknownTagThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse("8=FIX.4.4" + SOH + "9999=SOMETHING" + SOH));
    }

    @Test
    void testEmptyValueThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse("8=FIX.4.4" + SOH + "49=" + SOH));
    }

    @Test
    void testNonNumericTagThrows() {
        Assertions.assertThrows(InvalidFixFormatException.class,
                () -> FixParser.parse("abc=FIX.4.4" + SOH));
    }

    @Test
    void testDuplicateTagThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH + "55=MSFT" + SOH);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testInvalidBeginStringThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH)
                .replace("8=FIX.4.4", "8=FIX.3.0");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testNonSixDigitSenderIdThrows() {
        String msg = buildMessage("D", "ABC", "654321", "54=1" + SOH);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testNonSixDigitTargetIdThrows() {
        String msg = buildMessage("D", "123456", "XYZ", "54=1" + SOH);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testInvalidSideValueThrows() {
        String msg = buildMessage("D", "123456", "654321", "54=9" + SOH);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testInvalidOrdStatusValueThrows() {
        String msg = buildMessage("8", "654321", "123456", "39=5" + SOH);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testUnknownMsgTypeThrows() {
        String msg = buildMessage("Z", "123456", "654321", null);
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testBeginStringNotFirstThrows() {
        String msg = "35=D" + SOH + "8=FIX.4.4" + SOH + "9=5" + SOH + "10=000" + SOH;
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testBodyLengthNotSecondThrows() {
        String msg = "8=FIX.4.4" + SOH + "35=D" + SOH + "9=3" + SOH + "10=000" + SOH;
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testMsgTypeNotThirdThrows() {
        String msg = "8=FIX.4.4" + SOH + "9=20" + SOH + "49=123456" + SOH + "35=D" + SOH + "10=000" + SOH;
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testChecksumNotLastThrows() {
        String msg = "8=FIX.4.4" + SOH + "9=5" + SOH + "10=000" + SOH + "35=D" + SOH;
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testWrongChecksumThrows() {
        String msg = validOrder();
        String corrupted = msg.substring(0, msg.lastIndexOf("10=")) + "10=000" + SOH;
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(corrupted));
    }

    @Test
    void testWrongBodyLengthThrows() {
        String msg = validOrder().replaceFirst("9=\\d+", "9=999");
        Assertions.assertThrows(InvalidFixFormatException.class, () -> FixParser.parse(msg));
    }

    @Test
    void testOptionalTextFieldParsedCorrectly() {
        String msg = buildMessage("D", "123456", "654321", "54=1" + SOH + "58=Test error" + SOH);
        Map<FixTag, String> fields = FixParser.parse(msg);
        Assertions.assertEquals("Test error", fields.get(FixTag.TEXT));
    }
}
