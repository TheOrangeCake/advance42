package com.nguyen.scorpion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ImageValidator {
    private static final Logger logger = LogManager.getLogger(ImageValidator.class);

    public Set<Path> validateFile(String[] av) {
        Set<Path> fileList = new HashSet<>();
        for (String arg : av) {
            Path path = Paths.get(arg);
            if (!isImage(path)) {
                logger.warn("{} is not a supported image format, skipping.", arg);
                continue;
            }
            if (!Files.exists(path)) {
                logger.warn("{} does not exist, skipping.", arg);
                continue;
            }
            if (!Files.isReadable(path)) {
                logger.warn("{} is not readable, skipping.", arg);
                continue;
            }
            fileList.add(path);
        }
        return fileList;
    }

    private boolean isImage(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg")
                || name.endsWith(".png") || name.endsWith(".gif")
                || name.endsWith(".bmp");
    }
}
