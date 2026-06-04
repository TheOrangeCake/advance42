package com.nguyen.spider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// https://www.baeldung.com/java-9-http-client
public class HttpHandler {
    private List<String> currentUrlList = new ArrayList<>();
    private final OptionConfig config;
    private final HtmlParser parser;
    private final ImageDownloader downloader;
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final Logger logger = LogManager.getLogger(HttpHandler.class);

    public HttpHandler(OptionConfig config, HtmlParser parser, ImageDownloader downloader) {
        this.config = config;
        currentUrlList.add(config.getURL());
        this.parser = parser;
        this.downloader = downloader;
    }

    public void run() {
        for (int i = 0; i < config.getMax_depth(); i++) {
            int listSize = currentUrlList.size();
            List<String> nextUrlList = new ArrayList<>();
            for (String url : currentUrlList) {
                logger.info("Handling {} / {} : {}", i, listSize, url);
                try {
                    HttpResponse<String> response = sendRequest(url);
                    if (response == null ) {
                        continue;
                    }
                    if (response.statusCode() != 200) {
                        logger.warn("Request not OK: {}", url);
                        continue;
                    }
                    // TODO: handle downloadList somewhere
                    List<String> newUrlList = parser.parse(response.body(), downloader);
                    if (newUrlList.isEmpty()) {
                        logger.info("End early, no more link.");
                        break;
                    }
                    nextUrlList.addAll(newUrlList);

                } catch (HttpConnectTimeoutException e) {
                    logger.warn("Request timeout: {}", url);
                } catch (IOException | InterruptedException e) {
                    logger.fatal("Fail to send request.", e);
                    System.exit(-1);
                }
            }
            currentUrlList = nextUrlList;
            // loop through currentUrlList
                // send get request
                // receive response
                // check error -> throw exception
                // parse html with HtmlParser
                    // if image -> download with ImageDownloader
                    // if link -> add to currentUrlList (add to a temp list, then copy to currentUrlList cause looping currentUrlList)
        }
    }

    private HttpResponse<String> sendRequest(String url) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            logger.error("Bad URL: {}", url);
            return null;
        }
    }

    private void close() {
        if (!client.isTerminated()) {
            client.close();
        }
    }
}
