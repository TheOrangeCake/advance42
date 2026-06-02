package com.nguyen.spider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spider {
    private static final Logger logger = LogManager.getLogger(Spider.class);

    static void main(String[] av) {
        logger.info("Hello, this is Spider program.");

        if (av.length < 1) {
            logger.fatal("Invalid argument. Retry with arguments: [-rlp] [params] URL.");
            return;
        }

        OptionConfig config = null;
        try {
            config = new OptionConfig();
            config.parse(av);

            logger.info("CONFIG: r: {} | l: {} | p: {} | depth: {} | path: {} | url: {}", config.isOption_r(), config.isOption_l(), config.isOption_p(), config.getMax_depth(), config.getPath().toString(), config.getURL());
        } catch (ArgumentsParseException e) {
            logger.fatal("Parse Error.", e);
            return;
        }
    }
}