package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;
import com.nguyen.scorpion.parser.JpgMetaParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParserChain extends ChainAbstract {

    public void handle(ImageContext context) {
        readBasicAttributes(context);

        String extension = context.getExtension();
        switch (extension) {
            case "jpg", "jpeg" -> handleJpg(context);
            case "png" -> handlePng(context);
            case "gif" -> handleGif(context);
            case "bmp" -> handleBmp(context);
            default -> throw new ScorpionException("Unknown extension.");
        }

        if (this.next != null) {
            next.handle(context);
        }
    }

    private void readBasicAttributes(ImageContext context) {
        Path file = context.getPath();
        Map<String, String> attributes = new LinkedHashMap<>();

        attributes.put("FileName", String.valueOf(file.getFileName()));
        attributes.put("Format", context.getExtension());

        try {
            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
            attributes.put("FileSize", attr.size() + " bytes");
            attributes.put("Created", String.valueOf(attr.creationTime()));
            attributes.put("Modified", String.valueOf(attr.lastModifiedTime()));
            attributes.put("Accessed", String.valueOf(attr.lastAccessTime()));
        } catch (IOException e) {
            throw new ScorpionException("Could not read file attributes: " + e.getMessage());
        }

        context.setBasicAttributes(attributes);
    }

    private void handleJpg(ImageContext context) {
        JpgMetaParser.getInstance().parse(context);
    }

    private void handlePng(ImageContext context) {
    }

    private void handleGif(ImageContext context) {
        byte[] data;
        try {
            data = Files.readAllBytes(context.getPath());
        } catch (IOException e) {
            throw new ScorpionException("Could not read GIF file: " + e.getMessage());
        }
        if (data.length < 13) {
            return;
        }

        Map<String, String> attrs = context.getBasicAttributes();
        attrs.put("GIF Version", new String(data, 3, 3, StandardCharsets.US_ASCII));
        attrs.put("Width", String.valueOf((data[6] & 0xFF) | ((data[7] & 0xFF) << 8)));
        attrs.put("Height", String.valueOf((data[8] & 0xFF) | ((data[9] & 0xFF) << 8)));
        int packed = data[10] & 0xFF;
        boolean hasColorTable = (packed & 0x80) != 0;
        attrs.put("Global Color Table", hasColorTable ? (2 << (packed & 0x07)) + " colors" : "No");
    }

    private void handleBmp(ImageContext context) {
        JpgMetaParser parser = JpgMetaParser.getInstance();
        byte[] data;
        try {
            data = Files.readAllBytes(context.getPath());
        } catch (IOException e) {
            throw new ScorpionException("Could not read BMP file: " + e.getMessage());
        }
        if (data.length < 30) return;

        Map<String, String> attrs = context.getBasicAttributes();
        attrs.put("Width", String.valueOf(parser.readInt(data, 18, true)));
        attrs.put("Height", String.valueOf(Math.abs(parser.readInt(data, 22, true))));
        attrs.put("Bits Per Pixel", String.valueOf((data[28] & 0xFF) | ((data[29] & 0xFF) << 8)));
        attrs.put("Compression", bmpCompression(parser.readInt(data, 30, true)));
        if (data.length >= 46) {
            int xPpm = parser.readInt(data, 38, true);
            int yPpm = parser.readInt(data, 42, true);
            if (xPpm > 0) attrs.put("X Resolution", xPpm + " px/m");
            if (yPpm > 0) attrs.put("Y Resolution", yPpm + " px/m");
        }
    }

    private String bmpCompression(int value) {
        return switch (value) {
            case 0 -> "None";
            case 1 -> "RLE 8-bit";
            case 2 -> "RLE 4-bit";
            case 3 -> "Bit fields";
            case 4 -> "JPEG";
            case 5 -> "PNG";
            default -> "Unknown (" + value + ")";
        };
    }
}
