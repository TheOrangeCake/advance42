package com.nguyen.scorpion.parser;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ExifTag;
import com.nguyen.scorpion.model.ImageContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

// https://www.cipa.jp/std/documents/download_e.html?CIPA_DC-008-2026-E
public class JpgMetaParser {
    private static JpgMetaParser instance;

    private static final int SOI = 0xFFD8;
    private static final int APP1 = 0xFFE1;
    private static final int SOS = 0xFFDA;

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

    private JpgMetaParser() {

    }

    public static JpgMetaParser getInstance() {
        if (instance == null) {
            instance = new JpgMetaParser();
        }
        return instance;
    }

    public void parse(ImageContext context) {
        byte[] data;
        try {
            data = Files.readAllBytes(context.getPath());
        } catch (IOException e) {
            throw new ScorpionException("Could not read file: " + e.getMessage());
        }

        Map<ExifTag, String> metadata = new EnumMap<>(ExifTag.class);

        if (data.length < 2 || readShort(data, 0, false) != SOI) {
            throw new ScorpionException("Not a valid JPEG (missing SOI marker).");
        }

        int position = 2;

        while (position + 4 <= data.length) {
            int marker = readShort(data, position, false);

            if (marker == SOS) {
                break;
            }

            int segmentLength = readShort(data, position + 2, false);
            if (segmentLength < 2) {
                break;
            }

            if (marker == APP1) {
                int exifHeader = position + 4;
                if (isExifHeader(data, exifHeader)) {
                    int tiffBase = exifHeader + 6;
                    if (tiffBase + 8 > data.length) {
                        break;
                    }

                    boolean littleEndian;
                    int byteOrder = readShort(data, tiffBase, false);
                    if (byteOrder == 0x4949) {
                        littleEndian = true;
                    } else if (byteOrder == 0x4D4D) {
                        littleEndian = false;
                    } else {
                        break;
                    }

                    if (readShort(data, tiffBase + 2, littleEndian) != TIFF_MAGIC) {
                        position += 2 + segmentLength;
                        continue;
                    }

                    int ifd0Offset = readInt(data, tiffBase + 4, littleEndian);
                    parseIFD(data, tiffBase + ifd0Offset, tiffBase, littleEndian, metadata);
                    context.setMetadata(metadata);

                    break;
                }
            }
            position += 2 + segmentLength;
        }

        if (context.getMetadata() == null) {
            context.setMetadata(metadata);
        }
    }

    private void parseIFD(byte[] data,
                          int position,
                          int tiffBase,
                          boolean le,
                          Map<ExifTag, String> metadata) {
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
                int subOffset = readInt(data, valueFieldOffset, le);
                parseIFD(data, tiffBase + subOffset, tiffBase, le, metadata);
            } else if (tagId == TAG_GPS_IFD) {
                int gpsOffset = readInt(data, valueFieldOffset, le);
                parseIFD(data, tiffBase + gpsOffset, tiffBase, le, metadata);
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

    private String decodeValue(byte[] data,
                               int type,
                               int count,
                               int valueFieldOffset,
                               int tiffBase,
                               boolean le) {
        if (count < 0) {
            return null;
        }

        int typeSize = typeSize(type);
        if (typeSize == 0) {
            return null;
        }
        long totalSize = (long) typeSize * count;

        int dataOffset;
        if (totalSize <= 4) {
            dataOffset = valueFieldOffset;
        } else {
            dataOffset = tiffBase + readInt(data, valueFieldOffset, le);
        }
        if (dataOffset < 0 || dataOffset + totalSize > data.length) {
            return null;
        }

        switch (type) {
            case TYPE_ASCII: {
                int len = count;
                while (len > 0 && data[dataOffset + len - 1] == 0) {
                    len--;
                }
                return new String(data, dataOffset, len, StandardCharsets.US_ASCII).trim();
            }
            case TYPE_BYTE, TYPE_SHORT, TYPE_LONG, TYPE_SLONG: {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    int off = dataOffset + i * typeSize;
                    long v;
                    if (type == TYPE_BYTE) {
                        v = data[off] & 0xFF;
                    } else if (type == TYPE_SHORT) {
                        v = readShort(data, off, le);
                    } else {
                        v = readInt(data, off, le);
                    }
                    sb.append(v);
                }
                return sb.toString();
            }
            case TYPE_RATIONAL, TYPE_SRATIONAL: {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    int off = dataOffset + i * 8;
                    int numerator = readInt(data, off, le);
                    int denominator = readInt(data, off + 4, le);
                    sb.append(numerator).append('/').append(denominator);
                }
                return sb.toString();
            }
            case TYPE_UNDEFINED: {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count; i++) {
                    sb.append(String.format("%02X", data[dataOffset + i] & 0xFF));
                }
                return sb.toString();
            }
            default:
                return null;
        }
    }

    private int typeSize(int type) {
        return switch (type) {
            case TYPE_BYTE, TYPE_ASCII, TYPE_UNDEFINED -> 1;
            case TYPE_SHORT -> 2;
            case TYPE_LONG, TYPE_SLONG -> 4;
            case TYPE_RATIONAL, TYPE_SRATIONAL -> 8;
            default -> 0;
        };
    }

    private boolean isExifHeader(byte[] data, int pos) {
        if (pos + 6 > data.length) {
            return false;
        }
        return data[pos] == 'E' && data[pos + 1] == 'x' && data[pos + 2] == 'i'
                && data[pos + 3] == 'f' && data[pos + 4] == 0 && data[pos + 5] == 0;
    }

    private int readShort(byte[] data, int pos, boolean le) {
        if (pos < 0 || pos + 2 > data.length) {
            return 0;
        }
        if (le) {
            return (data[pos] & 0xFF) | ((data[pos + 1] & 0xFF) << 8);
        }
        return ((data[pos] & 0xFF) << 8) | (data[pos + 1] & 0xFF);
    }

    public int readInt(byte[] data, int pos, boolean le) {
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
