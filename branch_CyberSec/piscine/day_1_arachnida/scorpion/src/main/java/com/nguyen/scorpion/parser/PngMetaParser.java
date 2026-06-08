package com.nguyen.scorpion.parser;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ExifTag;
import com.nguyen.scorpion.model.ImageContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import static com.nguyen.scorpion.parser.EndianReader.readInt;
import static com.nguyen.scorpion.parser.EndianReader.readShort;

// https://www.w3.org/TR/png/
public class PngMetaParser {
    private static PngMetaParser instance;

    private PngMetaParser() {}

    public static PngMetaParser getInstance() {
        if (instance == null) {
            instance = new PngMetaParser();
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

        if (data.length < 8 || !isPngSignature(data)) {
            throw new ScorpionException("Not a valid PNG (bad signature).");
        }

        Map<ExifTag, String> metadata = new EnumMap<>(ExifTag.class);
        Map<String, String> attrs = context.getBasicAttributes();

        int pos = 8;
        outer:
        while (pos + 8 <= data.length) {
            int chunkLength = readInt(data, pos, false);
            String chunkType = new String(data, pos + 4, 4, StandardCharsets.US_ASCII);
            int dataStart = pos + 8;

            if (chunkLength < 0 || (long) dataStart + chunkLength > data.length) {
                break;
            }

            switch (chunkType) {
                case "IHDR" -> parseIhdr(data, dataStart, attrs);
                case "pHYs" -> parsePhys(data, dataStart, attrs);
                case "tIME" -> parseTime(data, dataStart, attrs);
                case "tEXt" -> parseTExt(data, dataStart, chunkLength, attrs);
                case "zTXt" -> parseZTxt(data, dataStart, chunkLength, attrs);
                case "iTXt" -> parseITxt(data, dataStart, chunkLength, attrs);
                case "eXIf" -> TiffParser.parse(data, dataStart, metadata);
                case "IEND" -> {
                    break outer;
                }
            }

            pos = dataStart + chunkLength + 4;
        }

        context.setMetadata(metadata);
    }

    private boolean isPngSignature(byte[] data) {
        return (data[0] & 0xFF) == 0x89
                && data[1] == 'P' && data[2] == 'N' && data[3] == 'G'
                && data[4] == '\r' && data[5] == '\n'
                && (data[6] & 0xFF) == 0x1A && data[7] == '\n';
    }

    private void parseIhdr(byte[] data, int pos, Map<String, String> attrs) {
        if (pos + 13 > data.length) {
            return;
        }

        attrs.put("Width", String.valueOf(readInt(data, pos, false)));
        attrs.put("Height", String.valueOf(readInt(data, pos + 4, false)));
        attrs.put("BitDepth", String.valueOf(data[pos + 8] & 0xFF));
        attrs.put("ColorType", colorTypeName(data[pos + 9] & 0xFF));
        attrs.put("Interlace", (data[pos + 12] & 0xFF) == 1 ? "Adam7" : "None");
    }

    private void parsePhys(byte[] data, int pos, Map<String, String> attrs) {
        if (pos + 9 > data.length) {
            return;
        }

        int xPpu = readInt(data, pos, false);
        int yPpu = readInt(data, pos + 4, false);
        String unit = (data[pos + 8] & 0xFF) == 1 ? " px/m" : " px/unit";

        if (xPpu > 0) {
            attrs.put("XResolution", xPpu + unit);
        }
        if (yPpu > 0) {
            attrs.put("YResolution", yPpu + unit);
        }
    }

    private void parseTime(byte[] data, int pos, Map<String, String> attrs) {
        if (pos + 7 > data.length) {
            return;
        }

        int year  = readShort(data, pos, false);
        int month = data[pos + 2] & 0xFF;
        int day   = data[pos + 3] & 0xFF;
        int hour  = data[pos + 4] & 0xFF;
        int min   = data[pos + 5] & 0xFF;
        int sec   = data[pos + 6] & 0xFF;

        attrs.put("LastModified", String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, min, sec));
    }

    private void parseTExt(byte[] data, int pos, int length, Map<String, String> attrs) {
        int nullPos = findNull(data, pos, length);
        if (nullPos < 0) {
            return;
        }

        String keyword = new String(data, pos, nullPos - pos, StandardCharsets.ISO_8859_1);

        int textStart = nullPos + 1;
        int textLen = length - (textStart - pos);
        if (textLen < 0) {
            return;
        }

        attrs.put(keyword, new String(data, textStart, textLen, StandardCharsets.ISO_8859_1));
    }

    private void parseZTxt(byte[] data, int pos, int length, Map<String, String> attrs) {
        int nullPos = findNull(data, pos, length);
        if (nullPos < 0 || nullPos + 1 >= pos + length) {
            return;
        }

        String keyword = new String(data, pos, nullPos - pos, StandardCharsets.ISO_8859_1);
        int compressedStart = nullPos + 2;
        int compressedLen = length - (compressedStart - pos);
        if (compressedLen <= 0) {
            return;
        }

        String value = inflate(data, compressedStart, compressedLen);
        if (value != null) {
            attrs.put(keyword, value);
        }
    }

    private void parseITxt(byte[] data, int pos, int length, Map<String, String> attrs) {
        int end = pos + length;
        int nullPos = findNull(data, pos, length);
        if (nullPos < 0) {
            return;
        }

        String keyword = new String(data, pos, nullPos - pos, StandardCharsets.UTF_8);
        int cursor = nullPos + 1;
        if (cursor + 2 > end) {
            return;
        }
        int compressionFlag = data[cursor] & 0xFF;
        cursor += 2;

        int langEnd = findNull(data, cursor, end - cursor);
        if (langEnd < 0) {
            return;
        }
        cursor = langEnd + 1;

        int tkEnd = findNull(data, cursor, end - cursor);
        if (tkEnd < 0) {
            return;
        }
        cursor = tkEnd + 1;

        int textLen = end - cursor;
        if (textLen < 0) {
            return;
        }

        String value = compressionFlag == 1
                ? inflate(data, cursor, textLen)
                : new String(data, cursor, textLen, StandardCharsets.UTF_8);

        if (value != null) {
            attrs.put(keyword, value);
        }
    }

    private int findNull(byte[] data, int start, int length) {
        for (int i = start; i < start + length && i < data.length; i++) {
            if (data[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    private String inflate(byte[] data, int start, int length) {
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(data, start, length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (!inflater.finished()) {
                int n = inflater.inflate(buf);
                if (n == 0) {
                    break;
                }
                baos.write(buf, 0, n);
            }
            return baos.toString(StandardCharsets.UTF_8);
        } catch (DataFormatException e) {
            return null;
        } finally {
            inflater.end();
        }
    }

    private String colorTypeName(int type) {
        return switch (type) {
            case 0 -> "Grayscale";
            case 2 -> "RGB";
            case 3 -> "Indexed";
            case 4 -> "Grayscale+Alpha";
            case 6 -> "RGBA";
            default -> "Unknown (" + type + ")";
        };
    }
}
