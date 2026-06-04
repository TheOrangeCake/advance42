package com.nguyen.spider;

import java.nio.file.Path;

public class ImageDownloader {
    private final Path path;

    public ImageDownloader(Path path) {
        this.path = path;
        // check PATH to save
            // if not exist -> create
            // if error -> throw ImageDownloadException
    }
    public void download(String url) {
        //
    }
}
