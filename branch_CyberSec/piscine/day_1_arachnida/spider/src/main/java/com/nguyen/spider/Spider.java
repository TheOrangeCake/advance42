package com.nguyen.spider;

import com.nguyen.spider.exception.ArgumentsParseException;
import com.nguyen.spider.exception.HttpException;
import com.nguyen.spider.exception.ImageDownloadException;
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

        OptionConfig config;
        try {
            config = new OptionConfig();
            config.parse(av);

            logger.info("CONFIG: r: {} | l: {} | p: {} | depth: {} | path: {} | url: {}",
                    config.isOptionR(),
                    config.isOptionL(),
                    config.isOptionP(),
                    config.getMax_depth(),
                    config.getPath().toString(),
                    config.getURL());
        } catch (ArgumentsParseException e) {
            logger.fatal("Parse Error.", e);
            return;
        }

        HtmlParser parser = new HtmlParser();
        ImageDownloader downloader;
        try {
            downloader = new ImageDownloader(config.getPath());
        } catch (ImageDownloadException e) {
            logger.fatal("Bad Path.", e);
            return;
        }
        HttpHandler handler = new HttpHandler(config, parser, downloader);
        try {
            handler.run();
        } catch (HttpException e) {
            logger.fatal("Http Error.", e);
            return;
        }

    }
}