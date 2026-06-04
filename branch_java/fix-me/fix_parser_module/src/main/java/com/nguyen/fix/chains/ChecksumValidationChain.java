package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ChecksumValidationChain extends ChainAbstract {
    @Override
    public void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException {
        try {
            int receivedChecksum = Integer.parseInt(fields.get(FixTag.CHECKSUM));
            int checksumIndex = rawFix.lastIndexOf("10=");
            if (checksumIndex <= 0) {
                throw new InvalidFixFormatException("No Checksum field");
            }
            byte[] messageBytes = rawFix.substring(0, checksumIndex).getBytes(StandardCharsets.US_ASCII);
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

        if (next != null) {
            next.handle(rawFix, fields);
        }
    }
}
