package com.nguyen.fix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FixBuilderTest {
    private FixBuilder.Builder baseBuilder() {
        return new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("D")
                .senderId("123456")
                .targetId("654321")
                .sendingTime("20240101-12:00:00.000")
                .symbol("AAPL")
                .orderQuantity(100.0)
                .price(150.0)
                .side("1");
    }

    private FixBuilder.Builder executionBuilder() {
        return new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("8")
                .senderId("654321")
                .targetId("123456")
                .sendingTime("20240101-12:00:00.000")
                .symbol("AAPL")
                .orderQuantity(100.0)
                .price(150.0)
                .orderStatus("2");
    }

    private FixBuilder.Builder rejectBuilder() {
        return new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("3")
                .senderId("000001")
                .targetId("123456")
                .sendingTime("20240101-12:00:00.000")
                .symbol("AAPL")
                .orderQuantity(100.0)
                .price(150.0)
                .text("Checksum invalid");
    }

    @Test
    void testBuildOrderMessageSucceeds() {
        FixBuilder fix = baseBuilder().build();
        assertNotNull(fix);
        assertEquals("FIX.4.4", fix.getBeginString());
        assertEquals("D",        fix.getMessageType());
        assertEquals("123456",   fix.getSenderId());
        assertEquals("654321",   fix.getTargetId());
        assertEquals("1",        fix.getSide());
        assertEquals("AAPL",     fix.getSymbol());
        assertEquals("100.0",    fix.getOrderQuantity());
        assertEquals("150.0",    fix.getPrice());
    }

    @Test
    void testBuildExecutionReportSucceeds() {
        FixBuilder fix = executionBuilder().build();
        assertNotNull(fix);
        assertEquals("8", fix.getMessageType());
        assertEquals("2", fix.getOrderStatus());
    }

    @Test
    void testBuildRejectMessageSucceeds() {
        FixBuilder fix = rejectBuilder().build();
        assertNotNull(fix);
        assertEquals("3",                fix.getMessageType());
        assertEquals("Checksum invalid", fix.getText());
    }

    @Test
    void testBodyLengthAutoCalculated() {
        FixBuilder fix = baseBuilder().build();
        assertNotNull(fix.getBodyLength());
        assertTrue(Integer.parseInt(fix.getBodyLength()) > 0);
    }

    @Test
    void testChecksumAutoCalculated() {
        FixBuilder fix = baseBuilder().build();
        assertNotNull(fix.getChecksum());
        assertTrue(fix.getChecksum().matches("\\d{3}"));
    }

    @Test
    void testManualBodyLengthRespected() {
        FixBuilder fix = baseBuilder().bodyLength("999").build();
        assertEquals("999", fix.getBodyLength());
    }

    @Test
    void testManualChecksumRespected() {
        FixBuilder fix = baseBuilder().checksum("123").build();
        assertEquals("123", fix.getChecksum());
    }

    @Test
    void testFixMessageStartsWithTag8() {
        String msg = baseBuilder().build().getFixMessage();
        assertTrue(msg.startsWith("8=FIX.4.4\u0001"));
    }

    @Test
    void testFixMessageEndsWithTag10() {
        String msg = baseBuilder().build().getFixMessage();
        assertTrue(msg.contains("10="));
        assertTrue(msg.endsWith("\u0001"));
    }

    @Test
    void testFixMessageContainsRequiredTags() {
        String msg = baseBuilder().build().getFixMessage();
        assertTrue(msg.contains("8=FIX.4.4\u0001"));
        assertTrue(msg.contains("35=D\u0001"));
        assertTrue(msg.contains("49=123456\u0001"));
        assertTrue(msg.contains("56=654321\u0001"));
        assertTrue(msg.contains("52=20240101-12:00:00.000\u0001"));
        assertTrue(msg.contains("9="));
        assertTrue(msg.contains("10="));
    }

    @Test
    void testFixMessageOmitsAbsentOptionalFields() {
        String msg = rejectBuilder().build().getFixMessage();
        assertFalse(msg.contains("54="));
        assertFalse(msg.contains("39="));
    }

    @Test
    void testFixMessageIncludesPresentOptionalFields() {
        String msg = baseBuilder().text("Test note").build().getFixMessage();
        assertTrue(msg.contains("55=AAPL\u0001"));
        assertTrue(msg.contains("54=1\u0001"));
        assertTrue(msg.contains("44=150.0\u0001"));
        assertTrue(msg.contains("58=Test note\u0001"));
    }

    @Test
    void testFixMessageChecksumIsAlways3Digits() {
        String msg = baseBuilder().build().getFixMessage();
        int idx = msg.indexOf("10=") + 3;
        String val = msg.substring(idx, idx + 3);
        assertTrue(val.matches("\\d{3}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"FIX.4.2", "FIX.4.4", "FIX.5.0"})
    void testValidBeginStringAccepted(String v) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().beginString(v));
    }

    @ParameterizedTest
    @ValueSource(strings = {"fix.4.4", "FIX44", "4.4", "FIX.4", ""})
    void testInvalidBeginStringRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().beginString(v));
    }

    @Test
    void testNullBeginStringThrows() {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().beginString(null));
    }

    @Test
    void testValidSixDigitSenderIdAccepted() {
        assertDoesNotThrow(() -> new FixBuilder.Builder().senderId("123456"));
    }

    @Test
    void testInvalidSenderIdThrows() {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().senderId("ABC"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"20240101-12:00:00", "20240101-12:00:00.000"})
    void testValidSendingTimeAccepted(String v) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().sendingTime(v));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-01-01 12:00:00", "20240101", ""})
    void testInvalidSendingTimeRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().sendingTime(v));
    }

    @Test
    void testSendingTimeDateOverloadFormatsCorrectly() {
        FixBuilder fix = baseBuilder().sendingTime(new Date()).build();
        assertTrue(fix.getSendingTime().matches("\\d{8}-\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    @Test
    void testNullDateThrows() {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().sendingTime((Date) null));
    }

    @Test
    void testSideBuyAccepted() {
        assertDoesNotThrow(() -> new FixBuilder.Builder().side("1"));
    }

    @Test
    void testSideSellAccepted() {
        assertDoesNotThrow(() -> new FixBuilder.Builder().side("2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "3", "B", ""})
    void testInvalidSideRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().side(v));
    }

    @Test
    void testOrdStatusExecutedAccepted() {
        assertDoesNotThrow(() -> new FixBuilder.Builder().orderStatus("2"));
    }

    @Test
    void testOrdStatusRejectedAccepted() {
        assertDoesNotThrow(() -> new FixBuilder.Builder().orderStatus("8"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "X", ""})
    void testInvalidOrdStatusRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().orderStatus(v));
    }

    @Test
    void testPriceDoubleOverloadWorks() {
        FixBuilder fix = baseBuilder().price(99.99).build();
        assertEquals("99.99", fix.getPrice());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0})
    void testNonPositivePriceRejected(double v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().price(v));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "-1", ""})
    void testInvalidPriceStringRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().price(v));
    }

    @Test
    void testOrderQuantityDoubleOverloadWorks() {
        FixBuilder fix = baseBuilder().orderQuantity(500.0).build();
        assertEquals("500.0", fix.getOrderQuantity());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0})
    void testNonPositiveOrderQtyRejected(double v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().orderQuantity(v));
    }

    @ParameterizedTest
    @ValueSource(strings = {"000", "123", "255"})
    void testValidChecksumAccepted(String v) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().checksum(v));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "1234", "abc"})
    void testInvalidChecksumRejected(String v) {
        assertThrows(InvalidFixFormatException.class, () -> new FixBuilder.Builder().checksum(v));
    }

    @Test
    void testMissingBeginStringThrows() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .messageType("D").senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0).side("1")
                        .build());
    }

    @Test
    void testMissingMessageTypeThrows() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4").senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0).side("1")
                        .build());
    }

    @Test
    void testMissingSideForMsgTypeDThrows() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4").messageType("D")
                        .senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0)
                        .build());
    }

    @Test
    void testMissingOrdStatusForMsgType8Throws() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4").messageType("8")
                        .senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0)
                        .build());
    }

    @Test
    void testMissingTextForMsgType3Throws() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4").messageType("3")
                        .senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0)
                        .build());
    }

    @Test
    void testUnknownMessageTypeThrows() {
        assertThrows(InvalidFixFormatException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4").messageType("Z")
                        .senderId("123456").targetId("654321")
                        .sendingTime("20240101-12:00:00.000")
                        .symbol("AAPL").orderQuantity(10.0).price(1.0)
                        .build());
    }
}
