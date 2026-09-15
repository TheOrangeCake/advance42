package com.nguyen.scorpion.parser;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ExifTag;
import com.nguyen.scorpion.model.ImageContext;

import java.io.IOException;
import java.nio.file.Files;
import java.util.EnumMap;
import java.util.Map;

import static com.nguyen.scorpion.parser.EndianReader.readShort;

// https://www.cipa.jp/std/documents/download_e.html?CIPA_DC-008-2026-E
public class JpgMetaParser {
    private static JpgMetaParser instance;

    private static final int SOI = 0xFFD8;
    private static final int APP1 = 0xFFE1;
    private static final int SOS = 0xFFDA;

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
                    TiffParser.parse(data, exifHeader + 6, metadata);
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

    private boolean isExifHeader(byte[] data, int pos) {
        if (pos + 6 > data.length) {
            return false;
        }
        return data[pos] == 'E' && data[pos + 1] == 'x' && data[pos + 2] == 'i'
                && data[pos + 3] == 'f' && data[pos + 4] == 0 && data[pos + 5] == 0;
    }
}
