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
            if (!config.isOption_r()) {
                logger.debug("HEY");
            }
        } catch (ArgumentsParseException e) {
            logger.fatal("Parse Error.", e);
            return;
        }
    }
}