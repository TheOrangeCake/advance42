package com.nguyen.spider;

import com.nguyen.spider.exception.HttpException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

// https://www.baeldung.com/java-9-http-client
public class HttpHandler {
    private final List<String> urlList = new ArrayList<>();
    private final OptionConfig config;
    private final HttpClient client = HttpClient.newHttpClient();
    private static final Logger logger = LogManager.getLogger(HttpHandler.class);

    public HttpHandler(OptionConfig config) {
        this.config = config;
        urlList.add(config.getURL());
    }

    public void run() {
        for (int i = 0; i < config.getMax_depth(); i++) {
            // loop through urlList
                // send get request
                // receive response
                // check error -> throw exception
                // parse html with HtmlParser
                    // if image -> download with ImageDownloader
                    // if link -> add to urlList (add to a temp list, then copy to urlList cause looping urlList)
        }
    }

    private HttpResponse<String> sendRequest(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException e) {
            logger.error("Bad URL: {}", url);
            return null;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
