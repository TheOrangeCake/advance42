package com.nguyen.scorpion.parser;

import com.nguyen.scorpion.model.ExifTag;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.nguyen.scorpion.parser.EndianReader.readInt;
import static com.nguyen.scorpion.parser.EndianReader.readShort;

public class TiffParser {
    private static final int TIFF_MAGIC = 42;
    private static final int TAG_EXIF_SUB_IFD = 0x8769;
    private static final int TAG_GPS_IFD = 0x8825;

    private static final int TYPE_BYTE = 1;
    private static final int TYPE_ASCII = 2;
    private static final int TYPE_SHORT = 3;
    private static final int TYPE_LONG = 4;
    private static final int TYPE_RATIONAL = 5;
    private static final int TYPE_UNDEFINED = 7;
    private static final int TYPE_SLONG = 9;
    private static final int TYPE_SRATIONAL = 10;

    private TiffParser() {

    }

    public static void parse(byte[] data, int tiffBase, Map<ExifTag, String> metadata) {
        if (tiffBase + 8 > data.length) {
            return;
        }

        int byteOrder = readShort(data, tiffBase, false);
        boolean le;
        if (byteOrder == 0x4949) {
            le = true;
        } else if (byteOrder == 0x4D4D) {
            le = false;
        } else {
            return;
        }

        if (readShort(data, tiffBase + 2, le) != TIFF_MAGIC) {
            return;
        }

        int ifd0Offset = readInt(data, tiffBase + 4, le);

        parseIFD(data, tiffBase + ifd0Offset, tiffBase, le, metadata);
    }

    private static void parseIFD(byte[] data, int position, int tiffBase, boolean le, Map<ExifTag, String> metadata) {
        if (position < 0 || position + 2 > data.length) {
            return;
        }

        int entryCount = readShort(data, position, le);
        position += 2;

        for (int i = 0; i < entryCount; i++) {
            int entry = position + i * 12;
            if (entry + 12 > data.length) {
                break;
            }
            int tagId = readShort(data, entry, le);
            int type = readShort(data, entry + 2, le);
            int count = readInt(data, entry + 4, le);
            int valueFieldOffset = entry + 8;

            if (tagId == TAG_EXIF_SUB_IFD) {
                parseIFD(data, tiffBase + readInt(data, valueFieldOffset, le), tiffBase, le, metadata);
            } else if (tagId == TAG_GPS_IFD) {
                parseIFD(data, tiffBase + readInt(data, valueFieldOffset, le), tiffBase, le, metadata);
            } else {
                ExifTag tag = ExifTag.fromId(tagId);
                if (tag != null) {
                    String value = decodeValue(data, type, count, valueFieldOffset, tiffBase, le);
                    if (value != null) {
                        metadata.put(tag, value);
                    }
                }
            }
        }
    }

    private static String decodeValue(byte[] data, int type, int count, int valueFieldOffset, int tiffBase, boolean le) {
        if (count < 0) {
            return null;
        }

        int typeSize = typeSize(type);
        if (typeSize == 0) {
            return null;
        }

        long totalSize = (long) typeSize * count;
        int dataOffset = totalSize <= 4 ? valueFieldOffset : tiffBase + readInt(data, valueFieldOffset, le);
        if (dataOffset < 0 || dataOffset + totalSize > data.length) {
            return null;
        }

        return switch (type) {
            case TYPE_ASCII -> {
                int len = count;
                while (len > 0 && data[dataOffset + len - 1] == 0) len--;
                yield new String(data, dataOffset, len, StandardCharsets.US_ASCII).trim();
            }
            case TYPE_BYTE, TYPE_SHORT, TYPE_LONG, TYPE_SLONG -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    int off = dataOffset + i * typeSize;
                    long v = switch (type) {
                        case TYPE_BYTE  -> data[off] & 0xFF;
                        case TYPE_SHORT -> readShort(data, off, le);
                        default -> readInt(data, off, le);
                    };
                    sb.append(v);
                }
                yield sb.toString();
            }
            case TYPE_RATIONAL, TYPE_SRATIONAL -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    int off = dataOffset + i * 8;
                    sb.append(readInt(data, off, le)).append('/').append(readInt(data, off + 4, le));
                }
                yield sb.toString();
            }
            case TYPE_UNDEFINED -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    sb.append(String.format("%02X", data[dataOffset + i] & 0xFF));
                }
                yield sb.toString();
            }
            default -> null;
        };
    }

    private static int typeSize(int type) {
        return switch (type) {
            case TYPE_BYTE, TYPE_ASCII, TYPE_UNDEFINED -> 1;
            case TYPE_SHORT -> 2;
            case TYPE_LONG, TYPE_SLONG -> 4;
            case TYPE_RATIONAL, TYPE_SRATIONAL -> 8;
            default -> 0;
        };
    }
}
