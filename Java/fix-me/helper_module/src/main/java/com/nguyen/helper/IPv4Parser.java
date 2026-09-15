package com.nguyen.helper;

public class IPv4Parser {
    public static boolean parseIPv4(String ipString) {
        if (ipString == null ||
                !ipString.matches("^(?:0|[1-9]\\d{0,2})(?:\\.(?:0|[1-9]\\d{0,2})){3}$")) {
            return false;
        }

        String[] octets = ipString.split("\\.");
        for (String octet : octets) {
            if (Integer.parseInt(octet) > 255) {
                return false;
            }
        }
        return true;
    }
}
