package com.nguyen.scorpion.parser;

public class EndianReader {
    public static int readShort(byte[] data, int pos, boolean le) {
        if (pos < 0 || pos + 2 > data.length) {
            return 0;
        }
        if (le) {
            return (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8);
        }
        return ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
    }

    public static int readInt(byte[] data, int pos, boolean le) {
        if (pos < 0 || pos + 4 > data.length) {
            return 0;
        }
        if (le) {
            return (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8)
                    | ((data[pos + 2] & 0xFF) << 16) | ((data[pos + 3] & 0xFF) << 24);
        }
        return ((data[pos] & 0xFF) << 24) | ((data[pos + 1] & 0xFF) << 16)
                | ((data[pos + 2] & 0xFF) << 8) | (data[pos + 3] & 0xFF);
    }
}
