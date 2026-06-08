package com.nguyen.scorpion.chain;

import com.nguyen.scorpion.exception.ScorpionException;
import com.nguyen.scorpion.model.ImageContext;
import com.nguyen.scorpion.parser.JpgMetaParser;

import java.io.IOException;
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
        Path file = context.getPath();
    }

    private void handleGif(ImageContext context) {
        Path file = context.getPath();
    }

    private void handleBmp(ImageContext context) {
        Path file = context.getPath();
    }
}
