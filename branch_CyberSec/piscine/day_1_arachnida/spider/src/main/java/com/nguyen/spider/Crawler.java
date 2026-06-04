package com.nguyen.spider;

import com.nguyen.spider.exception.ImageDownloadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// https://www.baeldung.com/java-9-http-client
public class Crawler {
    private List<String> currentUrlList = new ArrayList<>();
    private final Set<String> visitedUrls = new HashSet<>();
    private final OptionConfig config;
    private final HtmlParser parser;
    private final ImageDownloader downloader;
    private final HttpClient client;
    private static final Logger logger = LogManager.getLogger(Crawler.class);

    public Crawler(OptionConfig config, HttpClient client, HtmlParser parser, ImageDownloader downloader) {
        this.config = config;
        currentUrlList.add(config.getURL());
        visitedUrls.add(config.getURL());
        this.client = client;
        this.parser = parser;
        this.downloader = downloader;
    }

    public void run() {
        for (int i = 0; i < config.getMax_depth(); i++) {
            int listSize = currentUrlList.size();
            List<String> nextUrlList = new ArrayList<>();
            int count = 1;
            for (String url : currentUrlList) {
                logger.info("[Depth {}/{}] Handling link {}/{} : {}", i + 1, config.getMax_depth(), count, listSize, url);
                count++;
                try {
                    HttpResponse<String> response = sendUrlRequest(url);
                    if (response.statusCode() != 200) {
                        logger.warn("Request not OK: {}.", response.statusCode());
                        continue;
                    }

                    ParseResult result = parser.parse(response.body(), url);
                    if (result.urlList().isEmpty() && result.imageList().isEmpty()) {
                        logger.info("No link nor image.");
                        continue;
                    }

                    if (!result.urlList().isEmpty()) {
                        for (String candidate : result.urlList()) {
                            if (!visitedUrls.contains(candidate)) {
                                visitedUrls.add(candidate);
                                nextUrlList.add(candidate);
                            }
                        }
                    } else {
                        logger.info("No additional link.");
                    }

                    if (!result.imageList().isEmpty()) {
                        downloader.download(result.imageList());
                    } else {
                        logger.info("No image to download.");
                    }
                } catch (ImageDownloadException e) {
                    return;
                } catch (URISyntaxException e) {
                    logger.error("Bad URL: {}", url);
                } catch (HttpConnectTimeoutException e) {
                    logger.warn("Request timeout: {}", url);
                } catch (HttpTimeoutException e) {
                    logger.warn("Response timeout: {}", url);
                } catch (IOException | InterruptedException e) {
                    logger.fatal("Fail to send request.", e);
                    return;
                }
            }

            currentUrlList = nextUrlList;

            if (currentUrlList.isEmpty()) {
                logger.info("No more link, end before max depth.");
                return;
            }
        }
    }

    private HttpResponse<String> sendUrlRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void close() {
        if (!client.isTerminated()) {
            client.close();
        }
    }
}
