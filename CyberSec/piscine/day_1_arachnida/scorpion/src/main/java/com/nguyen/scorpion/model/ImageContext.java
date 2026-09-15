package com.nguyen.scorpion.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Map;

@Setter
@Getter
public class ImageContext {
    private final Path path;
    private String extension;
    private Map<ExifTag, String> metadata;
    private Map<String, String> basicAttributes;

    public ImageContext(Path path) {
        this.path = path;
    }
}
