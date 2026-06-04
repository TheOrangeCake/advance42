package com.nguyen.spider;

import com.nguyen.spider.exception.ImageDownloadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImageDownloader {
    private final Path path;
    private final HttpClient client;
    private static final Logger logger = LogManager.getLogger(ImageDownloader.class);

    public ImageDownloader(Path path, HttpClient client) {
        this.client = client;
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new ImageDownloadException(e.getMessage());
        }
        this.path = path;
    }

    public void download(List<String> imageList) {
        // loop through the imageList
            // download image through client sendImageRequest
    }

    private HttpResponse<byte[]> sendImageRequest(String url) throws IOException, InterruptedException, URISyntaxException {

    }
}
