package com.nguyen.spider;

import com.nguyen.spider.exception.HttpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

// https://www.baeldung.com/java-9-http-client
public class HttpHandler {
    private OptionConfig config;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final Logger logger = LogManager.getLogger(HttpHandler.class);

    public HttpHandler( OptionConfig config) {
        this.config = config;
    }

    public void run() {
        CompletableFuture<HttpResponse<String>> firstResponse = sendRequest(config.getURL());
        if (firstResponse == null) {
            throw new HttpException("Provided URL is invalid");
        }
        // Parse the html for list of link and list of image
        // download image
        // recursive fetch links

        for (int i = 1; i < config.getMax_depth(); i++) {

        }
    }

    private CompletableFuture<HttpResponse<String>> sendRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            logger.error("Bad URL: {}", url);
            return null;
        }
    }
}
