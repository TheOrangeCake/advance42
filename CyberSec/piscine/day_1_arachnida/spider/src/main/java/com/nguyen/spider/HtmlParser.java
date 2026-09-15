package com.nguyen.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HtmlParser {

    public ParseResult parse(String html, String baseUrl) {
        Set<String> urls = new HashSet<>();
        Set<String> images = new HashSet<>();
        URI base = URI.create(baseUrl);
        String lowerHtml = html.toLowerCase();

        for (String href : extractAttribute(html, lowerHtml, "href")) {
            try {
                String resolved = base.resolve(href).toString();
                if (resolved.startsWith("http") && new URI(resolved).getHost().equals(base.getHost())) {
                    urls.add(resolved);
                }
            } catch (IllegalArgumentException | URISyntaxException ignored) {}
        }

        for (String src : extractAttribute(html, lowerHtml, "src")) {
            try {
                String resolved = base.resolve(src).toString();
                if (isImage(resolved)) {
                    images.add(resolved);
                }
            } catch (IllegalArgumentException ignored) {}
        }

        return new ParseResult(urls, images);
    }

    private List<String> extractAttribute(String html, String lowerHtml, String attr) {
        List<String> list = new ArrayList<>();
        for (char quote : new char[]{'"', '\''}) {
            String search = attr + "=" + quote;
            int i = lowerHtml.indexOf(search);
            while (i != -1) {
                int start = i + search.length();
                int end = lowerHtml.indexOf(quote, start);
                if (end == -1) {
                    i = lowerHtml.indexOf(search, start);
                    continue;
                }
                list.add(html.substring(start, end).replace("&amp;", "&"));
                i = lowerHtml.indexOf(search, end + 1);
            }
        }
        return list;
    }

    public boolean isImage(String url) {
        try {
            String path = new URI(url).getPath();
            if (path == null) {
                return false;
            }
            path = path.toLowerCase();
            return path.endsWith(".jpg") || path.endsWith(".jpeg")
                    || path.endsWith(".png") || path.endsWith(".gif")
                    || path.endsWith(".bmp");
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
