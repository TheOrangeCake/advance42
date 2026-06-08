package com.nguyen.spider;

import com.nguyen.spider.exception.ImageDownloadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class ImageDownloader {
    private final Path path;
    private final HttpClient client;
    private final Set<String> downloadedUrls = new HashSet<>();
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

    public void download(Set<String> imageList) {
        for (String imageUrl : imageList) {
            if (downloadedUrls.contains(imageUrl)) {
                continue;
            }

            logger.info("Downloading: {}", imageUrl);
            downloadedUrls.add(imageUrl);

            try {
                String imageName = extractImageName(imageUrl);
                if (imageName == null) {
                    throw new ImageDownloadException("Bad Url");
                }

                Path savePath = path.resolve(imageName);

                HttpResponse<byte[]> data = sendImageRequest(imageUrl);

                if (data.statusCode() != 200) {
                    logger.warn("Bad status {}: {}", data.statusCode(), imageUrl);
                    continue;
                }

                Files.write(savePath, data.body());

            } catch (URISyntaxException | ImageDownloadException e) {
                logger.warn("Bad Url: {}", imageUrl);
            } catch (HttpConnectTimeoutException e) {
                logger.warn("Request timeout: {}", imageUrl);
            } catch (HttpTimeoutException e) {
                logger.warn("Response timeout: {}", imageUrl);
            } catch (IOException | InterruptedException e) {
                logger.fatal("Fail to process request or image.", e);
                throw new ImageDownloadException(e.getMessage());
            }
        }
    }

    private HttpResponse<byte[]> sendImageRequest(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private String extractImageName(String imageUrl) throws URISyntaxException {
        String path = new URI(imageUrl).getPath();
        int start = path.lastIndexOf("/");
        if (start == -1) {
            return null;
        }
        return path.substring(start + 1);
    }
}
