package com.nguyen.fix.chains;

import com.nguyen.fix.FixTag;
import com.nguyen.fix.InvalidFixFormatException;

import java.util.Map;

public interface Chain {
    void handle(String rawFix, Map<FixTag, String> fields) throws InvalidFixFormatException;
}
