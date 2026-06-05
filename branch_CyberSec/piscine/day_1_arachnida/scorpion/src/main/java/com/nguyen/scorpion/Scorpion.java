package com.nguyen.scorpion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class Scorpion {
    private static final Logger logger = LogManager.getLogger(Scorpion.class);

    static void main(String[] av) {
        logger.info("Hello, this is Scorpion program.");

        if (av.length < 1) {
            logger.fatal("Invalid argument. Retry with at least 1 photo.");
            return;
        }

        ImageValidator reader = new ImageValidator();
        Set<Path> files = reader.validateFile(av);
        if (files.isEmpty()) {
            logger.fatal("Invalid argument. No valid image file.");
        }

        MetaDataParser parser = new MetaDataParser();
        Map<Path, Map<ExifTag, String>> parsedFiles = parser.parse(files);
    }
}