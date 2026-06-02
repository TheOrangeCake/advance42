package com.nguyen.spider;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.FileSystemNotFoundException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Getter
@Setter
public class OptionConfig {
    private String URL;
    private boolean option_r = false;
    private boolean option_l = false;
    private int max_depth = 1;
    private boolean option_p = false;
    private String defaultPath = "./data/";
    private Path path;
    private static final Logger logger = LogManager.getLogger(OptionConfig.class);

    public OptionConfig() {
        try {
            path = Path.of(defaultPath);
        } catch (InvalidPathException e) {
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

        String pending = null;
        for (int i = 0; i < av.length - 1; i++) {
            if (pending != null) {
                if (av[i].startsWith("-")) {
                    handlePendingDefault(pending);
                    pending = null;
                } else {
                    consumeArgument(pending, av[i]);
                    pending = null;
                    continue;
                }
            }

            if (av[i].startsWith("-")) {
                String flags = av[i].substring(1);
                if (flags.isEmpty()) {
                    throw new ArgumentsParseException("Invalid flag: '-'");
                }
                pending = parseOption(flags);
            } else {
                throw new ArgumentsParseException("Unexpected argument: '" + av[i] + "'. Argument-taking flags (-l, -p) must be last in a cluster.");
            }
        }

        if (pending != null) {
            handlePendingDefault(pending);
        }

        if (option_l && !option_r) {
            logger.warn("Flag l is specified without flag r, flag r enabled");
            option_r = true;
        }
    }

    private String parseOption(String avOption) {
        if (avOption == null) {
            throw new ArgumentsParseException("Invalid flag.");
        }

        char[] chars = avOption.toCharArray();
        String pending = null;

        for (int i = 0; i < chars.length; i++) {
            boolean isLast = (i == chars.length - 1);
            switch (chars[i]) {
                case 'r' -> option_r = true;
                case 'l' -> {
                    option_l = true;
                    max_depth = 5;
                    if (isLast) {
                        pending = "l";
                    }
                }
                case 'p' -> {
                    option_p = true;
                    if (isLast) {
                        pending = "p";
                    }
                }
                default -> throw new ArgumentsParseException("Unknown flag: '-" + chars[i] + "'.");
            }
        }
        return pending;
    }

    private void consumeArgument(String flag, String argument) {
        switch (flag) {
            case "l" -> parseMaxDepth(argument);
            case "p" -> parsePath(argument);
        }
    }

    private void handlePendingDefault(String flag) {
        switch (flag) {
            case "l" -> logger.info("-l with no depth provided, using default: {}.", max_depth);
            case "p" -> logger.info("-p with no path provided, using default: {}.", defaultPath);
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
        } catch (InvalidPathException e) {
            throw new ArgumentsParseException("Problem parsing PATH: " + providedPath);
        }
    }
}
