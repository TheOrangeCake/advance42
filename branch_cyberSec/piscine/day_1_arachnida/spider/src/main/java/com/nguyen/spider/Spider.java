package com.nguyen.spider;

import com.nguyen.spider.exception.ArgumentsParseException;
import com.nguyen.spider.exception.ImageDownloadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;

public class Spider {
    private static final Logger logger = LogManager.getLogger(Spider.class);
    public static Path defaultPath;

    static void main(String[] av) {
        logger.info("Hello, this is Spider program.");

        if (av.length < 1) {
            logger.fatal("Invalid argument. Retry with arguments: [-rlp] [params] URL.");
            return;
        }

        String path = "./data/";
        try {
            defaultPath = Path.of(path);
            Files.createDirectories(defaultPath);
        } catch (IOException | InvalidPathException e) {
            logger.fatal("Problem with default path: {}", path);
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

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
        HtmlParser parser = new HtmlParser();
        ImageDownloader downloader;
        try {
            downloader = new ImageDownloader(config.getPath(), client);
        } catch (ImageDownloadException e) {
            logger.fatal("Bad Path.", e);
            return;
        }
        Crawler handler = new Crawler(config, client, parser, downloader);
        handler.run();
        handler.close();
    }
}