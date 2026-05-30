package com.nguyen.spider;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;

@Getter
@Setter
public class OptionConfig {
    private String URL;
    private boolean option_r = false;
    private boolean option_l = false;
    private int max_depth = 0;
    private boolean option_p = false;
    private String defaultPath = "./data/";
    private Path path;
    private static final Logger logger = LogManager.getLogger(OptionConfig.class);

    public OptionConfig() {
        try {
            path = Path.of(defaultPath);
        } catch (IllegalArgumentException | FileSystemNotFoundException | SecurityException e) {
            logger.error("Fail to access default path: {}", defaultPath, e);
            throw new ArgumentsParseException("Problem with default path: " + defaultPath);
        }
    }

    public void parse(String[] av) {
        if (av == null || av.length == 0) {
            throw new ArgumentsParseException("Please provide an URL.");
        }

        parseURL(av[av.length - 1]);

        if (av.length == 1) {
            return;
        }

        for (int i = 0; i < av.length - 2; i++) {
            if (av[i].startsWith("-")) {
                parseOption(av[i].substring(1));
                continue;
            } else {

            }
        }
        if (option_l && !option_r) {
            logger.warn("Flag l is specified without flag r, flag r enabled");
            option_r = true;
        }
    }

    private void parseOption(String avOption) {
        if (avOption == null) {
            throw new ArgumentsParseException("Invalid flag.");
        }
        for (char option : avOption.toCharArray()) {
            switch (option) {
                case 'r' -> option_r = true;
                case 'l' -> {
                    option_l = true;
                    max_depth = 5;
                }
                case 'p' -> {
                    option_p = true;
                }
                default -> {
                    logger.warn("Invalid flag {}, ignored.", option);
                }
            }
        }
    }

    private void parseURL(String providedUrl) {
        if (providedUrl.startsWith("https://") ||
                providedUrl.startsWith("http://") ||
                providedUrl.startsWith("www.")) {
            URL = providedUrl;
            return;
        }
        throw new ArgumentsParseException("Bad URL format. Must start with either \"https://\", \"http://\" or \"www.\".");
    }

    private void parseMaxDepth(String providedDepth) {
        try {
            max_depth = Integer.parseInt(providedDepth);
            if (max_depth < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ArgumentsParseException("Invalid -l flag parameter: " + providedDepth);
        }
    }

    private void parsePath(String providedPath) {
        try {
            path = Path.of(providedPath);
        } catch (IllegalArgumentException | FileSystemNotFoundException | SecurityException e) {
            logger.error("Fail to parse path: {}", providedPath, e);
            throw new ArgumentsParseException("Problem parsing PATH: " + providedPath);
        }
    }
}
