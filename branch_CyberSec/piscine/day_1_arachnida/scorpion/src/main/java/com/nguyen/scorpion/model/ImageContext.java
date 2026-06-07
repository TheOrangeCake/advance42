package com.nguyen.scorpion.model;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.Map;

@Setter
@Getter
public class ImageContext {
    private final Path path;
    private Map<ExifTag, String> metadata;

    public ImageContext(Path path) {
        this.path = path;
    }
}
