package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;
import com.nguyen.scorpion.parser.EndianReader;
import com.nguyen.scorpion.parser.JpgMetaParser;
import com.nguyen.scorpion.parser.PngMetaParser;

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
        PngMetaParser.getInstance().parse(context);
    }

    // https://en.wikipedia.org/wiki/GIF
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
        if (!new String(data, 0, 3, StandardCharsets.US_ASCII).equals("GIF")) {
            throw new ScorpionException("Not GIF file");
        }
        attrs.put("GIF Version", new String(data, 3, 3, StandardCharsets.US_ASCII));
        attrs.put("Width", String.valueOf(EndianReader.readShort(data, 6, true)));
        attrs.put("Height", String.valueOf(EndianReader.readShort(data, 8, true)));
        int packed = data[10] & 0xFF;
        boolean hasColorTable = (packed & 0x80) != 0;
        attrs.put("Global Color Table", hasColorTable ? (2 << (packed & 0x07)) + " colors" : "No");
    }

    // https://upload.wikimedia.org/wikipedia/commons/7/75/BMPfileFormat.svg
    private void handleBmp(ImageContext context) {
        byte[] data;
        try {
            data = Files.readAllBytes(context.getPath());
        } catch (IOException e) {
            throw new ScorpionException("Could not read BMP file: " + e.getMessage());
        }
        if (data.length < 30) {
            return;
        }

        Map<String, String> attrs = context.getBasicAttributes();
        if (!new String(data, 0, 2, StandardCharsets.US_ASCII).equals("BM")) {
            throw new ScorpionException("Not BMP file");
        }
        attrs.put("Width", String.valueOf(EndianReader.readInt(data, 18, true)));
        attrs.put("Height", String.valueOf(Math.abs(EndianReader.readInt(data, 22, true))));
        attrs.put("Bits Per Pixel", String.valueOf(EndianReader.readShort(data, 28, true)));
        attrs.put("Compression", bmpCompression(EndianReader.readInt(data, 30, true)));
        if (data.length >= 46) {
            int xPpm = EndianReader.readInt(data, 38, true);
            int yPpm = EndianReader.readInt(data, 42, true);
            if (xPpm > 0) {
                attrs.put("X Resolution", xPpm + " px/m");
            }
            if (yPpm > 0) {
                attrs.put("Y Resolution", yPpm + " px/m");
            }
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
