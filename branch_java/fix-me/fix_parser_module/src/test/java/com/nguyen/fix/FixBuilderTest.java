package com.nguyen.fix;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FixBuilderTest {
    private FixBuilder.Builder validBuilder() {
        return new FixBuilder.Builder()
                .beginString("FIX.4.4")
                .messageType("D")
                .senderId("CLIENT1")
                .targetId("BROKER1")
                .sequenceNumber(1)
                .sendingTime("20240101-12:00:00.000");
    }

    @Test
    void testBuildWithRequiredFieldsSucceeds() {
        FixBuilder fix = validBuilder().build();

        assertNotNull(fix);
        assertEquals("FIX.4.4", fix.getBeginString());
        assertEquals("D", fix.getMessageType());
        assertEquals("CLIENT1", fix.getSenderId());
        assertEquals("BROKER1", fix.getTargetId());
        assertEquals("1", fix.getSequenceNumber());
        assertEquals("20240101-12:00:00.000", fix.getSendingTime());
    }

    @Test
    void testBuildWithAllFieldsSucceeds() {
        FixBuilder fix = validBuilder()
                .symbol("AAPL")
                .orderQuantity(100.0)
                .side("1")
                .price(150.00)
                .orderId("ORD-001")
                .build();

        assertEquals("AAPL", fix.getSymbol());
        assertEquals("100.0", fix.getOrderQuantity());
        assertEquals("1", fix.getSide());
        assertEquals("150.0", fix.getPrice());
        assertEquals("ORD-001", fix.getOrderId());
    }

    @Test
    void testBodyLengthAutoCalculatedWhenNotProvided() {
        FixBuilder fix = validBuilder().build();

        assertNotNull(fix.getBodyLength());
        assertTrue(Integer.parseInt(fix.getBodyLength()) > 0);
    }

    @Test
    void testChecksumAutoCalculatedWhenNotProvided() {
        FixBuilder fix = validBuilder().build();

        assertNotNull(fix.getChecksum());
        assertTrue(fix.getChecksum().matches("\\d{3}"));
    }

    @Test
    void testManualBodyLengthIsRespected() {
        FixBuilder fix = validBuilder().bodyLength("999").build();

        assertEquals("999", fix.getBodyLength());
    }

    @Test
    void testManualChecksumIsRespected() {
        FixBuilder fix = validBuilder().checksum("123").build();

        assertEquals("123", fix.getChecksum());
    }

    @Test
    void testGetFixMessageStartsWithTag8EndsWithTag10() {
        String msg = validBuilder().build().getFixMessage();

        assertTrue(msg.startsWith("8=FIX.4.4\u0001"));
        assertTrue(msg.contains("10="));
        assertTrue(msg.endsWith("\u0001"));
    }

    @Test
    void testGetFixMessageContainsAllRequiredTags() {
        String msg = validBuilder().build().getFixMessage();

        assertTrue(msg.contains("8=FIX.4.4\u0001"));
        assertTrue(msg.contains("35=D\u0001"));
        assertTrue(msg.contains("49=CLIENT1\u0001"));
        assertTrue(msg.contains("56=BROKER1\u0001"));
        assertTrue(msg.contains("34=1\u0001"));
        assertTrue(msg.contains("52=20240101-12:00:00.000\u0001"));
        assertTrue(msg.contains("9="));
        assertTrue(msg.contains("10="));
    }

    @Test
    void testGetFixMessageOmitsAbsentOptionalFields() {
        String msg = validBuilder().build().getFixMessage();

        assertFalse(msg.contains("55="));
        assertFalse(msg.contains("38="));
        assertFalse(msg.contains("54="));
        assertFalse(msg.contains("44="));
        assertFalse(msg.contains("37="));
    }

    @Test
    void testGetFixMessageIncludesPresentOptionalFields() {
        String msg = validBuilder()
                .symbol("AAPL")
                .side("1")
                .price(150.0)
                .build()
                .getFixMessage();

        assertTrue(msg.contains("55=AAPL\u0001"));
        assertTrue(msg.contains("54=1\u0001"));
        assertTrue(msg.contains("44=150.0\u0001"));
    }

    @Test
    void testGetFixMessageChecksumIsAlways3Digits() {
        String msg = validBuilder().build().getFixMessage();

        int idx = msg.indexOf("10=") + 3;
        String checksumValue = msg.substring(idx, idx + 3);
        assertTrue(checksumValue.matches("\\d{3}"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"FIX.4.2", "FIX.4.4", "FIX.5.0"})
    void testValidBeginStringFormatsAccepted(String value) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().beginString(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"fix.4.4", "FIX44", "4.4", "FIX.4", ""})
    void testInvalidBeginStringFormatsRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().beginString(value));
    }

    @Test
    void testNullBeginStringThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().beginString(null));
    }

    @Test
    void testSequenceNumberIntOverloadWorks() {
        FixBuilder fix = validBuilder().sequenceNumber(42).build();
        assertEquals("42", fix.getSequenceNumber());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100})
    void testNonPositiveSequenceNumberIntRejected(int value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().sequenceNumber(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "abc", ""})
    void testInvalidSequenceNumberStringRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().sequenceNumber(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"20240101-12:00:00", "20240101-12:00:00.000"})
    void testValidSendingTimeFormatsAccepted(String value) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().sendingTime(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2024-01-01 12:00:00", "20240101", "12:00:00", ""})
    void testInvalidSendingTimeFormatsRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().sendingTime(value));
    }

    @Test
    void testSendingTimeDateOverloadFormatsCorrectly() {
        FixBuilder fix = validBuilder().sendingTime(new Date()).build();
        assertTrue(fix.getSendingTime().matches("\\d{8}-\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));
    }

    @Test
    void testSendingTimeNullDateThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().sendingTime((Date) null));
    }

    @Test
    void testSideBuyAccepted() {
        assertDoesNotThrow(() -> validBuilder().side("1").build());
    }

    @Test
    void testSideSellAccepted() {
        assertDoesNotThrow(() -> validBuilder().side("2").build());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "3", "B", "BUY", ""})
    void testInvalidSideValuesRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().side(value));
    }

    @Test
    void testPriceDoubleOverloadWorks() {
        FixBuilder fix = validBuilder().price(99.99).build();
        assertEquals("99.99", fix.getPrice());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -100.50})
    void testNonPositivePriceRejected(double value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().price(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "-1", "12.34.56", ""})
    void testInvalidPriceStringRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().price(value));
    }

    @Test
    void testOrderQuantityDoubleOverloadWorks() {
        FixBuilder fix = validBuilder().orderQuantity(500.0).build();
        assertEquals("500.0", fix.getOrderQuantity());
    }

    @Test
    void testTextWorks() {
        FixBuilder fix = validBuilder().text("Syntax error").build();
        assertEquals("Syntax error", fix.getText());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0})
    void testNonPositiveOrderQuantityRejected(double value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().orderQuantity(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"000", "123", "255"})
    void testValidChecksumValuesAccepted(String value) {
        assertDoesNotThrow(() -> new FixBuilder.Builder().checksum(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "1234", "abc"})
    void testInvalidChecksumValuesRejected(String value) {
        assertThrows(IllegalArgumentException.class,
                () -> new FixBuilder.Builder().checksum(value));
    }

    @Test
    void testMissingBeginStringThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .messageType("D")
                        .senderId("CLIENT1")
                        .targetId("BROKER1")
                        .sequenceNumber(1)
                        .sendingTime("20240101-12:00:00.000")
                        .build());
    }

    @Test
    void testMissingMessageTypeThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .senderId("CLIENT1")
                        .targetId("BROKER1")
                        .sequenceNumber(1)
                        .sendingTime("20240101-12:00:00.000")
                        .build());
    }

    @Test
    void testMissingSenderIdThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("D")
                        .targetId("BROKER1")
                        .sequenceNumber(1)
                        .sendingTime("20240101-12:00:00.000")
                        .build());
    }

    @Test
    void testMissingTargetIdThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("D")
                        .senderId("CLIENT1")
                        .sequenceNumber(1)
                        .sendingTime("20240101-12:00:00.000")
                        .build());
    }

    @Test
    void testMissingSequenceNumberThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("D")
                        .senderId("CLIENT1")
                        .targetId("BROKER1")
                        .sendingTime("20240101-12:00:00.000")
                        .build());
    }

    @Test
    void testMissingSendingTimeThrows() {
        assertThrows(IllegalStateException.class, () ->
                new FixBuilder.Builder()
                        .beginString("FIX.4.4")
                        .messageType("D")
                        .senderId("CLIENT1")
                        .targetId("BROKER1")
                        .sequenceNumber(1)
                        .build());
    }
}
