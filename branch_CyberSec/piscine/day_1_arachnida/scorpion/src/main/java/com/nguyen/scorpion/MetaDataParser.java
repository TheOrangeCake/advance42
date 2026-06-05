package com.nguyen.scorpion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetaDataParser {
    private static final Logger logger = LogManager.getLogger(MetaDataParser.class);

    public Map<Path, Map<ExifTag, String>> parse(Set<Path> files) {
            Map<Path, Map<ExifTag, String>> parsedFiles = new HashMap<>();
            for (Path file : files) {

            }
    }

    private Map<ExifTag, String> extractExif(byte[] data) {

    }
}
