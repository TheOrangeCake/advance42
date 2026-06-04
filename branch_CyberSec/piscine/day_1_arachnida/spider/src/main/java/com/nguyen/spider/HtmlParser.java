package com.nguyen.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class HtmlParser {

    public ParseResult parse(String html, String baseUrl) {
        List<String> urls = new ArrayList<>();
        List<String> images = new ArrayList<>();
        URI base = URI.create(baseUrl);

        for (String href : extractAttribute(html, "href")) {
            try {
                String resolved = base.resolve(href).toString();
                if (resolved.startsWith("http") && new URI(resolved).getHost().equals(base.getHost()))
                    urls.add(resolved);
            } catch (IllegalArgumentException | URISyntaxException ignored) {}
        }

        for (String src : extractAttribute(html, "src")) {
            try {
                String resolved = base.resolve(src).toString();
                if (isImage(resolved))
                    images.add(resolved);
            } catch (IllegalArgumentException ignored) {}
        }

        return new ParseResult(urls, images);
    }

    private List<String> extractAttribute(String html, String attr) {
        List<String> list = new ArrayList<>();
        String search = attr + "=\"";
        int i = html.indexOf(search);
        while (i != -1) {
            int start = i + search.length();
            int end = html.indexOf('"', start);
            if (end != -1)
                list.add(html.substring(start, end));
            i = html.indexOf(search, start);
        }
        return list;
    }

    private boolean isImage(String url) {
        try {
            String path = new URI(url).getPath().toLowerCase();
            return path.endsWith(".jpg") || path.endsWith(".jpeg")
                    || path.endsWith(".png") || path.endsWith(".gif")
                    || path.endsWith(".bmp");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
